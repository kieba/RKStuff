package com.rk.rkstuff.solar.tile;

import com.rk.rkstuff.coolant.CoolantStack;
import com.rk.rkstuff.core.tile.IMultiBlockMasterListener;
import com.rk.rkstuff.core.tile.TileMultiBlockMaster;
import com.rk.rkstuff.core.tile.TileRK;
import com.rk.rkstuff.helper.CCHelper;
import com.rk.rkstuff.helper.MultiBlockHelper;
import com.rk.rkstuff.solar.block.BlockSolarInput;
import com.rk.rkstuff.solar.block.BlockSolarMaster;
import com.rk.rkstuff.solar.block.BlockSolarOutput;
import com.rk.rkstuff.solar.block.ISolarBlock;
import com.rk.rkstuff.util.Pos;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import rk.com.core.io.IOStream;

import java.io.IOException;
import java.util.LinkedList;

@Optional.InterfaceList({
        @Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = "ComputerCraft")
})
public class TileSolarMaster extends TileMultiBlockMaster implements IPeripheral {

    private static CCHelper.CCMethods METHODS = new CCHelper.CCMethods();
    static {
        METHODS.add(new CCHelper.CCMethodDoc(METHODS));
        METHODS.add(new CCMethodGetMaxCoolCoolant());
        METHODS.add(new CCMethodGetMaxHotCoolant());
        METHODS.add(new CCMethodGetProduction());
    }

    private static float SCALE = 1.75f;
    private static float MAX_SOLAR_TEMPERATURE = 773.15f;
    private static int MAX_MB_PER_PANEL = 100;
    private static int BUFFER_MB_PER_PANEL = 1000;

    private int countSolarPanels;
    private int countSolarPanelsWithSky = 0;
    private CoolantStack coolantBuffer = new CoolantStack();
    private int coolantBufferMax = 0;
    private double productionLastTick = 0;

    private LinkedList<TileRK> outputTrigger = new LinkedList<TileRK>();

    private int tick = 0;

    @Override
    public boolean checkMultiBlockForm() {
        return computeMultiStructureBounds() != null;
    }


    @Override
    protected MultiBlockHelper.Bounds setupStructure() {
        MultiBlockHelper.Bounds tmpBounds = computeMultiStructureBounds();
        for(MultiBlockHelper.Bounds.BlockIterator.BoundsPos pos : tmpBounds){
            boolean hasNorth = pos.hasNeighbourBlock(ForgeDirection.NORTH);
            boolean hasEast = pos.hasNeighbourBlock(ForgeDirection.EAST);
            boolean hasSouth = pos.hasNeighbourBlock(ForgeDirection.SOUTH);
            boolean hasWest = pos.hasNeighbourBlock(ForgeDirection.WEST);

            int meta = 0;
            meta |= ((hasNorth ? 1 : 0) << (ForgeDirection.NORTH.ordinal() - 2));
            meta |= ((hasEast ? 1 : 0) << (ForgeDirection.EAST.ordinal() - 2));
            meta |= ((hasSouth ? 1 : 0) << (ForgeDirection.SOUTH.ordinal() - 2));
            meta |= ((hasWest ? 1 : 0) << (ForgeDirection.WEST.ordinal() - 2));

            worldObj.setBlockMetadataWithNotify(pos.x, pos.y, pos.z, meta, 2);

            //Update TileIOBlocks
            Block targetBlock = worldObj.getBlock(pos.x, pos.y, pos.z);
            if (targetBlock instanceof BlockSolarInput || targetBlock instanceof BlockSolarOutput) {
                IMultiBlockMasterListener masterListener = (IMultiBlockMasterListener) worldObj.getTileEntity(pos.x, pos.y, pos.z);
                masterListener.registerMaster(this);
            }
        }
        countSolarPanels = tmpBounds.getWidthX() * tmpBounds.getWidthZ();
        coolantBufferMax = countSolarPanels * BUFFER_MB_PER_PANEL;
        return tmpBounds;
    }

    public void addOutputTrigger(TileRK entity) {
        outputTrigger.add(entity);
    }

    private void triggerOutput() {
        for (TileRK outTile : outputTrigger) {
            outTile.updateEntityByMaster();
        }
    }

