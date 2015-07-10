package com.rk.rkstuff.accelerator.tile;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.accelerator.Accelerator;
import com.rk.rkstuff.accelerator.AcceleratorConfig;
import com.rk.rkstuff.accelerator.AcceleratorHelper;
import com.rk.rkstuff.accelerator.IAccelerator;
import com.rk.rkstuff.coolant.CoolantStack;
import com.rk.rkstuff.core.tile.IMultiBlockMasterListener;
import com.rk.rkstuff.core.tile.TileMultiBlockMaster;
import com.rk.rkstuff.helper.MultiBlockHelper;
import com.rk.rkstuff.util.Pos;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import rk.com.core.io.IOStream;

import java.io.IOException;
import java.util.ArrayList;

public abstract class TileAcceleratorMaster extends TileMultiBlockMaster implements IAccelerator {

    private AcceleratorHelper.AcceleratorStructure setup;
    protected Accelerator accelerator;

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        accelerator.writeToNBT("accelerator", tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        accelerator = new Accelerator(this, Accelerator.DEFAULT_CONFIG);
        accelerator.readFromNBT("accelerator", tag);
    }

    @Override
    protected void updateMaster() {
        accelerator.update();

        for (int i = 0; i < AcceleratorConfig.ACCELERATOR_SIDE_COUNT; i++) {
            ArrayList<TileAcceleratorCaseFluidIO> fluidIOs = setup.fluidIOs.get(i);
            for (int j = 0; j < fluidIOs.size(); j++) {
                fluidIOs.get(i).handleOutput();
            }
        }
    }

    public CoolantStack getCoolantStack(int side) {
        return accelerator.getCoolant(side);
    }

    public int receiveCoolant(int side, int maxAmount, float temperature, boolean simulate) {
        return accelerator.receiveCoolant(side, maxAmount, temperature, simulate);
    }

    public int getSideForFluidIO(final TileAcceleratorCaseFluidIO tile) {
        final int[] side = {-1};
        AcceleratorHelper.iterateRingCore(setup, new AcceleratorHelper.IAcceleratorPosVisitor() {
            @Override
            public boolean visit(AcceleratorHelper.AcceleratorPos pos) {
                if (pos.p.x == tile.xCoord && pos.p.z == tile.zCoord) {
                    side[0] = pos.side;
                    return false;
                }
                return true;
            }
        });
        return side[0];
    }

    @Override
    public boolean checkMultiBlockForm() {
        return createAcceleratorStructure() != null;
    }

    public void onBlockActivated(boolean shift) {

    }

    @Override
    protected MultiBlockHelper.Bounds setupStructure() {
        setup = createAcceleratorStructure();
        for (int i = 0; i < AcceleratorConfig.ACCELERATOR_SIDE_COUNT; i++) {
            setup.fluidIOs.add(new ArrayList<TileAcceleratorCaseFluidIO>(1));
        }
        if (setup != null) {
            AcceleratorHelper.iterateRing(setup, new AcceleratorHelper.IAcceleratorPosVisitor() {
                @Override
                public boolean visit(AcceleratorHelper.AcceleratorPos pos) {
                    Block block = worldObj.getBlock(pos.p.x, pos.p.y, pos.p.z);
                    TileEntity tile = worldObj.getTileEntity(pos.p.x, pos.p.y, pos.p.z);

                    if (pos.isBevelBlock) {
                        worldObj.setBlock(pos.p.x, pos.p.y, pos.p.z, pos.bevelBlock, pos.bevelMeta, 2);
                        block = pos.bevelBlock;
                    } else {
                        worldObj.setBlockMetadataWithNotify(pos.p.x, pos.p.y, pos.p.z, 1, 2);
                    }

                    TileAcceleratorMaster.this.registerMaster(tile);
                    setup(pos, block, tile);
                    return true;
                }
            });
            AcceleratorHelper.iterateControl(setup, new AcceleratorHelper.IAcceleratorPosVisitor() {
                @Override
                public boolean visit(AcceleratorHelper.AcceleratorPos pos) {
                    Block block = worldObj.getBlock(pos.p.x, pos.p.y, pos.p.z);
                    TileEntity tile = worldObj.getTileEntity(pos.p.x, pos.p.y, pos.p.z);

                    worldObj.setBlockMetadataWithNotify(pos.p.x, pos.p.y, pos.p.z, 1, 2);

                    TileAcceleratorMaster.this.registerMaster(tile);
                    setup(pos, block, tile);
                    return true;
                }
            });
            accelerator.initialize(setup);
            return setup.controlBounds;
        }
        return null;
    }

