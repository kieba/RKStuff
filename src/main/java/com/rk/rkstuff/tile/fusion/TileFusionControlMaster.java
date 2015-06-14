package com.rk.rkstuff.tile.fusion;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.helper.FusionHelper;
import com.rk.rkstuff.helper.MultiBlockHelper;
import com.rk.rkstuff.helper.Pos;
import com.rk.rkstuff.helper.RKLog;
import com.rk.rkstuff.tile.IMultiBlockMasterListener;
import com.rk.rkstuff.tile.TileMultiBlockMaster;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileFusionControlMaster extends TileMultiBlockMaster {

    private FusionHelper.FusionStructure setup;

    @Override
    protected boolean hasGui() {
        return false;
    }

    @Override
    public boolean checkMultiBlockForm() {
        return createFusionStructure() != null;
    }

    public void onBlockActivated(boolean shift) {
        /*
        if (!shift) {
            FusionHelper.iterateControl(setup, new FusionHelper.IFusionPosVisitor() {
                @Override
                public boolean visit(FusionHelper.FusionPos pos) {
                    if (pos.isCore) {
                        worldObj.setBlock(pos.p.x, pos.p.y, pos.p.z, Blocks.air);
                    } else if (pos.isCase) {
                        worldObj.setBlock(pos.p.x, pos.p.y, pos.p.z, Blocks.air);
                    } else {
                        worldObj.setBlock(pos.p.x, pos.p.y, pos.p.z, Blocks.air);
                    }
                    return true;
                }
            });

            FusionHelper.iterateRing(setup, new FusionHelper.IFusionPosVisitor() {
                @Override
                public boolean visit(FusionHelper.FusionPos pos) {
                    if (pos.isCore) {
                        worldObj.setBlock(pos.p.x, pos.p.y, pos.p.z, Blocks.air);
                    } else if (pos.isCase) {
                        worldObj.setBlock(pos.p.x, pos.p.y, pos.p.z, Blocks.air);
                    } else {
                        worldObj.setBlock(pos.p.x, pos.p.y, pos.p.z, Blocks.air);
                    }
                    return true;
                }
            });
        } else if (setup != null) {
            FusionHelper.iterateControl(setup, new FusionHelper.IFusionPosVisitor() {
                @Override
                public boolean visit(FusionHelper.FusionPos pos) {
                    if (pos.isCore) {
                        worldObj.setBlock(pos.p.x, pos.p.y, pos.p.z, RkStuff.blockFusionControlCore);
                    } else if (pos.isCase) {
                        worldObj.setBlock(pos.p.x, pos.p.y, pos.p.z, RkStuff.blockFusionControlCase);
                    }
                    return true;
                }
            });

            FusionHelper.iterateRing(setup, new FusionHelper.IFusionPosVisitor() {
                @Override
                public boolean visit(FusionHelper.FusionPos pos) {
                    if (pos.isCore) {
                        worldObj.setBlock(pos.p.x, pos.p.y, pos.p.z, RkStuff.blockFusionCore);
                    } else if (pos.isCase) {
                        worldObj.setBlock(pos.p.x, pos.p.y, pos.p.z, RkStuff.blockFusionCase);
                    }
                    return true;
                }
            });
        }
        */
    }

    @Override
    protected MultiBlockHelper.Bounds setupStructure() {
        setup = createFusionStructure();
        RKLog.info("Setup FusionCore structure!!!!!");

        if (setup != null) {
            FusionHelper.iterateControl(setup, new FusionHelper.IFusionPosVisitor() {
                @Override
                public boolean visit(FusionHelper.FusionPos pos) {
                    if (pos.isCore || pos.isCase) {
                        worldObj.setBlockMetadataWithNotify(pos.p.x, pos.p.y, pos.p.z, 1, 2);
                        TileFusionControlMaster.this.registerMaster(pos.p.x, pos.p.y, pos.p.z, 1);
                    }
                    return true;
                }
            });
            FusionHelper.iterateRing(setup, new FusionHelper.IFusionPosVisitor() {
                @Override
                public boolean visit(FusionHelper.FusionPos pos) {
                    if (pos.isCore) {
                        worldObj.setBlockMetadataWithNotify(pos.p.x, pos.p.y, pos.p.z, 1, 2);
                    } else if (pos.isCase) {
                        if (pos.isBevelBlock) {
                            Block b = worldObj.getBlock(pos.p.x, pos.p.y, pos.p.z);
                            if (b != pos.bevelBlock) {
                                worldObj.setBlock(pos.p.x, pos.p.y, pos.p.z, pos.bevelBlock, pos.bevelMeta, 2);
                            }
                        } else {
                            worldObj.setBlockMetadataWithNotify(pos.p.x, pos.p.y, pos.p.z, 1, 2);
                            TileFusionControlMaster.this.registerMaster(pos.p.x, pos.p.y, pos.p.z, 1);
                        }
                    }
                    return true;
                }
            });
        }

        return null;
    }

    private void registerMaster(int x, int y, int z, int meta) {
        Block b = worldObj.getBlock(x, y, z);
        if (b.hasTileEntity(meta)) {
            TileEntity tile = worldObj.getTileEntity(x, y, z);
            if (tile instanceof IMultiBlockMasterListener) {
                ((IMultiBlockMasterListener) tile).registerMaster(this);
            }
        }
    }

    private void unregisterMaster(int x, int y, int z, int meta) {
        Block b = worldObj.getBlock(x, y, z);
        if (b.hasTileEntity(meta)) {
            TileEntity tile = worldObj.getTileEntity(x, y, z);
            if (tile instanceof IMultiBlockMasterListener) {
                ((IMultiBlockMasterListener) tile).unregisterMaster();
            }
        }
    }

    @Override
    protected void resetStructure() {
        RKLog.info("Reset FusionCore structure!!!!!");
        FusionHelper.iterateControl(setup, new FusionHelper.IFusionPosVisitor() {
            @Override
            public boolean visit(FusionHelper.FusionPos pos) {
                worldObj.setBlockMetadataWithNotify(pos.p.x, pos.p.y, pos.p.z, 0, 2);
                TileFusionControlMaster.this.unregisterMaster(pos.p.x, pos.p.y, pos.p.z, 0);
                return true;
            }
        });

        FusionHelper.iterateRing(setup, new FusionHelper.IFusionPosVisitor() {
            @Override
            public boolean visit(FusionHelper.FusionPos pos) {
                if (pos.isBevelBlock) {
                    if (!worldObj.isAirBlock(pos.p.x, pos.p.y, pos.p.z)) {
                        worldObj.setBlock(pos.p.x, pos.p.y, pos.p.z, RkStuff.blockFusionCase, 0, 2);
                    }
                } else if (pos.isCase || pos.isCore) {
                    worldObj.setBlockMetadataWithNotify(pos.p.x, pos.p.y, pos.p.z, 0, 2);
                    TileFusionControlMaster.this.unregisterMaster(pos.p.x, pos.p.y, pos.p.z, 0);
                }
                return true;
            }
        });
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

    public boolean checkFusionControl() {
        if (!isBuild()) return false;
        boolean isValid = FusionHelper.iterateControl(setup, new FusionHelper.IFusionPosVisitor() {
            @Override
            public boolean visit(FusionHelper.FusionPos pos) {
                if (pos.isCore) {
                    return FusionHelper.isValidControlCoreBlock(worldObj, pos.p.x, pos.p.y, pos.p.z);
                } else if (pos.isCase) {
                    return FusionHelper.isValidControlCaseBlock(worldObj, pos.p.x, pos.p.y, pos.p.z);
                } else {
                    return !FusionHelper.isValidCaseOrCoreControlBlock(worldObj, pos.p.x, pos.p.y, pos.p.z);
                }
            }
        });
        return isValid;
    }

    public boolean checkFusionRing() {
        if (!isBuild()) return false;
        boolean isValid = FusionHelper.iterateRing(setup, new FusionHelper.IFusionPosVisitor() {
            @Override
            public boolean visit(FusionHelper.FusionPos pos) {
                if (pos.isCore) {
                    return FusionHelper.isValidCoreBlock(worldObj, pos.p.x, pos.p.y, pos.p.z);
                } else if (pos.isCase) {
                    if (pos.isBevelBlock) {
                        return worldObj.getBlock(pos.p.x, pos.p.y, pos.p.z) == RkStuff.blockFusionCase;
                    } else {
                        return FusionHelper.isValidCaseBlock(worldObj, pos.p.x, pos.p.y, pos.p.z);
                    }
                } else {
                    return !FusionHelper.isValidCaseOrCoreBlock(worldObj, pos.p.x, pos.p.y, pos.p.z);
                }
            }
        });
        return isValid;
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
            while (FusionHelper.isValidControlBlock(worldObj, xCoord + direction.offsetX * i, yCoord + direction.offsetY * i, zCoord + direction.offsetZ * i)) {
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
        isValid = FusionHelper.iterateControl(fs, new FusionHelper.IFusionPosVisitor() {
            @Override
            public boolean visit(FusionHelper.FusionPos pos) {
                if (pos.isCore) {
                    return FusionHelper.isValidControlCoreBlock(worldObj, pos.p.x, pos.p.y, pos.p.z);
                } else if (pos.isCase) {
                    return FusionHelper.isValidControlCaseBlock(worldObj, pos.p.x, pos.p.y, pos.p.z);
                } else {
                    return !FusionHelper.isValidCaseOrCoreControlBlock(worldObj, pos.p.x, pos.p.y, pos.p.z);
                }
            }
        });

        return isValid;
    }

    private boolean createFusionRing(FusionHelper.FusionStructure fs) {
        //first we search for IFusionCoreBlocks and compute the bounds for the IFusionCoreBlocks
        int y = fs.controlBounds.getMinY() + 2; //the fusion control base has a height of 5
        boolean isValid = false;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (dir == ForgeDirection.DOWN) continue;
            if (dir == ForgeDirection.UP) continue;

            if (dir.offsetX == 1) {
                int z = fs.controlBounds.getMinZ() + 2;
                if (followFusionCoreBlocks(fs, fs.controlBounds.getMaxX() + 1, y, z, fs.controlBounds.getMinX() - 1, y, z, FusionHelper.FusionCoreDir.XpZz)) {
                    isValid = true;
                    break;
                }
            } else if (dir.offsetZ == 1) {
                int x = fs.controlBounds.getMinX() + 2;
                if (followFusionCoreBlocks(fs, x, y, fs.controlBounds.getMaxZ() + 1, x, y, fs.controlBounds.getMinZ() - 1, FusionHelper.FusionCoreDir.XzZp)) {
                    isValid = true;
                    break;
                }
            } else if (dir.offsetX == -1) {
                int z = fs.controlBounds.getMinZ() + 2;
                if (followFusionCoreBlocks(fs, fs.controlBounds.getMinX() - 1, y, z, fs.controlBounds.getMaxX() + 1, y, z, FusionHelper.FusionCoreDir.XnZz)) {
                    isValid = true;
                    break;
                }
            } else if (dir.offsetZ == -1) {
                int x = fs.controlBounds.getMinX() + 2;
                if (followFusionCoreBlocks(fs, x, y, fs.controlBounds.getMinZ() - 1, x, y, fs.controlBounds.getMaxZ() + 1, FusionHelper.FusionCoreDir.XzZn)) {
                    isValid = true;
                    break;
                }
            }
        }

        if (!isValid) return false;

        isValid = FusionHelper.iterateRing(fs, new FusionHelper.IFusionPosVisitor() {
            @Override
            public boolean visit(FusionHelper.FusionPos pos) {
                if (pos.isCore) {
                    return FusionHelper.isValidCoreBlock(worldObj, pos.p.x, pos.p.y, pos.p.z);
                } else if (pos.isCase) {
                    return FusionHelper.isValidCaseBlock(worldObj, pos.p.x, pos.p.y, pos.p.z);
                } else {
                    return !FusionHelper.isValidCaseOrCoreBlock(worldObj, pos.p.x, pos.p.y, pos.p.z);
                }
            }
        });

        return isValid;
    }

    private boolean followFusionCoreBlocks(FusionHelper.FusionStructure fs, int xSrc, int ySrc, int zSrc, int xDst, int yDst, int zDst, FusionHelper.FusionCoreDir dir) {
        if (!FusionHelper.isValidCoreBlock(worldObj, xSrc, ySrc, zSrc)) return false;
        if (!FusionHelper.isValidCoreBlock(worldObj, xDst, yDst, zDst)) return false;
        fs.ringStart = new Pos(xSrc, ySrc, zSrc);
        fs.ringEnd = new Pos(xDst, yDst, zDst);
        fs.startDir = dir;
        int index = 0;
        int length = 0;
        boolean isValid = false;
        Pos current = new Pos(xSrc - dir.xOff, ySrc, zSrc - dir.zOff);
        while (index < fs.lengths.length) {
            length = 0;
            while (FusionHelper.isValidCoreBlock(worldObj, current.x + dir.xOff, ySrc, current.z + dir.zOff)) {
                length++;
                current.x += dir.xOff;
                current.z += dir.zOff;

                if (index == (fs.lengths.length - 1)) {
                    if (current.equals(fs.ringEnd)) {
                        length++;
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
            dir = dir.getNext(false);
        }

        return isValid;
    }



}
