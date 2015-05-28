package com.rk.rkstuff.tile;

import com.rk.rkstuff.block.ISolarBlock;
import com.rk.rkstuff.helper.MultiBlockHelper;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class TileSolarMaster extends TileMultiBlockMaster {

    private int count;

    @Override
    protected boolean checkMultiBlockForm() {
        return computeMultiStructureBounds() != null;
    }

    @Override
    protected MultiBlockHelper.Bounds setupStructure() {
        MultiBlockHelper.Bounds tmpBounds = computeMultiStructureBounds();
        for(int x = bounds.getMinX(); x <= bounds.getMaxX(); x++){
            for(int z = bounds.getMinZ(); z <= bounds.getMaxZ(); z++){
                //TODO: Set Metadata
            }
        }
        return tmpBounds;
    }

    private MultiBlockHelper.Bounds computeMultiStructureBounds(){
        MultiBlockHelper.Bounds tmpBounds = new MultiBlockHelper.Bounds(xCoord, yCoord, zCoord);
        for(ForgeDirection direction : ForgeDirection.values()){
            if(direction == ForgeDirection.UP) continue;
            if(direction == ForgeDirection.DOWN) continue;

            int i = 0;
            while (isValidMultiblock(xCoord + direction.offsetX * i, yCoord  + direction.offsetY * i, zCoord  + direction.offsetZ * i)) {
                i++;
            }
            tmpBounds.add(xCoord + direction.offsetX * i, yCoord  + direction.offsetY * i, zCoord  + direction.offsetZ * i);
        }

        boolean isValid = true;
        for(int x = bounds.getMinX(); x <= bounds.getMaxX(); x++){
            for(int z = bounds.getMinZ(); z <= bounds.getMaxZ(); z++){
                if(!isValidMultiblock(x, yCoord, z))
                {
                    isValid = false;
                    break;
                }
            }
            if(!isValid) break;
        }

        if(isValid){
            count = tmpBounds.getWidthX() * tmpBounds.getWidthZ();
            return tmpBounds;
        }else {
            return null;
        }
    }

    @Override
    public void resetStructure() {
        if(bounds != null){
            for(int x = bounds.getMinX(); x <= bounds.getMaxX(); x++){
                for(int z = bounds.getMinZ(); z <= bounds.getMaxZ(); z++){
                    //TODO: Set Metadata
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

    private boolean isValidMultiblock(int x, int y, int z){
        Block block = worldObj.getBlock(x, y, z);
        return block instanceof ISolarBlock;
    }
}