    @Override
    protected void resetStructure() {
        AcceleratorHelper.iterateRing(setup, new AcceleratorHelper.IAcceleratorPosVisitor() {
            @Override
            public boolean visit(AcceleratorHelper.AcceleratorPos pos) {
                Block block = worldObj.getBlock(pos.p.x, pos.p.y, pos.p.z);
                TileEntity tile = worldObj.getTileEntity(pos.p.x, pos.p.y, pos.p.z);

                if (pos.isBevelBlock) {
                    worldObj.setBlock(pos.p.x, pos.p.y, pos.p.z, RkStuff.blockAcceleratorCase, 0, 2);
                } else if (pos.isCase || pos.isCore) {
                    worldObj.setBlockMetadataWithNotify(pos.p.x, pos.p.y, pos.p.z, 0, 2);
                }

                TileAcceleratorMaster.this.unregisterMaster(tile);
                reset(pos, block, tile);
                return true;
            }
        });
        AcceleratorHelper.iterateControl(setup, new AcceleratorHelper.IAcceleratorPosVisitor() {
            @Override
            public boolean visit(AcceleratorHelper.AcceleratorPos pos) {
                Block block = worldObj.getBlock(pos.p.x, pos.p.y, pos.p.z);
                TileEntity tile = worldObj.getTileEntity(pos.p.x, pos.p.y, pos.p.z);

                worldObj.setBlockMetadataWithNotify(pos.p.x, pos.p.y, pos.p.z, 0, 2);

                TileAcceleratorMaster.this.unregisterMaster(tile);
                reset(pos, block, tile);
                return true;
            }
        });
        accelerator.unInitialize();
        setup = null;
    }

    private void registerMaster(TileEntity tile) {
        if (tile instanceof IMultiBlockMasterListener) {
            ((IMultiBlockMasterListener) tile).registerMaster(this);
            if (tile instanceof TileAcceleratorCaseFluidIO) {
                int side = ((TileAcceleratorCaseFluidIO) tile).getSide();
                setup.fluidIOs.get(side).add((TileAcceleratorCaseFluidIO) tile);
            }
        }
    }

    private void unregisterMaster(TileEntity tile) {
        if (tile instanceof IMultiBlockMasterListener) {
            ((IMultiBlockMasterListener) tile).unregisterMaster();
        }
    }

    protected abstract void setup(AcceleratorHelper.AcceleratorPos pos, Block block, TileEntity tile);

    protected abstract void reset(AcceleratorHelper.AcceleratorPos pos, Block block, TileEntity tile);

    @Override
    public void readData(IOStream data) throws IOException {

    }

    @Override
    public void writeData(IOStream data) {

    }

    public boolean checkAcceleratorControl() {
        if (!isBuild()) return false;
        boolean isValid = AcceleratorHelper.iterateControl(setup, new AcceleratorHelper.IAcceleratorPosVisitor() {
            @Override
            public boolean visit(AcceleratorHelper.AcceleratorPos pos) {
                if (pos.isCore) {
                    return AcceleratorHelper.isValidControlCoreBlock(worldObj, pos.p.x, pos.p.y, pos.p.z);
                } else if (pos.isCase) {
                    return AcceleratorHelper.isValidControlCaseBlock(worldObj, pos.p.x, pos.p.y, pos.p.z);
                } else {
                    return !AcceleratorHelper.isValidCaseOrCoreControlBlock(worldObj, pos.p.x, pos.p.y, pos.p.z);
                }
            }
        });
        return isValid;
    }

