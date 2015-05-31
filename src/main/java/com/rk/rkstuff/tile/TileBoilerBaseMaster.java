package com.rk.rkstuff.tile;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.block.BlockBoilerBaseMaster;
import com.rk.rkstuff.block.BlockBoilerTank;
import com.rk.rkstuff.block.IBoilerBaseBlock;
import com.rk.rkstuff.cc.CCMethodRegistry;
import com.rk.rkstuff.cc.ICCMethod;
import com.rk.rkstuff.helper.FluidHelper;
import com.rk.rkstuff.helper.MultiBlockHelper;
import com.rk.rkstuff.helper.RKLog;
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

    private int baseCount;
    private int tankCount;

    private final int HEATENERGY_PER_TANK = 1000;
    private final int HEATENERGY_PER_WATERBUCKET = 1000;
    private final int HEATENERGY_PRODUCTION_PER_BASE = 28;
    private final float STEAM_PER_HEATENERGY = 0.02f;
    private final int STEAM_PER_WATER_MB = 2;
    private final int COOLANT_TRANSFER_PER_BASE = 1;
    private final int COOLANT_STORAGE_PER_BASE = 1000;
    private final int WATER_STORAGE_PER_TANK = 1000;


    //in mB
    private int steamStorage;
    private int waterStorage;
    private int hotCoolantStorage;
    private int coolCoolantStorage;

    private int heatEnergy = 0;

    @Override
    protected void updateMaster() {
        int coolantUsage = Math.min(hotCoolantStorage, COOLANT_TRANSFER_PER_BASE * baseCount);
        coolantUsage = Math.min(coolantUsage, getMaxCoolantStorage() - coolCoolantStorage);
        hotCoolantStorage -= coolantUsage;
        coolCoolantStorage += coolantUsage;
        heatingProcess(coolantUsage);

        doSteamProduction();
    }

    private void heatingProcess(int amountHotFluid) {
        heatEnergy -= tankCount * HEATENERGY_PER_TANK * 0.05;
        if (heatEnergy < 0) heatEnergy = 0;

        heatEnergy += amountHotFluid * baseCount * HEATENERGY_PRODUCTION_PER_BASE;

        if (heatEnergy > getStartSteamProducingHeatingEnergyLevel() * 3) {
            heatEnergy = getStartSteamProducingHeatingEnergyLevel() * 3;
        }
        if (worldObj.getWorldTime() % 100 == 0) {
            RKLog.info("Heat: " + heatEnergy);
        }
    }

    private int doSteamProduction() {
        int heatDiff = heatEnergy - getStartSteamProducingHeatingEnergyLevel();
        if (heatDiff > 0) {
            int production = (int) (heatDiff * STEAM_PER_HEATENERGY);
            production = Math.min(production, getMaxSteamStorage() - steamStorage);
            production = Math.min(production, getWaterStorage() * STEAM_PER_WATER_MB);

            heatEnergy -= production / STEAM_PER_WATER_MB;
            waterStorage -= production / STEAM_PER_WATER_MB;
            steamStorage += production;
            return production;
        }
        return 0;
    }

    private int getStartSteamProducingHeatingEnergyLevel() {
        return (int) (tankCount * HEATENERGY_PER_TANK + (waterStorage / 1000.0) * HEATENERGY_PER_WATERBUCKET);
    }

    public int getTemperature() {
        return (int) ((float) heatEnergy / getStartSteamProducingHeatingEnergyLevel() * 100);
    }

    public int getMaxTemperature() {
        return 300;
    }

    public int getMaxCoolantStorage() {
        return COOLANT_STORAGE_PER_BASE * baseCount;
    }

    public int getCoolCoolantStorage() {
        return coolCoolantStorage;
    }

    public int getHotCoolantStorage() {
        return hotCoolantStorage;
    }

    public int getWaterStorage() {
        return waterStorage;
    }

    public int getMaxWaterStorage() {
        return tankCount * WATER_STORAGE_PER_TANK;
    }

    public int getSteamStorage() {
        return steamStorage;
    }

    public int getMaxSteamStorage() {
        return getMaxWaterStorage() * STEAM_PER_WATER_MB;
    }


    @Override
    public void writeData(IOStream data) {
        data.writeLast(heatEnergy);
        data.writeLast(coolCoolantStorage);
        data.writeLast(hotCoolantStorage);
        data.writeLast(waterStorage);
        data.writeLast(steamStorage);
        data.writeLast(tankCount);
        data.writeLast(baseCount);
    }

    @Override
    public void readData(IOStream data) throws IOException {
        heatEnergy = data.readFirstInt();
        coolCoolantStorage = data.readFirstInt();
        hotCoolantStorage = data.readFirstInt();
        waterStorage = data.readFirstInt();
        steamStorage = data.readFirstInt();
        tankCount = data.readFirstInt();
        baseCount = data.readFirstInt();
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("steam", steamStorage);
        data.setInteger("water", waterStorage);
        data.setInteger("coolCoolant", coolCoolantStorage);
        data.setInteger("hotCoolant", hotCoolantStorage);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        steamStorage = data.getInteger("steam");
        waterStorage = data.getInteger("water");
        coolCoolantStorage = data.getInteger("coolCoolant");
        hotCoolantStorage = data.getInteger("hotCoolant");
    }


    public int fill(FluidStack resource, boolean doFill) {
        int amount = 0;
        if(FluidHelper.isWater(resource.getFluid())) {
            amount = Math.min(resource.amount, getMaxWaterStorage() - waterStorage);
            if(doFill) waterStorage += amount;
        } else if(FluidHelper.isHotCoolant(resource.getFluid())) {
            amount = Math.min(resource.amount, getMaxCoolantStorage() - hotCoolantStorage);
            if(doFill) hotCoolantStorage += amount;
        }
        return amount;
    }

    public FluidStack drainSteam(int maxDrain, boolean doDrain) {
        int amount = Math.min(maxDrain, steamStorage);
        if(doDrain) steamStorage -= amount;
        return new FluidStack(FluidHelper.steam, amount);
    }

    public FluidStack drainCoolCoolant(int maxDrain, boolean doDrain) {
        int amount = Math.min(maxDrain, coolCoolantStorage);
        if(doDrain) coolCoolantStorage -= amount;
        return new FluidStack(RkStuff.coolCoolant, amount);
    }

    public boolean canFill(Fluid fluid) {
        if(FluidHelper.isWater(fluid)) {
            return waterStorage < getMaxWaterStorage();
        } else if(FluidHelper.isHotCoolant(fluid)) {
            return hotCoolantStorage < getMaxCoolantStorage();
        }
        return false;
    }

    public boolean canDrainSteam() {
        return steamStorage > 0;
    }

    public boolean canDrainCoolCoolant() {
        return coolCoolantStorage > 0;
    }

    public FluidTankInfo[] getTankInfoInput() {
        return new FluidTankInfo[] {
                new FluidTankInfo(new FluidStack(FluidHelper.water, waterStorage), getMaxWaterStorage()),
                new FluidTankInfo(new FluidStack(RkStuff.hotCoolant, hotCoolantStorage), getMaxCoolantStorage())
        };
    }

    public FluidTankInfo[] getTankInfoSteam() {
        return new FluidTankInfo[] {
                new FluidTankInfo(new FluidStack(FluidHelper.steam, steamStorage), getMaxSteamStorage())
        };
    }

    public FluidTankInfo[] getTankInfoCoolCoolant() {
        return new FluidTankInfo[] {
                new FluidTankInfo(new FluidStack(RkStuff.coolCoolant, coolCoolantStorage), getMaxCoolantStorage())
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
                meta |= ((pos.hasBlock(ForgeDirection.NORTH) ? 1 : 0) << (ForgeDirection.NORTH.ordinal() - 2));
                meta |= ((pos.hasBlock(ForgeDirection.EAST) ? 1 : 0) << (ForgeDirection.EAST.ordinal() - 2));
                meta |= ((pos.hasBlock(ForgeDirection.SOUTH) ? 1 : 0) << (ForgeDirection.SOUTH.ordinal() - 2));
                meta |= ((pos.hasBlock(ForgeDirection.WEST) ? 1 : 0) << (ForgeDirection.WEST.ordinal() - 2));
                worldObj.setBlockMetadataWithNotify(pos.x, pos.y, pos.z, meta, 2);
            } else if(block instanceof BlockBoilerTank) {
                //TODO: set right metadata
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

        if (coolCoolantStorage > getMaxCoolantStorage()) coolCoolantStorage = getMaxCoolantStorage();
        if (hotCoolantStorage > getMaxCoolantStorage()) hotCoolantStorage = getMaxCoolantStorage();
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

        //check boiler tank (min. size 2x2)
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
                    //boiler tank heights are not equal
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
    protected boolean hasGui() {
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
        return CCMethodRegistry.getMethods(this);
    }

    @Override
    @Optional.Method(modid = "ComputerCraft")
    public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
        return CCMethodRegistry.executeMethod(computer, context, arguments, this, method);
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

    public static class CCMethodGetCoolCoolant implements ICCMethod<TileBoilerBaseMaster> {

        @Override
        public String getMethodName() {
            return "getCoolCoolant";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileBoilerBaseMaster tile) {
            if(arguments != null && arguments.length > 0) {
                return new Object[0];
            }
            return new Object[] { tile.getCoolCoolantStorage()};
        }
    }

    public static class CCMethodGetMaxCoolCoolant implements ICCMethod<TileBoilerBaseMaster> {

        @Override
        public String getMethodName() {
            return "getMaxCoolCoolant";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileBoilerBaseMaster tile) {
            if(arguments != null && arguments.length > 0) {
                return new Object[0];
            }
            return new Object[] { tile.getMaxCoolantStorage() };
        }
    }

    public static class CCMethodGetHotCoolant implements ICCMethod<TileBoilerBaseMaster> {

        @Override
        public String getMethodName() {
            return "getHotCoolant";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileBoilerBaseMaster tile) {
            if(arguments != null && arguments.length > 0) {
                return new Object[0];
            }
            return new Object[] { tile.getHotCoolantStorage()};
        }
    }

    public static class CCMethodGetMaxHotCoolant implements ICCMethod<TileBoilerBaseMaster> {

        @Override
        public String getMethodName() {
            return "getMaxHotCoolant";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileBoilerBaseMaster tile) {
            if(arguments != null && arguments.length > 0) {
                return new Object[0];
            }
            return new Object[] { tile.getMaxCoolantStorage() };
        }
    }

    public static class CCMethodGetWater implements ICCMethod<TileBoilerBaseMaster> {

        @Override
        public String getMethodName() {
            return "getWater";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileBoilerBaseMaster tile) {
            if(arguments != null && arguments.length > 0) {
                return new Object[0];
            }
            return new Object[] { tile.getWaterStorage()};
        }
    }

    public static class CCMethodGetMaxWater implements ICCMethod<TileBoilerBaseMaster> {

        @Override
        public String getMethodName() {
            return "getMaxWater";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileBoilerBaseMaster tile) {
            if(arguments != null && arguments.length > 0) {
                return new Object[0];
            }
            return new Object[] { tile.getMaxWaterStorage() };
        }
    }

    public static class CCMethodGetSteam implements ICCMethod<TileBoilerBaseMaster> {

        @Override
        public String getMethodName() {
            return "getSteam";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileBoilerBaseMaster tile) {
            if(arguments != null && arguments.length > 0) {
                return new Object[0];
            }
            return new Object[] { tile.getSteamStorage()};
        }
    }

    public static class CCMethodGetMaxSteam implements ICCMethod<TileBoilerBaseMaster> {

        @Override
        public String getMethodName() {
            return "getMaxSteam";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileBoilerBaseMaster tile) {
            if(arguments != null && arguments.length > 0) {
                return new Object[0];
            }
            return new Object[] { tile.getMaxSteamStorage() };
        }
    }

    public static class CCMethodGetTemperature implements ICCMethod<TileBoilerBaseMaster> {

        @Override
        public String getMethodName() {
            return "getTemperature";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileBoilerBaseMaster tile) {
            if(arguments != null && arguments.length > 0) {
                return new Object[0];
            }
            return new Object[] { tile.getTemperature() };
        }
    }

    public static class CCMethodGetMaxTemperature implements ICCMethod<TileBoilerBaseMaster> {

        @Override
        public String getMethodName() {
            return "getMaxTemperature";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileBoilerBaseMaster tile) {
            if(arguments != null && arguments.length > 0) {
                return new Object[0];
            }
            return new Object[] { tile.getMaxTemperature() };
        }
    }
}
