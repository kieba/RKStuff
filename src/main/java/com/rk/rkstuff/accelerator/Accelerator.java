package com.rk.rkstuff.accelerator;

import com.rk.rkstuff.coolant.CoolantStack;
import net.minecraft.nbt.NBTTagCompound;

public class Accelerator {

    public static final AcceleratorConfig DEFAULT_CONFIG = new AcceleratorConfig();

    private AcceleratorConfig config;
    private AcceleratorHelper.AcceleratorStructure setup;
    private int[] maxCoolantStorage = new int[AcceleratorConfig.ACCELERATOR_SIDE_COUNT];
    private CoolantStack[] coolant = new CoolantStack[AcceleratorConfig.ACCELERATOR_SIDE_COUNT];

    private boolean isInitialized;
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
    private IAccelerator accelerator;

    public Accelerator(IAccelerator collisionAccelerator, AcceleratorConfig config) {
        this.config = config;
        this.accelerator = collisionAccelerator;
        this.isCollideMode = accelerator.isCollideMode();
    }

    public void writeToNBT(String prefix, NBTTagCompound tag) {
        for (int i = 0; i < AcceleratorConfig.ACCELERATOR_SIDE_COUNT; i++) {
            coolant[i].writeToNBT(prefix + "coolant" + i, tag);
        }
        tag.setFloat(prefix + "speed", currentSpeed);
        tag.setFloat(prefix + "mass", currentMass);
    }

    public void readFromNBT(String prefix, NBTTagCompound tag) {
        for (int i = 0; i < AcceleratorConfig.ACCELERATOR_SIDE_COUNT; i++) {
            coolant[i] = new CoolantStack();
            coolant[i].readFromNBT(prefix + "coolant" + i, tag);
        }
        currentSpeed = tag.getFloat(prefix + "speed");
        currentMass = tag.getFloat(prefix + "mass");
    }

    public void update() {
        if (!isInitialized) return;
        if (isCollideMode) {
            if (hasWork()) {
                work();
            } else {
                float energy = 0.5f * currentMass * currentSpeed * currentSpeed;
                this.currentMass += accelerator.injectMass();
                this.currentSpeed = (float) Math.sqrt((energy * 2.0f) / this.currentMass);
            }
        } else {
            float energy = 0.5f * currentMass * currentSpeed * currentSpeed;
            this.currentMass += accelerator.injectMass();
            this.currentSpeed = (float) Math.sqrt((energy * 2.0f) / this.currentMass);
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
                accelerator.onToSlow();
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
                        accelerator.collide();
                        resetWork();
                    } else {
                        accelerate();
                    }
                } else {
                    this.currentMass -= accelerator.produce();
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
        energy += accelerator.getAccelerationEnergy(maxSpeedEnergy - energy);
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
            currentSpeed = currentSpeed * (1.0f - ((tempDiff * config.DECELERATION_PER_CENTIGRADE_IN_PERCENT * efficiency) / totalLength));
        }
    }

    private void heatUpSide() {
        if (!isControlSide()) {
            float heatEnergy = ((config.HEAT_ENERGY_PER_SPEED * currentSpeed) / (efficiency * totalLength));
            coolant[currentRingSide].addEnergy(heatEnergy);
        }
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
        this.currentRingSide = AcceleratorConfig.ACCELERATOR_SIDE_COUNT;
        this.currentSidePosition = (controlLength / 2);
    }

    private boolean isControlSide() {
        return currentRingSide == AcceleratorConfig.ACCELERATOR_SIDE_COUNT;
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

    public void initialize(AcceleratorHelper.AcceleratorStructure setup) {
        if (!isInitialized) {
            this.setup = setup;
            if (setup.startDir.xOff != 0) {
                controlLength = setup.controlBounds.getWidthX();
            } else {
                controlLength = setup.controlBounds.getWidthZ();
            }

            totalLength = 0;
            for (int i = 0; i < AcceleratorConfig.ACCELERATOR_SIDE_COUNT; i++) {
                totalLength += setup.lengths[i];
            }
            totalLength += controlLength;

            float avgLength = totalLength / 8.0f;

            //the smaller the average deviation from the average side length the better
            float avgDev = 0.0f;
            for (int i = 1; i < AcceleratorConfig.ACCELERATOR_SIDE_COUNT - 1; i++) {
                avgDev += Math.abs(setup.lengths[i] - avgLength);
            }

            avgDev += Math.abs(controlLength + setup.lengths[0] + setup.lengths[AcceleratorConfig.ACCELERATOR_SIDE_COUNT - 1] - avgLength);

            efficiency = 1.0f;
            if (avgDev != 0.0f) efficiency = 1.0f - (avgDev / avgLength);

            //max speed is MAX_ROUNDS_PER_TICK rounds per tick (if the setting is optimal!)
            maxSpeed = totalLength * efficiency * config.MAX_ROUNDS_PER_TICK;
            accelerator.onInitialize();
            isInitialized = true;

            for (int i = 0; i < AcceleratorConfig.ACCELERATOR_SIDE_COUNT; i++) {
                maxCoolantStorage[i] = setup.fluidIOs[i] * config.COOLANT_PER_FLUID_IO;
                if (coolant[i].getAmount() > maxCoolantStorage[i])
                    coolant[i].set(maxCoolantStorage[i], coolant[i].getTemperature());
            }
        }
    }

    public void unInitialize() {
        if (isInitialized) {
            setup = null;
            resetWork();
            efficiency = 0.0f;
            maxSpeed = 0.0f;
            totalLength = 0;
            controlLength = 0;
            accelerator.onUnInitialize();
            isInitialized = false;
        }
    }

    public int receiveCoolant(int side, int maxAmount, float temperature, boolean simulate) {
        CoolantStack stack = coolant[side];
        maxAmount = Math.min(maxAmount, maxCoolantStorage[side] - stack.getAmount());
        if (!simulate) {
            stack.add(maxAmount, temperature);
        }
        return maxAmount;
    }


}
