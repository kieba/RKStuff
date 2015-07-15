package com.rk.rkstuff.boiler.tile;

import com.rk.rkstuff.boiler.block.BlockBoilerBaseMaster;
import com.rk.rkstuff.boiler.block.BlockBoilerTank;
import com.rk.rkstuff.boiler.block.IBoilerBaseBlock;
import com.rk.rkstuff.coolant.CoolantStack;
import com.rk.rkstuff.core.tile.IMultiBlockMasterListener;
import com.rk.rkstuff.core.tile.TileMultiBlockMaster;
import com.rk.rkstuff.helper.CCHelper;
import com.rk.rkstuff.helper.FluidHelper;
import com.rk.rkstuff.helper.MultiBlockHelper;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import rk.com.core.io.IOStream;

import java.io.IOException;

@Optional.InterfaceList({
        @Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = "ComputerCraft")
})
public class TileBoilerBaseMaster extends TileMultiBlockMaster implements IPeripheral {

    private static CCHelper.CCMethods METHODS = new CCHelper.CCMethods();
    static {
        METHODS.add(new CCHelper.CCMethodDoc(METHODS));
        METHODS.add(new CCMethodGetCoolant());
        METHODS.add(new CCMethodGetMaxCoolant());
        METHODS.add(new CCMethodGetWater());
        METHODS.add(new CCMethodGetMaxWater());
        METHODS.add(new CCMethodGetSteam());
        METHODS.add(new CCMethodGetMaxSteam());
        METHODS.add(new CCMethodGetTemperature());
        METHODS.add(new CCMethodGetMaxTemperature());
    }

    private static final float SCALE = 0.1f;
    private static final float MAX_TEMPERATURE = 500.0f;
    private static final float NEEDED_TEMPERATURE = 100.0f;
    private static final float WATER_TEMPERATURE = 20.0f;
    private static final float WATER_TO_STEAM = 4.0f;

    private int baseCount;
    private int tankCount;

    //in mB
    private int maxHotWater;
    private int maxSteamStorage;
    private int maxWaterStorage;
    private int maxCoolantStorage;
    private int maxCoolantOutput;
    private int maxCoolantInput;
    private int steamStorage;
    private int waterStorage;
    private CoolantStack coolantStack = new CoolantStack();

    @Override
    protected void updateMaster() {

        if (coolantStack.getAmount() > 0 && coolantStack.getTemperature() >= NEEDED_TEMPERATURE && waterStorage != 0 && steamStorage < maxSteamStorage) {
            //convert water to steam
            int hotWater = (int) Math.floor(SCALE * (coolantStack.getAmount() * (NEEDED_TEMPERATURE - coolantStack.getTemperature())) / (WATER_TEMPERATURE - NEEDED_TEMPERATURE));
            hotWater = Math.min(hotWater, waterStorage);
            hotWater = Math.min(hotWater, (int) Math.floor((maxSteamStorage - steamStorage) / WATER_TO_STEAM));
            hotWater = Math.min(hotWater, maxHotWater);

            waterStorage -= hotWater;
            steamStorage += (int) Math.floor(hotWater * WATER_TO_STEAM);

            //cool down coolant
            double newTemp = (hotWater * WATER_TEMPERATURE + coolantStack.getAmount() * coolantStack.getTemperature()) / ((float) hotWater + coolantStack.getAmount());
            coolantStack.set(coolantStack.getAmount(), newTemp);
        }

        double newTemp = coolantStack.getTemperature() - 0.00175f;
        if (newTemp < 0.0f) newTemp = 0.0f;
        coolantStack.set(coolantStack.getAmount(), newTemp);
    }


    @Override
    public void writeData(IOStream data) {
        data.writeLast(waterStorage);
        data.writeLast(steamStorage);
        coolantStack.writeData(data);

        data.writeLast(maxWaterStorage);
        data.writeLast(maxSteamStorage);
        data.writeLast(maxCoolantStorage);

        data.writeLast(tankCount);
        data.writeLast(baseCount);
    }

