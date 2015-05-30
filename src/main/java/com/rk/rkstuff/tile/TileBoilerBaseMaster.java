package com.rk.rkstuff.tile;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.block.BlockBoilerBaseMaster;
import com.rk.rkstuff.block.BlockBoilerTank;
import com.rk.rkstuff.block.IBoilerBaseBlock;
import com.rk.rkstuff.helper.FluidHelper;
import com.rk.rkstuff.helper.MultiBlockHelper;
import com.rk.rkstuff.helper.RKLog;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

public class TileBoilerBaseMaster extends TileMultiBlockMaster {

    private int baseCount;
    private int tankCount;

    //in mB
    private int maxSteamStorage = 10000;
    private int maxWaterStorage = 10000;
    private int maxCoolantStorage = 10000;

    //in mB
    private int steamStorage;
    private int waterStorage;
    private int hotCoolantStorage;
    private int coolCoolantStorage;

    private int coolantTransferRate = 20; //mB per tick
    private int steamProductionRate = 20; //mB per tick

    @Override
    protected void updateMaster() {
        //TODO: implement logic

        //transfer hotCoolant to coldCoolant with a rate of coolantTransferRate
        int tmp = Math.min(hotCoolantStorage, coolantTransferRate);
        tmp = Math.min(tmp, maxCoolantStorage - coolCoolantStorage);

        if(tmp > 0) {
            hotCoolantStorage -= tmp;
            coolCoolantStorage += tmp;
        }

        //transfer water to steam with a rate of steamProductionRate
        tmp = Math.min(waterStorage, steamProductionRate);
        tmp = Math.min(tmp, maxSteamStorage - steamStorage);

        if(tmp > 0) {
            waterStorage -= tmp;
            steamStorage += tmp;
        }

    }

    @Override
    protected void writeToNBTMaster(NBTTagCompound data) {
        data.setInteger("steam", steamStorage);
        data.setInteger("water", waterStorage);
        data.setInteger("coolCoolant", coolCoolantStorage);
        data.setInteger("hotCoolant", hotCoolantStorage);
    }

    @Override
    protected void readFromNBTMaster(NBTTagCompound data) {
        steamStorage = data.getInteger("steam");
        waterStorage = data.getInteger("water");
        coolCoolantStorage = data.getInteger("coolCoolant");
        hotCoolantStorage = data.getInteger("hotCoolant");
        bounds = null;
        isBuild = false;
    }

    public int fill(FluidStack resource, boolean doFill) {
        int amount = 0;
        if(FluidHelper.isWater(resource.getFluid())) {
            amount = Math.min(resource.amount, maxWaterStorage - waterStorage);
            if(doFill) waterStorage += amount;
        } else if(FluidHelper.isHotCoolant(resource.getFluid())) {
            amount = Math.min(resource.amount, maxCoolantStorage - hotCoolantStorage);
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
            return waterStorage < maxWaterStorage;
        } else if(FluidHelper.isHotCoolant(fluid)) {
            return hotCoolantStorage < maxCoolantStorage;
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
                new FluidTankInfo(new FluidStack(FluidHelper.water, waterStorage), maxWaterStorage),
                new FluidTankInfo(new FluidStack(RkStuff.hotCoolant, hotCoolantStorage), maxCoolantStorage)
        };
    }

    public FluidTankInfo[] getTankInfoSteam() {
        return new FluidTankInfo[] {
                new FluidTankInfo(new FluidStack(FluidHelper.steam, steamStorage), maxSteamStorage)
        };
    }

    public FluidTankInfo[] getTankInfoCoolCoolant() {
        return new FluidTankInfo[] {
                new FluidTankInfo(new FluidStack(RkStuff.coolCoolant, coolCoolantStorage), maxCoolantStorage)
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
                worldObj.setBlockMetadataWithNotify(pos.x, pos.y, pos.z, 1, 2);
            }

            if(block instanceof ITileEntityProvider) {
                TileEntity tile = worldObj.getTileEntity(pos.x, pos.y, pos.z);
                if(tile instanceof IBoilerBaseTile) {
                    ((IBoilerBaseTile)tile).setMaster(xCoord, yCoord, zCoord);
                }
            }
        }
        baseCount = tmpBounds.getWidthX() * tmpBounds.getWidthZ();
        tankCount = baseCount * (tmpBounds.getHeight() - 1);

        maxCoolantStorage = baseCount * 1000;
        maxWaterStorage = tankCount * 2000;
        maxSteamStorage = tankCount  * 4000;

        if(coolCoolantStorage > maxCoolantStorage) coolCoolantStorage = maxCoolantStorage;
        if(hotCoolantStorage > maxCoolantStorage) hotCoolantStorage = maxCoolantStorage;
        if(waterStorage > maxWaterStorage) waterStorage = maxWaterStorage;
        if(steamStorage > maxSteamStorage) steamStorage = maxSteamStorage;

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
                    if(tile instanceof IBoilerBaseTile) {
                        ((IBoilerBaseTile)tile).resetMaster();
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
}
