package com.rk.rkstuff.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileMultiBlockTest extends TileMultiBlock {

    @Override
    public boolean checkMultiBlockForm() {
        int i = 0;
        // Scan a 3x3x3 area, starting with the bottom left corner
        for (int x = xCoord - 1; x < xCoord + 2; x++)
            for (int y = yCoord; y < yCoord + 3; y++)
                for (int z = zCoord - 1; z < zCoord + 2; z++) {
                    TileEntity tile = worldObj.getTileEntity(x, y, z);
                    // Make sure tile isn't null, is an instance of the same Tile, and isn't already a part of a multiblock
                    if (tile != null && (tile instanceof TileMultiBlock)) {
                        if (this.isMaster()) {
                            if (((TileMultiBlock)tile).hasMaster())
                                i++;
                        } else if (!((TileMultiBlock)tile).hasMaster())
                            i++;
                    }
                }
        // check if there are 26 blocks present ((3*3*3) - 1) and check that center block is empty
        return i > 25 && worldObj.isAirBlock(xCoord, yCoord + 1, zCoord);
    }

    @Override
    public void setupStructure() {
        for (int x = xCoord - 1; x < xCoord + 2; x++)
            for (int y = yCoord; y < yCoord + 3; y++)
                for (int z = zCoord - 1; z < zCoord + 2; z++) {
                    TileEntity tile = worldObj.getTileEntity(x, y, z);
                    // Check if block is bottom center block
                    boolean master = (x == xCoord && y == yCoord && z == zCoord);
                    if (tile != null && (tile instanceof TileMultiBlock)) {
                        ((TileMultiBlock) tile).setMasterCoords(xCoord, yCoord, zCoord);
                        ((TileMultiBlock) tile).setHasMaster(true);
                        ((TileMultiBlock) tile).setIsMaster(master);
                    }
                }
    }

    @Override
    public void resetStructure() {
        for (int x = xCoord - 1; x < xCoord + 2; x++) {
            for (int y = yCoord; y < yCoord + 3; y++) {
                for (int z = zCoord - 1; z < zCoord + 2; z++) {
                    TileEntity tile = worldObj.getTileEntity(x, y, z);
                    if (tile != null && (tile instanceof TileMultiBlock))
                        ((TileMultiBlock) tile).reset();
                }
            }
        }
    }

    @Override
    protected void writeToNBTMaster(NBTTagCompound data) {

    }

    @Override
    protected void readFromNBTMaster(NBTTagCompound data) {

    }

    @Override
    protected void updateMaster() {

    }
}