    private MultiBlockHelper.Bounds computeMultiStructureBounds(){
        MultiBlockHelper.Bounds tmpBounds = new MultiBlockHelper.Bounds(xCoord, yCoord, zCoord);
        for(ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS){
            if(direction == ForgeDirection.UP) continue;
            if(direction == ForgeDirection.DOWN) continue;

            int i = 0;
            while (isValidMultiblock(xCoord + direction.offsetX * i, yCoord  + direction.offsetY * i, zCoord  + direction.offsetZ * i)) {
                i++;
            }
            i--;
            tmpBounds.add(xCoord + direction.offsetX * i, yCoord  + direction.offsetY * i, zCoord  + direction.offsetZ * i);
    }

        boolean isValid = true;
        for (int x = tmpBounds.getMinX() - 1; x <= tmpBounds.getMaxX() + 1; x++) {
            for (int z = tmpBounds.getMinZ() - 1; z <= tmpBounds.getMaxZ() + 1; z++) {

                if (x == (tmpBounds.getMinX() - 1) || x == (tmpBounds.getMaxX() + 1)
                        || z == (tmpBounds.getMinZ() - 1) || z == (tmpBounds.getMaxZ() + 1)) {
                    if (isValidMultiblock(x, yCoord, z)) {
                        isValid = false;
                        break;
                    }
                } else {
                    if (!isValidMultiblock(x, yCoord, z)) {
                        isValid = false;
                        break;
                    }
                    if (!new Pos(x, yCoord, z).equals(getPosition()) && isMasterBlock(x, yCoord, z)) {
                        isValid = false;
                        break;
                    }
                }
            }
            if(!isValid) break;
        }

        if(isValid){
            return tmpBounds;
        }else {
            return null;
        }
    }

    @Override
    public void resetStructure() {
        if(bounds != null){
            for(Pos pos : bounds){
                worldObj.setBlockMetadataWithNotify(pos.x, pos.y, pos.z, 0, 2);
                Block targetBlock = worldObj.getBlock(pos.x, pos.y, pos.z);
                if (targetBlock instanceof BlockSolarInput || targetBlock instanceof BlockSolarOutput) {
                    IMultiBlockMasterListener masterListener = (IMultiBlockMasterListener) worldObj.getTileEntity(pos.x, pos.y, pos.z);
                    masterListener.unregisterMaster();
                }
            }
        }
        outputTrigger.clear();
    }

    public CoolantStack getCoolantBuffer() {
        return coolantBuffer;
    }

    public int getCountCurrentSkySeeingSolarPanel() {
        if (tick % 20 == 0) {
            countSolarPanelsWithSky = 0;
            for (Pos pos : bounds) {
                if (worldObj.canBlockSeeTheSky(pos.x, pos.y + 1, pos.z)) {
                    countSolarPanelsWithSky++;
                }
            }
        }
        return countSolarPanelsWithSky;
    }


