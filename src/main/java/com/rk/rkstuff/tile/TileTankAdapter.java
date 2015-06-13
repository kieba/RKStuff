package com.rk.rkstuff.tile;

import com.rk.rkstuff.block.BlockTank;
import com.rk.rkstuff.block.BlockTankAdapter;
import com.rk.rkstuff.helper.MultiBlockHelper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileTankAdapter extends TileMultiBlockMaster {
    private int maxStorage = 0;
    private int currentStorage = 0;
    private Fluid currentFluid;

    @Override
    protected boolean hasGui() {
        return false;
    }

    @Override
    public boolean checkMultiBlockForm() {
        return computeMultiStructureBounds() != null;
    }

    @Override
    protected MultiBlockHelper.Bounds setupStructure() {
        MultiBlockHelper.Bounds bounds = computeMultiStructureBounds();
        maxStorage = (bounds.getHeight() - 2) * (bounds.getWidthX() - 2) * (bounds.getWidthZ() - 2) * 32000;
        return bounds;
    }

    private MultiBlockHelper.Bounds computeMultiStructureBounds() {
        MultiBlockHelper.Bounds tmpBounds = new MultiBlockHelper.Bounds(xCoord, yCoord, zCoord);

        //get boiler base bounds
        for (ForgeDirection direction : new ForgeDirection[]{ForgeDirection.NORTH, ForgeDirection.EAST}) {
            int i = 1;
            while (isValidTankBlock(xCoord + direction.offsetX * i, yCoord, zCoord + direction.offsetZ * i)) {
                i++;
            }
            i--;
            tmpBounds.add(xCoord + direction.offsetX * i, yCoord, zCoord + direction.offsetZ * i);
            direction = direction.getOpposite();
            tmpBounds.add(xCoord + direction.offsetX * i, yCoord, zCoord + direction.offsetZ * i);
        }

        //calculate high
        int i = 0;
        while (isValidTankBlock(tmpBounds.getMinX(), yCoord + i, tmpBounds.getMinZ())) {
            i++;
        }
        tmpBounds.add(tmpBounds.getMinX(), yCoord + i, tmpBounds.getMinZ());

        //check if there are only BoilerBseBlocks within the bounds of the boiler base
        for (MultiBlockHelper.Bounds.BlockIterator.BoundsPos pos : tmpBounds) {
            if (pos.isBorder()) {
                if (!isValidTankBlock(pos.x, pos.y, pos.z)) {
                    return null;
                }
            } else {
                if (!(worldObj.getBlock(pos.x, pos.y, pos.z) == Blocks.air)) {
                    return null;
                }
            }
        }

        //check volume
        if (!(tmpBounds.getHeight() > 2 &&
                tmpBounds.getWidthX() > 2 &&
                tmpBounds.getWidthZ() > 2))
            return null;


        return tmpBounds;
    }

    private boolean isValidTankBlock(int x, int y, int z) {
        Block block = worldObj.getBlock(x, y, z);
        if (block instanceof BlockTankAdapter) {
            if (x == xCoord &&
                    y == yCoord &&
                    z == zCoord) {
                return true;
            }
        }
        return block instanceof BlockTank;
    }


    @Override
    protected void resetStructure() {

    }

    @Override
    protected void updateMaster() {

    }

    @Override
    public void readData(IOStream data) throws IOException {

    }

    @Override
    public void writeData(IOStream data) {

    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 4, zCoord + 1);
    }


}
