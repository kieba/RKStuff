package com.rk.rkstuff.fusion.tile;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.coolant.CoolantStack;
import com.rk.rkstuff.core.tile.IMultiBlockMasterListener;
import com.rk.rkstuff.core.tile.TileMultiBlockMaster;
import com.rk.rkstuff.fusion.FusionHelper;
import com.rk.rkstuff.helper.MultiBlockHelper;
import com.rk.rkstuff.util.Pos;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileFusionControlMaster extends TileMultiBlockMaster {

    private static final int FUSION_RING_SIDE_COUNT = 9;
    private static final float MAX_ROUNDS_PER_TICK = 0.75f;

    private static final int COOLANT_PER_FLUID_IO = 1000;

    //deceleration per °C temperature difference between CoolantStack.MIN_TEMPERATURE and the current coolant temperature at the side
    //the deceleration is applied after each movement! This will be called ((int)currentSpeed)-times per tick.
    //if we have a efficiency of 0.9 and and a temperature of -200 °C we have a speed lost of about ~50% per round
    private static final float DECELERATION_PER_CENTIGRADE_IN_PERCENT = 0.015f;

    //the amount of heat energy produced when the mass travels though a block with a speed of 1
    //the heat energy will added to the coolantStack of the side (coolantEnergy = coolant.amount * coolant.temperature)
    //1 FluidIO Block has a capacity of 1000mB!
    //if we have 1000mB coolant (at each side) with a temp of -270°C we can run the fusion reactor at max speed for ~25 sec without cooling! (side length = 5)
    private static final float HEAT_ENERGY_PER_SPEED = 100.0f;

    private FusionHelper.FusionStructure setup;
    private int[] maxCoolantStorage = new int[FUSION_RING_SIDE_COUNT];
    private CoolantStack[] coolant = new CoolantStack[FUSION_RING_SIDE_COUNT];

    private float efficiency;
    //max speed is MAX_ROUNDS_PER_TICK rounds per tick (if the setting is optimal!)
    private float maxSpeed; //in blocks per tick
    private float currentSpeed; //in blocks per tick
    private float currentMass; //the mass of the object which will be accelerated
    private int currentRingSide;
    private int currentSidePosition;
    private int controlLength;
    private int totalLength;
    private boolean isCollideMode;

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        for (int i = 0; i < FUSION_RING_SIDE_COUNT; i++) {
            coolant[i].writeToNBT("coolant" + i, tag);
        }
        tag.setFloat("speed", currentSpeed);
        tag.setFloat("mass", currentMass);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        for (int i = 0; i < FUSION_RING_SIDE_COUNT; i++) {
            coolant[i] = new CoolantStack();
            coolant[i].readFromNBT("coolant" + i, tag);
        }
        currentSpeed = tag.getFloat("speed");
        currentMass = tag.getFloat("mass");
    }

    @Override
    protected void updateMaster() {
        if (isCollideMode) {
            if (hasWork()) {
                work();
            } else {
                this.currentMass += injectMass();
            }
        } else {
            this.currentMass += injectMass();
            if (hasWork()) work();
        }
    }

    private void resetWork() {
        currentMass = 0;
        currentSpeed = 0.0f;
        setCenter();
    }

    private boolean hasWork() {
        return currentMass >= 0.0f;
    }

    private void work() {
        int maxSteps = (int) currentSpeed;
        if (maxSteps == 0) {
            if (currentSpeed == 0.0f) {
                //initial acceleration for the mass
                accelerate();
                maxSteps = (int) currentSpeed;
            } else {
                onToSlow();
                resetWork();
            }

        }
        for (int s = 0; s < maxSteps; s++) {
            moveToNextPosition();
            heatUpSide();
            deceleration();
            if (isControlCenter()) {
                if (isCollideMode) {
                    if (doCollide()) {
                        collide();
                        resetWork();
                    } else {
                        accelerate();
                    }
                } else {
                    this.currentMass -= produce();
                    if (currentMass <= 0.0f) {
                        resetWork();
                    } else {
                        accelerate();
                    }
                }
            }
        }
    }

    private void accelerate() {
        float energy = 0.5f * currentMass * currentSpeed * currentSpeed;
        float maxSpeedEnergy = 0.5f * currentMass * maxSpeed * maxSpeed;
        energy += getAccelerationEnergy(maxSpeedEnergy - energy);
        currentSpeed = (float) Math.sqrt(2.0f * energy / currentMass);
        if (currentSpeed > maxSpeed) currentSpeed = maxSpeed;
    }

    private void deceleration() {
        //control side does not need to be cooled -> no deceleration
        if (!isControlSide()) {
            float temperature = coolant[currentRingSide].getTemperature();
            //the coolant does only work if there are more than 100 mB in the side
            if (coolant[currentRingSide].getAmount() <= 100) temperature = 20.0f;
            float tempDiff = Math.abs(CoolantStack.MIN_TEMPERATURE - temperature);
            currentSpeed = currentSpeed * (1.0f - ((tempDiff * DECELERATION_PER_CENTIGRADE_IN_PERCENT * efficiency) / totalLength));
        }
    }

    private void heatUpSide() {
        if (!isControlSide()) {
            float heatEnergy = ((HEAT_ENERGY_PER_SPEED * currentSpeed) / (efficiency * totalLength));
            coolant[currentRingSide].addEnergy(heatEnergy);
        }
    }

    //returns the amount of mass that should be injected into the system
    protected float injectMass() {

        return 0.0f;
    }

    //returns the mass of the material which should be removed from the current mass
    protected float produce() {

        return 0.0f;
    }

    protected void collide() {

    }

    protected void onInitialize() {

    }

    protected void onUnInitialize() {

    }

    //this is called if the speed drops below 1, which means we have not enough energy to accelerate the mass for one round
    protected void onToSlow() {

    }

    //maxEnergy returns the energy needed to accelerate the mass to maxSpeed
    //return the energy used per acceleration, depends on the control core setting
    protected float getAccelerationEnergy(float maxEnergy) {

        return 0.0f;
    }

    protected boolean isCollideMode() {
        return true;
    }

    protected float getMass() {
        return currentMass;
    }

    protected float getSpeed() {
        return currentSpeed;
    }

    protected float getMaxSpeed() {
        return maxSpeed;
    }

    protected float getEfficiency() {
        return efficiency;
    }

    protected float getTotalLength() {
        return totalLength;
    }

    private boolean isControlCenter() {
        if (isControlSide()) {
            if (currentSidePosition == (controlLength / 2)) {
                return true;
            }
        }
        return false;
    }

    private void setCenter() {
        this.currentRingSide = FUSION_RING_SIDE_COUNT;
        this.currentSidePosition = (controlLength / 2);
    }

    private boolean isControlSide() {
        return currentRingSide == FUSION_RING_SIDE_COUNT;
    }

    private boolean doCollide() {
        return currentSpeed >= maxSpeed;
    }

    /**
     * @return true if the side changes
     */
    private void moveToNextPosition() {
        int sideLength;

        if (isControlSide()) {
            sideLength = controlLength;
        } else {
            sideLength = setup.lengths[currentRingSide];
        }

        currentSidePosition++;

        if (sideLength >= currentSidePosition) {
            currentSidePosition = 0;

            if (isControlSide()) {
                currentRingSide = 0;
            } else {
                currentRingSide++;
            }
        }
    }

    private void initialize() {
        if (setup.startDir.xOff != 0) {
            controlLength = setup.controlBounds.getWidthX();
        } else {
            controlLength = setup.controlBounds.getWidthZ();
        }

        totalLength = 0;
        for (int i = 0; i < FUSION_RING_SIDE_COUNT; i++) {
            totalLength += setup.lengths[i];
        }
        totalLength += controlLength;

        float avgLength = totalLength / 8.0f;

        //the smaller the average deviation from the average side length the better
        float avgDev = 0.0f;
        for (int i = 1; i < FUSION_RING_SIDE_COUNT - 1; i++) {
            avgDev += Math.abs(setup.lengths[i] - avgLength);
        }

        avgDev += Math.abs(controlLength + setup.lengths[0] + setup.lengths[FUSION_RING_SIDE_COUNT - 1] - avgLength);

        efficiency = 1.0f;
        if (avgDev != 0.0f) efficiency = 1.0f - (avgDev / avgLength);

        //max speed is MAX_ROUNDS_PER_TICK rounds per tick (if the setting is optimal!)
        maxSpeed = totalLength * efficiency * MAX_ROUNDS_PER_TICK;
        isCollideMode = isCollideMode();
        onInitialize();
    }

    private void unInitialize() {
        resetWork();
        efficiency = 0.0f;
        maxSpeed = 0.0f;
        isCollideMode = false;
        totalLength = 0;
        controlLength = 0;
        onUnInitialize();
    }

    public int receiveCoolant(int side, int maxAmount, float temperature, boolean simulate) {
        CoolantStack stack = coolant[side];
        maxAmount = Math.min(maxAmount, maxCoolantStorage[side] - stack.getAmount());
        if (!simulate) {
            stack.add(maxAmount, temperature);
        }
        return maxAmount;
    }

    public int getSideForFluidIO(final TileFusionCaseFluidIO tile) {
        final int[] side = {-1};
        FusionHelper.iterateRingCore(setup, new FusionHelper.IFusionPosVisitor() {
            @Override
            public boolean visit(FusionHelper.FusionPos pos) {
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
        final int[] fluidIOTiles = new int[FUSION_RING_SIDE_COUNT];
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
                            TileEntity tile = TileFusionControlMaster.this.registerMaster(pos.p.x, pos.p.y, pos.p.z, 1);
                            if (tile instanceof TileFusionCaseFluidIO) {
                                fluidIOTiles[((TileFusionCaseFluidIO) tile).getSide()]++;
                            }
                        }
                    }
                    return true;
                }
            });

            for (int i = 0; i < FUSION_RING_SIDE_COUNT; i++) {
                maxCoolantStorage[i] = fluidIOTiles[i] * COOLANT_PER_FLUID_IO;
                if (coolant[i].getAmount() > maxCoolantStorage[i])
                    coolant[i].set(maxCoolantStorage[i], coolant[i].getTemperature());
            }

            initialize();
        }
        return null;
    }

    private TileEntity registerMaster(int x, int y, int z, int meta) {
        Block b = worldObj.getBlock(x, y, z);
        if (b.hasTileEntity(meta)) {
            TileEntity tile = worldObj.getTileEntity(x, y, z);
            if (tile instanceof IMultiBlockMasterListener) {
                ((IMultiBlockMasterListener) tile).registerMaster(this);
                return tile;
            }
        }
        return null;
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
        unInitialize();
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
                        Block b = worldObj.getBlock(pos.p.x, pos.p.y, pos.p.z);
                        if (b == RkStuff.blockFusionCaseBevelLarge) {
                            return true;
                        }
                        if (b == RkStuff.blockFusionCaseBevelSmall) {
                            return true;
                        }
                        if (b == RkStuff.blockFusionCaseBevelSmallInverted) {
                            return true;
                        }
                        return false;
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