    @Override
    public void readData(IOStream data) throws IOException {
        waterStorage = data.readFirstInt();
        steamStorage = data.readFirstInt();
        coolantStack.readData(data);

        maxWaterStorage = data.readFirstInt();
        maxSteamStorage = data.readFirstInt();
        maxCoolantStorage = data.readFirstInt();

        tankCount = data.readFirstInt();
        baseCount = data.readFirstInt();
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("steam", steamStorage);
        data.setInteger("water", waterStorage);
        coolantStack.writeToNBT("coolant", data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        steamStorage = data.getInteger("steam");
        waterStorage = data.getInteger("water");
        coolantStack = new CoolantStack();
        coolantStack.readFromNBT("coolant", data);
    }

    public float getMaxTemperature() {
        return MAX_TEMPERATURE;
    }

    public int getMaxWaterStorage() {
        return maxWaterStorage;
    }

    public int getMaxSteamStorage() {
        return maxSteamStorage;
    }

    public int getMaxCoolantStorage() {
        return maxCoolantStorage;
    }

    public int getWaterStorage() {
        return waterStorage;
    }

    public int getSteamStorage() {
        return steamStorage;
    }

    public int getCoolantStorage() {
        return Math.min(coolantStack.getAmount(), getMaxCoolantStorage());
    }

    public int getMaxOutputCoolant() {
        int amount = 0;
        if (coolantStack.getTemperature() < NEEDED_TEMPERATURE) {
            amount = coolantStack.getAmount();
        } else if (coolantStack.getAmount() > getMaxCoolantStorage()) {
            amount = coolantStack.getAmount() - getMaxCoolantStorage();
        }
        return Math.min(amount, maxCoolantOutput);
    }

    public void drainCoolant(int amount) {
        coolantStack.remove(amount);
    }

    public double getTemperature() {
        return coolantStack.getTemperature();
    }

    public int receiveCoolant(ForgeDirection from, int maxAmount, double temperature, boolean simulate) {
        int amount = Math.min(maxAmount, (maxCoolantStorage + maxCoolantOutput) - coolantStack.getAmount());
        if (!simulate) {
            coolantStack.add(amount, temperature);
        }
        return amount;
    }

    public int fill(FluidStack resource, boolean doFill) {
        int amount = 0;
        if(FluidHelper.isWater(resource.getFluid())) {
            amount = Math.min(resource.amount, getMaxWaterStorage() - waterStorage);
            amount = Math.min(amount, maxCoolantInput);
            if(doFill) waterStorage += amount;
        }
        return amount;
    }

    public FluidStack drainSteam(int maxDrain, boolean doDrain) {
        int amount = Math.min(maxDrain, steamStorage);
        if(doDrain) steamStorage -= amount;
        return new FluidStack(FluidHelper.steam, amount);
    }


    public boolean canFill(Fluid fluid) {
        if (FluidHelper.isWater(fluid)) return true;
        return false;
    }


    public FluidTankInfo[] getTankInfoInput() {
        return new FluidTankInfo[] {
                new FluidTankInfo(new FluidStack(FluidHelper.water, waterStorage), getMaxWaterStorage()),
        };
    }

    public FluidTankInfo[] getTankInfoOutput() {
        return new FluidTankInfo[] {
                new FluidTankInfo(new FluidStack(FluidHelper.steam, steamStorage), getMaxSteamStorage())
        };
    }

    @Override
    public boolean checkMultiBlockForm() {
        return computeMultiStructureBounds() != null;
    }

    @Override
    protected MultiBlockHelper.Bounds setupStructure() {
        MultiBlockHelper.Bounds tmpBounds = computeMultiStructureBounds();
        for(MultiBlockHelper.Bounds.BlockIterator.BoundsPos pos : tmpBounds){
            Block block = worldObj.getBlock(pos.x, pos.y, pos.z);

            if(block instanceof IBoilerBaseBlock || block instanceof BlockBoilerBaseMaster) {
                int meta = 0;
                meta |= ((pos.hasNeighbourBlock(ForgeDirection.NORTH) ? 1 : 0) << (ForgeDirection.NORTH.ordinal() - 2));
                meta |= ((pos.hasNeighbourBlock(ForgeDirection.EAST) ? 1 : 0) << (ForgeDirection.EAST.ordinal() - 2));
                meta |= ((pos.hasNeighbourBlock(ForgeDirection.SOUTH) ? 1 : 0) << (ForgeDirection.SOUTH.ordinal() - 2));
                meta |= ((pos.hasNeighbourBlock(ForgeDirection.WEST) ? 1 : 0) << (ForgeDirection.WEST.ordinal() - 2));
                worldObj.setBlockMetadataWithNotify(pos.x, pos.y, pos.z, meta, 2);
            } else if(block instanceof BlockBoilerTank) {
                int meta = 0;
                if (pos.x == tmpBounds.getMinX()) {
                    if (pos.z == tmpBounds.getMinZ()) {
                        meta = 4;
                    } else if (pos.z == tmpBounds.getMaxZ()) {
                        meta = 6;
                    } else {
                        meta = 3;
                    }
                }

                if (pos.x == tmpBounds.getMaxX()) {
                    if (pos.z != tmpBounds.getMinZ() && pos.z != tmpBounds.getMaxZ()) {
                        meta = 5;
                    }
                }

                if (pos.z == tmpBounds.getMinZ()) {
                    if (pos.x == tmpBounds.getMaxX()) {
                        meta = 2;
                    }
                    if (pos.x != tmpBounds.getMinX() && pos.x != tmpBounds.getMaxX()) {
                        meta = 1;
                    }
                }

                if (pos.z == tmpBounds.getMaxZ()) {
                    if (pos.x == tmpBounds.getMaxX()) {
                        meta = 8;
                    }
                    if (pos.x != tmpBounds.getMinX() && pos.x != tmpBounds.getMaxX()) {
                        meta = 7;
                    }
                }

                worldObj.setBlockMetadataWithNotify(pos.x, pos.y, pos.z, meta, 2);
            }

            if(block instanceof ITileEntityProvider) {
                TileEntity tile = worldObj.getTileEntity(pos.x, pos.y, pos.z);
                if (tile instanceof IMultiBlockMasterListener) {
                    ((IMultiBlockMasterListener) tile).registerMaster(this);
                }
            }
        }
        baseCount = tmpBounds.getWidthX() * tmpBounds.getWidthZ();
        tankCount = baseCount * (tmpBounds.getHeight() - 1);

        maxCoolantStorage = tankCount * 1000;
        maxWaterStorage = tankCount * 2000;
        maxSteamStorage = tankCount * 5000;
        maxCoolantOutput = (int) (maxCoolantStorage * 0.005);
        maxCoolantInput = (int) (maxCoolantStorage * 0.005);
        maxHotWater = tankCount * 2;

        if (coolantStack.getAmount() > getMaxCoolantStorage())
            coolantStack.set(getMaxCoolantStorage(), coolantStack.getTemperature());
        if (waterStorage > getMaxWaterStorage()) waterStorage = getMaxWaterStorage();
        if (steamStorage > getMaxSteamStorage()) steamStorage = getMaxSteamStorage();

        return tmpBounds;
    }

    private MultiBlockHelper.Bounds computeMultiStructureBounds(){
        MultiBlockHelper.Bounds tmpBounds = new MultiBlockHelper.Bounds(xCoord, yCoord, zCoord);

        //get boiler base bounds
        for(ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS){
            if(direction == ForgeDirection.UP) continue;
            if(direction == ForgeDirection.DOWN) continue;
            int i = 1;
            while (isValidBoilerBase(xCoord + direction.offsetX * i, yCoord, zCoord  + direction.offsetZ * i)) {
                i++;
            }
            i--;
            tmpBounds.add(xCoord + direction.offsetX * i, yCoord, zCoord  + direction.offsetZ * i);
        }

        //check if there are only BoilerBseBlocks within the bounds of the boiler base
        for(int x = tmpBounds.getMinX(); x <= tmpBounds.getMaxX(); x++){
            for(int z = tmpBounds.getMinZ(); z <= tmpBounds.getMaxZ(); z++){
                if(x == xCoord && z == zCoord) continue;
                if(!isValidBoilerBase(x, yCoord, z)) {
                    //base is not complete
                    return null;
                }
            }
        }

        //check if there are other BoilerBaseBlocks around the base, if true don't build the structure
        for (int x = tmpBounds.getMinX() - 1; x <= tmpBounds.getMaxX() + 1; x++) {
            if(isValidBoilerBase(x, yCoord, tmpBounds.getMinZ() - 1)) {
                return null;
            }
            if(isValidBoilerBase(x, yCoord, tmpBounds.getMaxZ() + 1)) {
                return null;
            }
        }

        for (int z = tmpBounds.getMinZ() - 1; z <= tmpBounds.getMaxZ() + 1; z++) {
            if(isValidBoilerBase(tmpBounds.getMinX() - 1, yCoord, z)) {
                return null;
            }
            if(isValidBoilerBase(tmpBounds.getMaxX() + 1, yCoord, z)) {
                return null;
            }
        }

        //check boiler block (min. size 2x2)
        if(tmpBounds.getWidthX() > 0 && tmpBounds.getWidthZ() > 0) {
            int height = -1;
            for(MultiBlockHelper.Bounds.BlockIterator.BoundsPos pos : tmpBounds) {
                int i = 1;
                while(isValidBoilerTank(pos.x, yCoord + i, pos.z)) {
                    i++;
                }
                i--;
                if(height == -1) {
                    height = i;
                } else if(height != i) {
                    //boiler block heights are not equal
                    return null;
                }
            }
            if(height < 2) {
                //we need at least a height of 2
                return null;
            } else {
                tmpBounds.setMaxY(tmpBounds.getMaxY() + height);
                return tmpBounds;
            }
        }
        return null;
    }

    @Override
    public void resetStructure() {
        if(bounds != null){
            for(MultiBlockHelper.Bounds.BlockIterator.BoundsPos pos : bounds){
                worldObj.setBlockMetadataWithNotify(pos.x, pos.y, pos.z, 0, 2);

                Block block = worldObj.getBlock(pos.x, pos.y, pos.z);
                if(block instanceof ITileEntityProvider) {
                    TileEntity tile = worldObj.getTileEntity(pos.x, pos.y, pos.z);
                    if (tile instanceof IMultiBlockMasterListener) {
                        ((IMultiBlockMasterListener) tile).unregisterMaster();
                    }
                }
            }
        }
    }

    private boolean isValidBoilerBase(int x, int y, int z){
        Block block = worldObj.getBlock(x, y, z);
        return block instanceof IBoilerBaseBlock || block instanceof BlockBoilerBaseMaster;
    }

    private boolean isValidBoilerTank(int x, int y, int z){
        Block block = worldObj.getBlock(x, y, z);
        return block instanceof BlockBoilerTank;
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    @Optional.Method(modid = "ComputerCraft")
    public String getType() {
        return Reference.TILE_BOILER_BASE_MASTER;
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

    private static class CCMethodGetCoolant implements CCHelper.ICCMethod<TileBoilerBaseMaster> {

        @Override
        public String getMethodName() {
            return "getCoolant";
        }

        @Override
        public String getMethodDescription() {
            return "\tReturns the storage of the coolant[mB].\n\tUsage: getCoolant();";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileBoilerBaseMaster tile) throws LuaException {
            if(arguments == null || arguments.length != 0) {
                throw CCHelper.INVALID_ARGUMENT_EXCEPTION;
            }
            return new Object[]{tile.getCoolantStorage()};
        }
    }

    private static class CCMethodGetMaxCoolant implements CCHelper.ICCMethod<TileBoilerBaseMaster> {

        @Override
        public String getMethodName() {
            return "getMaxCoolant";
        }

        @Override
        public String getMethodDescription() {
            return "\tReturns the maximum storage of coolant[mB].\n\tUsage: getMaxCoolant();";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileBoilerBaseMaster tile) throws LuaException {
            if(arguments == null || arguments.length != 0) {
                throw CCHelper.INVALID_ARGUMENT_EXCEPTION;
            }
            return new Object[] { tile.getMaxCoolantStorage() };
        }
    }

    private static class CCMethodGetWater implements CCHelper.ICCMethod<TileBoilerBaseMaster> {

        @Override
        public String getMethodName() {
            return "getWater";
        }

        @Override
        public String getMethodDescription() {
            return "\tReturns the storage of water[mB].\n\tUsage: getWater();";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileBoilerBaseMaster tile) throws LuaException {
            if(arguments == null || arguments.length != 0) {
                throw CCHelper.INVALID_ARGUMENT_EXCEPTION;
            }
            return new Object[] { tile.getWaterStorage()};
        }
    }

    private static class CCMethodGetMaxWater implements CCHelper.ICCMethod<TileBoilerBaseMaster> {

        @Override
        public String getMethodName() {
            return "getMaxWater";
        }

        @Override
        public String getMethodDescription() {
            return "\tReturns the maximum storage of water[mB].\n\tUsage: getMaxWater();";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileBoilerBaseMaster tile) throws LuaException {
            if(arguments == null || arguments.length != 0) {
                throw CCHelper.INVALID_ARGUMENT_EXCEPTION;
            }
            return new Object[] { tile.getMaxWaterStorage() };
        }
    }

    private static class CCMethodGetSteam implements CCHelper.ICCMethod<TileBoilerBaseMaster> {

        @Override
        public String getMethodName() {
            return "getSteam";
        }

        @Override
        public String getMethodDescription() {
            return "\tReturns the storage of steam[mB].\n\tUsage: getSteam();";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileBoilerBaseMaster tile) throws LuaException {
            if(arguments == null || arguments.length != 0) {
                throw CCHelper.INVALID_ARGUMENT_EXCEPTION;
            }
            return new Object[] { tile.getSteamStorage()};
        }
    }

    private static class CCMethodGetMaxSteam implements CCHelper.ICCMethod<TileBoilerBaseMaster> {

        @Override
        public String getMethodName() {
            return "getMaxSteam";
        }

        @Override
        public String getMethodDescription() {
            return "\tReturns the maximum storage of steam[mB].\n\tUsage: getMaxSteam();";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileBoilerBaseMaster tile) throws LuaException {
            if(arguments == null || arguments.length != 0) {
                throw CCHelper.INVALID_ARGUMENT_EXCEPTION;
            }
            return new Object[] { tile.getMaxSteamStorage() };
        }
    }

    private static class CCMethodGetTemperature implements CCHelper.ICCMethod<TileBoilerBaseMaster> {

        @Override
        public String getMethodName() {
            return "getTemperature";
        }

        @Override
        public String getMethodDescription() {
            return "\tReturns the temperature.\n\tUsage: getTemperature();";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileBoilerBaseMaster tile) throws LuaException {
            if(arguments == null || arguments.length != 0) {
                throw CCHelper.INVALID_ARGUMENT_EXCEPTION;
            }
            return new Object[] { tile.getTemperature() };
        }
    }

    private static class CCMethodGetMaxTemperature implements CCHelper.ICCMethod<TileBoilerBaseMaster> {

        @Override
        public String getMethodName() {
            return "getMaxTemperature";
        }

        @Override
        public String getMethodDescription() {
            return "\tReturns the maximum temperature.\n\tUsage: getMaxTemperature();";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileBoilerBaseMaster tile) throws LuaException {
            if(arguments == null || arguments.length != 0) {
                throw CCHelper.INVALID_ARGUMENT_EXCEPTION;
            }
            return new Object[]{MAX_TEMPERATURE};
        }
    }
}