    public boolean checkAcceleratorRing() {
        if (!isBuild()) return false;
        boolean isValid = AcceleratorHelper.iterateRing(setup, new AcceleratorHelper.IAcceleratorPosVisitor() {
            @Override
            public boolean visit(AcceleratorHelper.AcceleratorPos pos) {
                if (pos.isCore) {
                    return AcceleratorHelper.isValidCoreBlock(worldObj, pos.p.x, pos.p.y, pos.p.z);
                } else if (pos.isCase) {
                    if (pos.isBevelBlock) {
                        Block b = worldObj.getBlock(pos.p.x, pos.p.y, pos.p.z);
                        if (b == RkStuff.blockAcceleratorCaseBevelLarge) {
                            return true;
                        }
                        if (b == RkStuff.blockAcceleratorCaseBevelSmall) {
                            return true;
                        }
                        return b == RkStuff.blockAcceleratorCaseBevelSmallInverted;
                    } else {
                        return AcceleratorHelper.isValidCaseBlock(worldObj, pos.p.x, pos.p.y, pos.p.z);
                    }
                } else {
                    return !AcceleratorHelper.isValidCaseOrCoreBlock(worldObj, pos.p.x, pos.p.y, pos.p.z);
                }
            }
        });
        return isValid;
    }

    private AcceleratorHelper.AcceleratorStructure createAcceleratorStructure() {
        AcceleratorHelper.AcceleratorStructure as = new AcceleratorHelper.AcceleratorStructure();
        if (!createAcceleratorControl(as)) return null;
        if (!createAcceleratorRing(as)) return null;
        return as;
    }

    private boolean createAcceleratorControl(AcceleratorHelper.AcceleratorStructure as) {
        MultiBlockHelper.Bounds tmpBounds = new MultiBlockHelper.Bounds(xCoord, yCoord, zCoord);

        //get accelerator control base bounds
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            int i = 1;
            while (AcceleratorHelper.isValidControlBlock(worldObj, xCoord + direction.offsetX * i, yCoord + direction.offsetY * i, zCoord + direction.offsetZ * i)) {
                i++;
            }
            i--;
            tmpBounds.add(xCoord + direction.offsetX * i, yCoord + direction.offsetY * i, zCoord + direction.offsetZ * i);
        }

        //check the size of the accelerator control base
        if (tmpBounds.getHeight() != 5) return false;
        boolean isValid = false;
        if (tmpBounds.getWidthX() == 5 && tmpBounds.getWidthZ() >= 5) {
            isValid = true;
        }
        if (!isValid && tmpBounds.getWidthZ() == 5 && tmpBounds.getWidthX() >= 5) {
            isValid = true;
        }

        if (!isValid) return false;

        as.controlBounds = tmpBounds;

        //check if there are other AcceleratorControlBlocks around the control base, if true don't build the structure
        //and check if the accelerator control base is surrounded by IAcceleratorCaseBlocks
        //and check if there are only IAcceleratorCoreBlocks in the accelerator control base
        isValid = AcceleratorHelper.iterateControl(as, new AcceleratorHelper.IAcceleratorPosVisitor() {
            @Override
            public boolean visit(AcceleratorHelper.AcceleratorPos pos) {
                if (pos.isCore) {
                    return AcceleratorHelper.isValidControlCoreBlock(worldObj, pos.p.x, pos.p.y, pos.p.z);
                } else if (pos.isCase) {
                    return AcceleratorHelper.isValidControlCaseBlock(worldObj, pos.p.x, pos.p.y, pos.p.z);
                } else {
                    return !AcceleratorHelper.isValidCaseOrCoreControlBlock(worldObj, pos.p.x, pos.p.y, pos.p.z);
                }
            }
        });

