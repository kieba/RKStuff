package com.rk.rkstuff.tank.tile;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.core.tile.TileMultiBlockMaster;
import com.rk.rkstuff.helper.MultiBlockHelper;
import com.rk.rkstuff.tank.block.*;
import com.rk.rkstuff.util.RKLog;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileTankAdapter extends TileMultiBlockMaster {
    private int maxStorage = 0;
    private int currentStorage = 18000;
    private Fluid currentFluid;

    @Override
    protected boolean hasGui() {
        return false;
    }

    @Override
    public boolean checkMultiBlockForm() {
        return computeMultiStructureBounds() != null;
    }

    public int getInnerRadius() {
        if (bounds == null) return 0;
        return (bounds.getWidthX() - 2) / 2;
    }

    public int getOuterRadius() {
        if (bounds == null) return 0;
        return getInnerRadius() + 1;
    }

    public int getInnerHeight() {
        if (bounds == null) return 0;
        return (bounds.getHeight()) - 2;
    }

    public int getOuterHeight() {
        if (bounds == null) return 0;
        return bounds.getHeight();
    }

    public float getFillHeight() {
        if (maxStorage == 0) return 0;
        return getInnerHeight() * ((float) currentStorage / maxStorage);
    }

    @Override
    protected MultiBlockHelper.Bounds setupStructure() {
        MultiBlockHelper.Bounds bounds = computeMultiStructureBounds();
        for (MultiBlockHelper.Bounds.BlockIterator.BoundsPos pos : bounds) {
            if (pos.isEdge()) {
                int meta = 0;
                if (pos.y == bounds.getMinY()) meta |= (0x01 << 1);
                if (pos.x == bounds.getMinX()) meta |= 0x01;
                if (pos.z == bounds.getMinZ()) meta |= (0x01 << 2);

                worldObj.setBlock(pos.x, pos.y, pos.z, RkStuff.blockTankBevelSmall, meta, 2);
                continue;
            }
            if (!pos.isEdge() && pos.isBorder()) {
                if (pos.y == bounds.getMinY()) {
                    if (pos.x == bounds.getMinX()) {
                        worldObj.setBlock(pos.x, pos.y, pos.z, RkStuff.blockTankBevelLarge, 6, 2);
                        continue;
                    }
                    if (pos.x == bounds.getMaxX()) {
                        worldObj.setBlock(pos.x, pos.y, pos.z, RkStuff.blockTankBevelLarge, 7, 2);
                        continue;
                    }
                    if (pos.z == bounds.getMinZ()) {
                        worldObj.setBlock(pos.x, pos.y, pos.z, RkStuff.blockTankBevelLarge, 5, 2);
                        continue;
                    }
                    if (pos.z == bounds.getMaxZ()) {
                        worldObj.setBlock(pos.x, pos.y, pos.z, RkStuff.blockTankBevelLarge, 4, 2);
                        continue;
                    }
                }
                if (pos.y == bounds.getMaxY()) {
                    if (pos.x == bounds.getMinX()) {
                        worldObj.setBlock(pos.x, pos.y, pos.z, RkStuff.blockTankBevelLarge, 2, 2);
                        continue;
                    }
                    if (pos.x == bounds.getMaxX()) {
                        worldObj.setBlock(pos.x, pos.y, pos.z, RkStuff.blockTankBevelLarge, 3, 2);
                        continue;
                    }
                    if (pos.z == bounds.getMinZ()) {
                        worldObj.setBlock(pos.x, pos.y, pos.z, RkStuff.blockTankBevelLarge, 1, 2);
                        continue;
                    }
                    if (pos.z == bounds.getMaxZ()) {
                        worldObj.setBlock(pos.x, pos.y, pos.z, RkStuff.blockTankBevelLarge, 0, 2);
                        continue;
                    }
                }
                if (pos.x == bounds.getMinX() && pos.z == bounds.getMinZ()) {
                    worldObj.setBlock(pos.x, pos.y, pos.z, RkStuff.blockTankBevelLarge, 10, 2);
                    continue;
                }
                if (pos.x == bounds.getMinX() && pos.z == bounds.getMaxZ()) {
                    worldObj.setBlock(pos.x, pos.y, pos.z, RkStuff.blockTankBevelLarge, 8, 2);
                    continue;
                }
                if (pos.x == bounds.getMaxX() && pos.z == bounds.getMinZ()) {
                    worldObj.setBlock(pos.x, pos.y, pos.z, RkStuff.blockTankBevelLarge, 9, 2);
                    continue;
                }
                if (pos.x == bounds.getMaxX() && pos.z == bounds.getMaxZ()) {
                    worldObj.setBlock(pos.x, pos.y, pos.z, RkStuff.blockTankBevelLarge, 11, 2);
                    continue;
                }
                worldObj.setBlockMetadataWithNotify(pos.x, pos.y, pos.z, 1, 2);
            }

        }
        maxStorage = (bounds.getHeight() - 2) * (bounds.getWidthX() - 2) * (bounds.getWidthZ() - 2) * 1000;
        RKLog.info(bounds);
        RKLog.info("MaxStorage: " + maxStorage);
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
        i--;
        tmpBounds.add(tmpBounds.getMinX(), yCoord + i, tmpBounds.getMinZ());

        //check if there are only BoilerBseBlocks within the bounds of the boiler base
        for (MultiBlockHelper.Bounds.BlockIterator.BoundsPos pos : tmpBounds) {
            if (pos.isBorder()) {
                if (!isValidTankBlock(pos.x, pos.y, pos.z)) {
                    return null;
                }
            } else {
                if (!(worldObj.getBlock(pos.x, pos.y, pos.z).getMaterial() == Material.air)) {
                    return null;
                }
            }
        }

        //check volume
        if (!(tmpBounds.getHeight() > 2 &&
                tmpBounds.getWidthX() > 2 &&
                tmpBounds.getWidthZ() > 2))
            return null;

        //check border
        MultiBlockHelper.Bounds extendedBounds = tmpBounds.clone();
        extendedBounds.extendDirections(1);

        for (MultiBlockHelper.Bounds.BlockIterator.BoundsPos pos : extendedBounds) {
            if (!pos.isBorder()) continue;
            if (worldObj.getBlock(pos.x, pos.y, pos.z) instanceof ITankBlock) {
                return null;
            }
        }


        return tmpBounds;
    }

    private boolean isValidTankBlock(int x, int y, int z) {
        Block block = worldObj.getBlock(x, y, z);
        if (block instanceof BlockTankAdapter) {
            if (x == xCoord &&
                    y == yCoord &&
                    z == zCoord) {
                return true;
            } else {
                return false;
            }
        }
        return block instanceof ITankBlock;
    }


    @Override
    protected void resetStructure() {
        for (MultiBlockHelper.Bounds.BlockIterator.BoundsPos pos : bounds) {
            Block block = worldObj.getBlock(pos.x, pos.y, pos.z);
            if (block instanceof ITankBlock) {
                if (block instanceof BlockTankBevelSmall ||
                        block instanceof BlockTankBevelLarge) {
                    worldObj.setBlock(pos.x, pos.y, pos.z, RkStuff.blockTank);
                }
                worldObj.setBlockMetadataWithNotify(pos.x, pos.y, pos.z, 0, 2);

                if (block instanceof BlockTankValve ||
                        block instanceof BlockTankInteraction) {
                    //TODO: Do someting
                }
            }
        }
    }

    @Override
    protected void updateMaster() {

    }

    @Override
    public void readData(IOStream data) throws IOException {
        if (data.available() == 0) {
            bounds = null;
            currentStorage = 18000;
            maxStorage = 0;
            return;
        }
        bounds = new MultiBlockHelper.Bounds(0, 0, 0);
        bounds.readData(data);
        maxStorage = data.readFirstInt();
        currentStorage = data.readFirstInt();
    }

    @Override
    public void writeData(IOStream data) {
        if (bounds == null) return;
        bounds.writeData(data);
        data.writeLast(maxStorage);
        data.writeLast(currentStorage);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(xCoord - getInnerRadius(), yCoord, zCoord - getInnerRadius(), xCoord + getInnerRadius() + 1, yCoord + getInnerHeight() + 1, zCoord + getInnerRadius() + 1);
    }


}
