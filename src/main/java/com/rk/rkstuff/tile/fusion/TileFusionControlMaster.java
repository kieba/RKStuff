package com.rk.rkstuff.tile.fusion;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.block.fusion.IFusionCaseBlock;
import com.rk.rkstuff.block.fusion.IFusionControlCaseBlock;
import com.rk.rkstuff.block.fusion.IFusionControlCoreBlock;
import com.rk.rkstuff.block.fusion.IFusionCoreBlock;
import com.rk.rkstuff.helper.FusionHelper;
import com.rk.rkstuff.helper.MultiBlockHelper;
import com.rk.rkstuff.helper.Pos;
import com.rk.rkstuff.helper.RKLog;
import com.rk.rkstuff.tile.TileMultiBlockMaster;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileFusionControlMaster extends TileMultiBlockMaster {

    private FusionHelper.FusionStructure setup;

    public TileFusionControlMaster() {
        setup = new FusionHelper.FusionStructure();
        setup.ringStart = new Pos(484, 6, 69);
        setup.ringEnd = new Pos(478, 6, 69);
        setup.startDir = FusionHelper.FusionCoreDir.XpZz;
        setup.lengths[0] = 5;
        setup.lengths[1] = 5;
        setup.lengths[2] = 12;
        setup.lengths[3] = 10;
        setup.lengths[4] = 13;
        setup.lengths[5] = 7;
        setup.lengths[6] = 11;
        setup.lengths[7] = 9;
        setup.lengths[8] = 6;
        setup.isClockwise = false;
    }

    @Override
    protected boolean hasGui() {
        return false;
    }

    @Override
    public boolean checkMultiBlockForm() {
        return createFusionStructure() != null;
    }

    public void onBlockActivated() {
        FusionHelper.iterateRing(setup, new FusionHelper.IFusionVisitor() {
            @Override
            public boolean visit(FusionHelper.FusionPos pos) {
                if (pos.isCore) {
                    worldObj.setBlock(pos.p.x, pos.p.y, pos.p.z, RkStuff.blockFusionCore);
                } else if (pos.isCase) {
                    worldObj.setBlock(pos.p.x, pos.p.y, pos.p.z, RkStuff.blockFusionCase);
                } else {
                    worldObj.setBlock(pos.p.x, pos.p.y, pos.p.z, Blocks.air);
                }
                return true;
            }
        });

    }

    @Override
    protected MultiBlockHelper.Bounds setupStructure() {
        setup = createFusionStructure();

        RKLog.info("Setup FusionCore structure!!!!!");


        return null;
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

    private FusionHelper.FusionStructure createFusionStructure() {
        FusionHelper.FusionStructure fs = new FusionHelper.FusionStructure();
        if (!createFusionControl(fs)) return null;
        if (!createFusionRing(fs)) return null;
        return fs;
    }

    private boolean createFusionControl(FusionHelper.FusionStructure fs) {
        MultiBlockHelper.Bounds tmpBounds = new MultiBlockHelper.Bounds(xCoord, yCoord, zCoord);

        //get fusion control base bounds
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            int i = 1;
            while (isValidControlBlock(xCoord + direction.offsetX * i, yCoord + direction.offsetY * i, zCoord + direction.offsetZ * i)) {
                i++;
            }
            i--;
            tmpBounds.add(xCoord + direction.offsetX * i, yCoord + direction.offsetY * i, zCoord + direction.offsetZ * i);
        }

        //check the size of the fusion control base
        if (tmpBounds.getHeight() != 5) return false;
        boolean isValid = false;
        if (tmpBounds.getWidthX() == 5 && tmpBounds.getWidthZ() >= 5) {
            isValid = true;
        }
        if (!isValid && tmpBounds.getWidthZ() == 5 && tmpBounds.getWidthX() >= 5) {
            isValid = true;
        }

        if (!isValid) return false;

        fs.controlBounds = tmpBounds;

        //check if there are other FusionControlBlocks around the control base, if true don't build the structure
        //and check if the fusion control base is surrounded by IFusionCaseBlocks
        //and check if there are only IFusionCoreBlocks in the fusion control base

        isValid = FusionHelper.iterateControl(fs, new FusionHelper.IFusionVisitor() {
            @Override
            public boolean visit(FusionHelper.FusionPos pos) {
                if (pos.isCore && !isValidControlCoreBlock(pos.p.x, pos.p.y, pos.p.z)) {
                    return false;
                } else if (pos.isCase && !isValidControlCaseBlock(pos.p.x, pos.p.y, pos.p.z)) {
                    return false;
                } else if (isValidControlBlock(pos.p.x, pos.p.y, pos.p.z)) {
                    return false;
                }
                return true;
            }
        });

        return isValid;


        /*
        int xMin = tmpBounds.getMinX() - 1;
        int yMin = tmpBounds.getMinY() - 1;
        int zMin = tmpBounds.getMinZ() - 1;
        int xMax = tmpBounds.getMaxX() + 1;
        int yMax = tmpBounds.getMaxY() + 1;
        int zMax = tmpBounds.getMaxZ() + 1;
        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                for (int z = zMin; z <= zMax; z++) {
                    if (x == xCoord && y == yCoord && z == zCoord) continue;// don't check master block

                    //check the blocks around the fusion control base
                    if (x == xMin || x == xMax || y == yMin || y == yMax || z == zMin || z == zMax) {
                        if (isValidControlBlock(x, y, z)) {
                            return false;
                        }
                        continue;
                    }

                    //check the case of the fusion control base
                    if (x == (xMin + 1) || x == (xMax - 1) || y == (yMin + 1) || y == (yMax - 1) || z == (zMin + 1) || z == (zMax - 1)) {
                        if (!isValidControlCaseBlock(x, y, z)) {
                            return false;
                        }
                        continue;
                    }

                    //check the blocks inside the fusion control base
                    if (!isValidControlCoreBlock(x, y, z)) {
                        return false;
                    }
                }
            }
        }
        */
    }

    private boolean createFusionRing(FusionHelper.FusionStructure fs) {
        //first we search for IFusionCoreBlocks and compute the bounds for the IFusionCoreBlocks
        int y = fs.controlBounds.getMinY() + 2; //the fusion control base has a height of 5
        boolean isValid = false;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (dir == ForgeDirection.DOWN) continue;
            if (dir == ForgeDirection.UP) continue;

            if (dir.offsetX > 0) {
                int z = fs.controlBounds.getMinZ() + 2;
                if (followFusionCoreBlocks(fs, fs.controlBounds.getMaxX() + 1, y, z, fs.controlBounds.getMinX() - 1, y, z, FusionHelper.FusionCoreDir.XpZz)) {
                    isValid = true;
                    break;
                }
            } else if (dir.offsetZ > 0) {
                int x = fs.controlBounds.getMinX() + 2;
                if (followFusionCoreBlocks(fs, x, y, fs.controlBounds.getMaxZ() + 1, x, y, fs.controlBounds.getMinZ() - 1, FusionHelper.FusionCoreDir.XzZp)) {
                    isValid = true;
                    break;
                }
            }
        }

        if (!isValid) return false;

        isValid = FusionHelper.iterateRing(fs, new FusionHelper.IFusionVisitor() {
            @Override
            public boolean visit(FusionHelper.FusionPos pos) {
                if (pos.isCore) {
                    return isValidCoreBlock(pos.p.x, pos.p.y, pos.p.z);
                } else if (pos.isCase) {
                    return isValidCaseBlock(pos.p.x, pos.p.y, pos.p.z);
                } else {
                    return !(isValidCoreBlock(pos.p.x, pos.p.y, pos.p.z) || isValidCaseBlock(pos.p.x, pos.p.y, pos.p.z));
                }
            }
        });

        return isValid;
    }

    private boolean followFusionCoreBlocks(FusionHelper.FusionStructure fs, int xSrc, int ySrc, int zSrc, int xDst, int yDst, int zDst, FusionHelper.FusionCoreDir dir) {
        if (!isValidCoreBlock(xSrc, ySrc, zSrc)) return false;
        if (!isValidCoreBlock(xDst, yDst, zDst)) return false;
        fs.ringStart = new Pos(xSrc, ySrc, zSrc);
        fs.ringEnd = new Pos(xDst, yDst, zDst);
        fs.startDir = dir;

        int index = 0;
        int length = 0;
        boolean isValid = false;
        for (int i = 0; i < 2; i++) {
            index = 0;
            dir = fs.startDir;
            Pos current = new Pos(xSrc - dir.xOff, ySrc, zSrc - dir.zOff);

            while (index < fs.lengths.length) {
                length = 0;
                while (isValidCoreBlock(current.x + dir.xOff, ySrc, current.z + dir.zOff)) {
                    length++;
                    current.x += dir.xOff;
                    current.z += dir.zOff;

                    if (index == (setup.lengths.length - 1)) {
                        if (current.equals(fs.ringEnd)) {
                            isValid = true;
                            break;
                        }
                    }
                }

                length = (index % 2 == 1) ? length + 1 : length - 1;
                if (index == 0 || index == 8) {//the side with the fusion base can be shorter
                    if (length < 2) {
                        break;
                    }
                } else {
                    if (length < 5) {
                        break;
                    }
                }

                fs.lengths[index] = length;
                index++;
                dir = dir.getNext(i == 0); //first check clockwise, if it fails check other direction
            }
            if (isValid) break;
        }

        return isValid;
    }

    private boolean isValidControlBlock(int x, int y, int z) {
        Block b = worldObj.getBlock(x, y, z);
        return b instanceof IFusionControlCoreBlock || b instanceof IFusionControlCaseBlock;
    }

    private boolean isValidControlCoreBlock(int x, int y, int z) {
        Block b = worldObj.getBlock(x, y, z);
        return b instanceof IFusionControlCoreBlock;
    }

    private boolean isValidControlCaseBlock(int x, int y, int z) {
        Block b = worldObj.getBlock(x, y, z);
        return b instanceof IFusionControlCaseBlock;
    }

    private boolean isValidCoreBlock(int x, int y, int z) {
        Block b = worldObj.getBlock(x, y, z);
        return b instanceof IFusionCoreBlock;
    }

    private boolean isValidCaseBlock(int x, int y, int z) {
        Block b = worldObj.getBlock(x, y, z);
        return b instanceof IFusionCaseBlock;
    }

}
