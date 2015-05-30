package com.rk.rkstuff.tile;

import com.rk.rkstuff.block.BlockBoilerTank;
import com.rk.rkstuff.block.IBoilerBaseBlock;
import com.rk.rkstuff.helper.MultiBlockHelper;
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

    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return 0;
    }

    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return null;
    }

    public FluidStack drainSteam(int maxDrain, boolean doDrain) {
        return null;
    }

    public FluidStack drainColdCoolant(int maxDrain, boolean doDrain) {
        return null;
    }

    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return false;
    }

    public boolean canDrainSteam() {
        return false;
    }

    public boolean canDrainColdCoolant() {
        return false;
    }

    public FluidTankInfo[] getTankInfoInput() {
        return new FluidTankInfo[0];
    }

    public FluidTankInfo[] getTankInfoSteam() {
        return new FluidTankInfo[0];
    }

    public FluidTankInfo[] getTankInfoColdCoolant() {
        return new FluidTankInfo[0];
    }

    @Override
    protected boolean checkMultiBlockForm() {
        return computeMultiStructureBounds() != null;
    }

    @Override
    protected MultiBlockHelper.Bounds setupStructure() {
        MultiBlockHelper.Bounds tmpBounds = computeMultiStructureBounds();
        for(MultiBlockHelper.Bounds.BlockIterator.BoundsPos pos : tmpBounds){
            //TODO: set metadata for each block

            Block block = worldObj.getBlock(pos.x, pos.y, pos.z);
            if(block instanceof ITileEntityProvider) {
                TileEntity tile = worldObj.getTileEntity(pos.x, pos.y, pos.z);
                if(tile instanceof IBoilerBaseTile) {
                    ((IBoilerBaseTile)tile).setMaster(xCoord, yCoord, zCoord);
                }
            }
        }
        baseCount = tmpBounds.getWidthX() * tmpBounds.getWidthZ();
        tankCount = baseCount * (tmpBounds.getHeight() - 1);
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
        for(int x = bounds.getMinX(); x <= bounds.getMaxX(); x++){
            for(int z = bounds.getMinZ(); z <= bounds.getMaxZ(); z++){
                if(x == xCoord && z == zCoord) continue;
                if(!isValidBoilerBase(x, yCoord, z)) {
                    //base is not complete
                    return null;
                }
            }
        }

        //check if there are other BoilerBaseBlocks around the base, if true don't build the structure
        for (int x = bounds.getMinX() - 1; x <= bounds.getMaxX() + 1; x++) {
            if(isValidBoilerBase(x, yCoord, bounds.getMinZ() - 1)) {
                return null;
            }
            if(isValidBoilerBase(x, yCoord, bounds.getMaxZ() + 1)) {
                return null;
            }
        }

        for (int z = bounds.getMinZ() - 1; z <= bounds.getMaxZ() + 1; z++) {
            if(isValidBoilerBase(bounds.getMinX() - 1, yCoord, z)) {
                return null;
            }
            if(isValidBoilerBase(bounds.getMaxX() + 1, yCoord, z)) {
                return null;
            }
        }

        //check boiler tank
        if(tmpBounds.getWidthX() >= 2 && tmpBounds.getWidthZ() >= 2) {
            int height = -1;
            for(MultiBlockHelper.Bounds.BlockIterator.BoundsPos pos : tmpBounds) {
                int i = 1;
                while(isValidBoilerTank(pos.x, yCoord + i, pos.y)) {
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

    @Override
    protected void updateMaster() {

    }

    @Override
    protected void writeToNBTMaster(NBTTagCompound data) {

    }

    @Override
    protected void readFromNBTMaster(NBTTagCompound data) {

    }

    private boolean isValidBoilerBase(int x, int y, int z){
        Block block = worldObj.getBlock(x, y, z);
        return block instanceof IBoilerBaseBlock;
    }

    private boolean isValidBoilerTank(int x, int y, int z){
        Block block = worldObj.getBlock(x, y, z);
        return block instanceof BlockBoilerTank;
    }

}