    @Override
    protected void updateMaster() {
        tick++;
        if (coolantBuffer.getTemperature() < MAX_SOLAR_TEMPERATURE) {
            float energy = SCALE * getCountCurrentSkySeeingSolarPanel() * getCurrentProductionPerSolar();// (0.0 - 1.0) * countSolarWithSky
            productionLastTick = energy;

            coolantBuffer.addEnergy(energy);

            if (coolantBuffer.getTemperature() > MAX_SOLAR_TEMPERATURE) {
                coolantBuffer.set(coolantBuffer.getAmount(), MAX_SOLAR_TEMPERATURE);
            }
        }

        triggerOutput();

        if (tick % 20 == 0) {
            tick = 0;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        coolantBuffer.readFromNBT("coolantBuffer", data);
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        coolantBuffer.writeToNBT("coolantBuffer", data);
    }

    private boolean isValidMultiblock(int x, int y, int z){
        Block block = worldObj.getBlock(x, y, z);
        return block instanceof ISolarBlock;
    }

    private boolean isMasterBlock(int x, int y, int z) {
        Block block = worldObj.getBlock(x, y, z);
        return block instanceof BlockSolarMaster;
    }


    public int getMaxTankCapacity() {
        return BUFFER_MB_PER_PANEL * countSolarPanels;
    }


    public double getProductionLastTick() {
        return productionLastTick;
    }

    public double getProductionMaximal() {
        return MAX_MB_PER_PANEL * countSolarPanels * 1000;
    }

    private float getCurrentProductionPerSolar() {
        long time = worldObj.getWorldTime();
        time += 2000;
        time %= 24000;

        if (time <= 16000) {
            return (float) (0.42 - 0.5 * Math.cos(2 * Math.PI * time / 16000) + 0.08 * Math.cos(4 * Math.PI * time / 16000)) * MAX_MB_PER_PANEL;
        } else {
            return 0;
        }
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    public void writeData(IOStream data) {
        data.writeLast(countSolarPanels);
        data.writeLast(getProductionLastTick());
        coolantBuffer.writeData(data);
    }

    @Override
    public void readData(IOStream data) throws IOException {
        countSolarPanels = data.readFirstInt();
        productionLastTick = data.readFirstDouble();
        coolantBuffer.readData(data);
        coolantBufferMax = countSolarPanels * BUFFER_MB_PER_PANEL;
    }

    @Override
    public String getType() {
        return Reference.TILE_SOLAR_MASTER;
    }

    public int receiveCoolant(ForgeDirection from, int maxAmount, float temperature, boolean simulate) {
        maxAmount = Math.min(maxAmount, coolantBufferMax - coolantBuffer.getAmount());
        maxAmount = Math.min(maxAmount, MAX_MB_PER_PANEL * countSolarPanels);
        if (!simulate) {
            coolantBuffer.add(maxAmount, temperature);
        }
        return maxAmount;
    }

    @Override
    @Optional.Method(modid = "ComputerCraft")
    public String[] getMethodNames() {
        return METHODS.getMethodNames();
    }

    @Override
    @Optional.Method(modid = "ComputerCraft")
    public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
        return METHODS.execute(method, computer, context, arguments, this);
    }

    @Override
    @Optional.Method(modid = "ComputerCraft")
    public void attach(IComputerAccess computer) {

    }

    @Override
    @Optional.Method(modid = "ComputerCraft")
    public void detach(IComputerAccess computer) {

    }

    @Override
    @Optional.Method(modid = "ComputerCraft")
    public boolean equals(IPeripheral other) {
        if(other == null) return false;
        return this.hashCode() == other.hashCode();
    }

    private static class CCMethodGetMaxCoolCoolant implements CCHelper.ICCMethod<TileSolarMaster> {

        @Override
        public String getMethodName() {
            return "getMaxCoolCoolant";
        }

        @Override
        public String getMethodDescription() {
            return "\tReturns the maximum storage of fluidCoolant[mB].\n\tUsage: getMaxCoolCoolant();";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileSolarMaster tile) throws LuaException {
            if(arguments == null || arguments.length != 0) {
                throw CCHelper.INVALID_ARGUMENT_EXCEPTION;
            }
            return new Object[] { tile.getMaxTankCapacity() };
        }
    }

    private static class CCMethodGetMaxHotCoolant implements CCHelper.ICCMethod<TileSolarMaster> {

        @Override
        public String getMethodName() {
            return "getMaxHotCoolant";
        }

        @Override
        public String getMethodDescription() {
            return "\tReturns the maximum storage of fluidUsedCoolant[mB].\n\tUsage: getMaxHotCoolant();";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileSolarMaster tile) throws LuaException {
            if(arguments == null || arguments.length != 0) {
                throw CCHelper.INVALID_ARGUMENT_EXCEPTION;
            }
            return new Object[] { tile.getMaxTankCapacity() };
        }
    }

    private static class CCMethodGetProduction implements CCHelper.ICCMethod<TileSolarMaster> {

        @Override
        public String getMethodName() {
            return "getProduction";
        }

        @Override
        public String getMethodDescription() {
            return "\tReturns the amount of fluidUsedCoolant[mB] produced per tick.\n\tUsage: getProduction();";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileSolarMaster tile) throws LuaException {
            if(arguments == null || arguments.length != 0) {
                throw CCHelper.INVALID_ARGUMENT_EXCEPTION;
            }
            return new Object[] { tile.getProductionLastTick() };
        }
    }


}