        return isValid;
    }

    private boolean createAcceleratorRing(AcceleratorHelper.AcceleratorStructure as) {
        //first we search for IAcceleratorCoreBlocks and compute the bounds for the IAcceleratorCoreBlocks
        int y = as.controlBounds.getMinY() + 2; //the accelerator control base has a height of 5
        boolean isValid = false;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (dir == ForgeDirection.DOWN) continue;
            if (dir == ForgeDirection.UP) continue;

            if (dir.offsetX == 1) {
                int z = as.controlBounds.getMinZ() + 2;
                if (followAcceleratorCoreBlocks(as, as.controlBounds.getMaxX() + 1, y, z, as.controlBounds.getMinX() - 1, y, z, AcceleratorHelper.AcceleratorCoreDir.XpZz)) {
                    isValid = true;
                    break;
                }
            } else if (dir.offsetZ == 1) {
                int x = as.controlBounds.getMinX() + 2;
                if (followAcceleratorCoreBlocks(as, x, y, as.controlBounds.getMaxZ() + 1, x, y, as.controlBounds.getMinZ() - 1, AcceleratorHelper.AcceleratorCoreDir.XzZp)) {
                    isValid = true;
                    break;
                }
            } else if (dir.offsetX == -1) {
                int z = as.controlBounds.getMinZ() + 2;
                if (followAcceleratorCoreBlocks(as, as.controlBounds.getMinX() - 1, y, z, as.controlBounds.getMaxX() + 1, y, z, AcceleratorHelper.AcceleratorCoreDir.XnZz)) {
                    isValid = true;
                    break;
                }
            } else if (dir.offsetZ == -1) {
                int x = as.controlBounds.getMinX() + 2;
                if (followAcceleratorCoreBlocks(as, x, y, as.controlBounds.getMinZ() - 1, x, y, as.controlBounds.getMaxZ() + 1, AcceleratorHelper.AcceleratorCoreDir.XzZn)) {
                    isValid = true;
                    break;
                }
            }
        }

        if (!isValid) return false;

        isValid = AcceleratorHelper.iterateRing(as, new AcceleratorHelper.IAcceleratorPosVisitor() {
            @Override
            public boolean visit(AcceleratorHelper.AcceleratorPos pos) {
                if (pos.isCore) {
                    return AcceleratorHelper.isValidCoreBlock(worldObj, pos.p.x, pos.p.y, pos.p.z);
                } else if (pos.isCase) {
                    return AcceleratorHelper.isValidCaseBlock(worldObj, pos.p.x, pos.p.y, pos.p.z);
                } else {
                    return !AcceleratorHelper.isValidCaseOrCoreBlock(worldObj, pos.p.x, pos.p.y, pos.p.z);
                }
            }
        });

        return isValid;
    }

    private boolean followAcceleratorCoreBlocks(AcceleratorHelper.AcceleratorStructure as, int xSrc, int ySrc, int zSrc, int xDst, int yDst, int zDst, AcceleratorHelper.AcceleratorCoreDir dir) {
        if (!AcceleratorHelper.isValidCoreBlock(worldObj, xSrc, ySrc, zSrc)) return false;
        if (!AcceleratorHelper.isValidCoreBlock(worldObj, xDst, yDst, zDst)) return false;
        as.ringStart = new Pos(xSrc, ySrc, zSrc);
        as.ringEnd = new Pos(xDst, yDst, zDst);
        as.startDir = dir;
        int index = 0;
        int length = 0;
        boolean isValid = false;
        Pos current = new Pos(xSrc - dir.xOff, ySrc, zSrc - dir.zOff);
        while (index < as.lengths.length) {
            length = 0;
            while (AcceleratorHelper.isValidCoreBlock(worldObj, current.x + dir.xOff, ySrc, current.z + dir.zOff)) {
                length++;
                current.x += dir.xOff;
                current.z += dir.zOff;

                if (index == (as.lengths.length - 1)) {
                    if (current.equals(as.ringEnd)) {
                        length++;
                        isValid = true;
                        break;
                    }
                }
            }

            length = (index % 2 == 1) ? length + 1 : length - 1;
            if (index == 0 || index == AcceleratorConfig.ACCELERATOR_SIDE_COUNT - 1) {//the side with the accelerator base can be shorter
                if (length < AcceleratorConfig.MIN_START_END_LENGTH) {
                    break;
                }
            } else {
                if (length < AcceleratorConfig.MIN_SIDE_LENGTH) {
                    break;
                }
            }

            as.lengths[index] = length;
            index++;
            dir = dir.getNext(false);
        }

        return isValid;
    }


}
