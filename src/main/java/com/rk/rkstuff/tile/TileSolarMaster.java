package com.rk.rkstuff.tile;

import com.rk.rkstuff.block.*;
import com.rk.rkstuff.helper.MultiBlockHelper;
import com.rk.rkstuff.helper.Pos;
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
        for(MultiBlockHelper.Bounds.BlockIterator.BoundsPos pos : tmpBounds){
            boolean hasNorth = pos.hasBlock(ForgeDirection.NORTH);
            boolean hasEast = pos.hasBlock(ForgeDirection.EAST);
            boolean hasSouth = pos.hasBlock(ForgeDirection.SOUTH);
            boolean hasWest = pos.hasBlock(ForgeDirection.WEST);

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
                masterListener.registerMaster(getPosition());
            }
        }
        return tmpBounds;
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
            count = tmpBounds.getWidthX() * tmpBounds.getWidthZ();
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

    private boolean isMasterBlock(int x, int y, int z) {
        Block block = worldObj.getBlock(x, y, z);
        return block instanceof BlockSolarMaster;
    }
}
