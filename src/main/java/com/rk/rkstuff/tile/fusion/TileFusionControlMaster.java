package com.rk.rkstuff.tile.fusion;

import com.rk.rkstuff.block.fusion.IFusionCaseBlock;
import com.rk.rkstuff.block.fusion.IFusionControlCaseBlock;
import com.rk.rkstuff.block.fusion.IFusionControlCoreBlock;
import com.rk.rkstuff.block.fusion.IFusionCoreBlock;
import com.rk.rkstuff.helper.FusionHelper;
import com.rk.rkstuff.helper.MultiBlockHelper;
import com.rk.rkstuff.helper.Pos;
import com.rk.rkstuff.tile.TileMultiBlockMaster;
import net.minecraft.block.Block;
import net.minecraftforge.common.util.ForgeDirection;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileFusionControlMaster extends TileMultiBlockMaster {

    private FusionHelper.FusionCoreSetup setup;

    @Override
    protected boolean hasGui() {
        return false;
    }

    @Override
    public boolean checkMultiBlockForm() {
        MultiBlockHelper.Bounds fusionControlBounds = computeControlBounds();
        if (fusionControlBounds == null) return false;
        return computeFusionBounds(fusionControlBounds) != null;
    }

    @Override
    protected MultiBlockHelper.Bounds setupStructure() {
        MultiBlockHelper.Bounds fusionControlBounds = computeControlBounds();
        if (fusionControlBounds == null) return null;
        setup = computeFusionBounds(fusionControlBounds);
        return fusionControlBounds;
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

    private MultiBlockHelper.Bounds computeControlBounds() {
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
        if (tmpBounds.getHeight() == 5) {
            if (tmpBounds.getWidthX() == 5 && tmpBounds.getWidthZ() >= 5) {
                //fusion control base is to small
                return null;
            }
            if (tmpBounds.getWidthZ() == 5 && tmpBounds.getWidthX() >= 5) {
                //fusion control base is to small
                return null;
            }
        }

        //check if there are other FusionControlBlocks around the control base, if true don't build the structure
        //and check if the fusion control base is surrounded by IFusionCaseBlocks
        //and check if there are only IFusionCoreBlocks in the fusion control base
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

                    //check the case of the fusion control base
                    if (x == (xMin - 1) || x == (xMax + 1) || y == (yMin - 1) || y == (yMax + 1) || z == (zMin - 1) || z == (zMax + 1)) {
                        if (!isValidControlCaseBlock(x, y, z)) {
                            return null;
                        }
                        continue;
                    }


                    //check the blocks around the fusion control base
                    if (x == xMin || x == xMax || y == yMin || y == yMax || z == zMin || z == zMax) {
                        if (isValidControlBlock(x, y, z)) {
                            return null;
                        }
                        continue;
                    }

                    //check the blocks inside the fusion control base
                    if (!isValidControlCoreBlock(x, y, z)) {
                        return null;
                    }
                }
            }
        }

        return tmpBounds;
    }

    private FusionHelper.FusionCoreSetup computeFusionBounds(MultiBlockHelper.Bounds fusionControlBounds) {
        if (fusionControlBounds == null) return null;
        FusionHelper.FusionCoreSetup setup = null;

        //first we search for IFusionCoreBlocks and compute the bounds for the IFusionCoreBlocks
        int y = fusionControlBounds.getMinY() + 3; //the fusion control base has a height of 5
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (dir == ForgeDirection.DOWN) continue;
            if (dir == ForgeDirection.UP) continue;

            if (dir.offsetX > 0) {
                int z = fusionControlBounds.getMinZ() + 3;
                setup = followFusionCoreBlocks(fusionControlBounds.getMaxX() + 1, y, z, fusionControlBounds.getMinX() - 1, y, z, FusionHelper.FusionCoreDir.XpZz);
                if (bounds != null) break;
            } else if (dir.offsetZ > 0) {
                int x = fusionControlBounds.getMinX() + 3;
                setup = followFusionCoreBlocks(x, y, fusionControlBounds.getMaxZ() + 1, x, y, fusionControlBounds.getMinZ() - 1, FusionHelper.FusionCoreDir.XzZp);
                if (bounds != null) break;
            }
        }

        if (setup == null) return null;

        boolean isComplete = FusionHelper.iterate(setup, new FusionHelper.IFusionRingVisitor() {
            @Override
            public boolean visit(FusionHelper.FusionRingPos pos) {
                if (pos.isCore) {
                    return isValidCoreBlock(pos.p.x, pos.p.y, pos.p.z);
                } else if (pos.isCase) {
                    return isValidCaseBlock(pos.p.x, pos.p.y, pos.p.z);
                } else {
                    return !(isValidCoreBlock(pos.p.x, pos.p.y, pos.p.z) || isValidCaseBlock(pos.p.x, pos.p.y, pos.p.z));
                }
            }
        });

        return isComplete ? setup : null;
    }

    private FusionHelper.FusionCoreSetup followFusionCoreBlocks(int xSrc, int ySrc, int zSrc, int xDst, int yDst, int zDst, FusionHelper.FusionCoreDir dir) {
        if (!isValidCoreBlock(xSrc, ySrc, zSrc)) return null;
        if (!isValidCoreBlock(xDst, yDst, zDst)) return null;
        FusionHelper.FusionCoreSetup setup = new FusionHelper.FusionCoreSetup();
        setup.src = new Pos(xSrc, ySrc, zSrc);
        setup.end = new Pos(xDst, yDst, zDst);
        setup.startDir = dir;
        Pos current = new Pos(xSrc - dir.xOff, ySrc, zSrc - dir.zOff);

        int index = 0;
        int length = 0;
        boolean isValid = false;
        for (int i = 0; i < 2; i++) {
            while (index < FusionHelper.FusionCoreDir.values().length) {
                while (isValidCoreBlock(current.x + dir.zOff, ySrc, current.z + dir.zOff)) {
                    length++;
                    current.x += dir.xOff;
                    current.z += dir.zOff;

                    if (index == (FusionHelper.FusionCoreDir.values().length - 1)) {
                        if (current.equals(setup.end)) {
                            isValid = true;
                            break;
                        }
                    }
                }
                length = (index % 2 == 1) ? length + 1 : length - 1;
                setup.lengths[index] = length;
                if (index != 0 && index != 8) {//the side with the fusion base can be shorter
                    if (length < 2) {
                        break;
                    }
                } else {
                    if (length < 5) {
                        break;
                    }
                }

                length = 0;
                index++;
                dir = dir.getNext(i == 0); //first check clockwise, if it fails check other direction
            }
            if (isValid) break;
        }

        return setup;
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
