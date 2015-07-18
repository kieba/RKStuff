package com.rk.rkstuff.accelerator;

import com.rk.rkstuff.coolant.CoolantStack;
import net.minecraft.nbt.NBTTagCompound;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class Accelerator {

    public static final AcceleratorConfig DEFAULT_CONFIG = new AcceleratorConfig();

    private AcceleratorConfig config;
    private AcceleratorHelper.AcceleratorStructure setup;
    private int[] maxCoolantStorage = new int[AcceleratorConfig.ACCELERATOR_SIDE_COUNT];
    private CoolantStack[] coolant = new CoolantStack[AcceleratorConfig.ACCELERATOR_SIDE_COUNT];

    private boolean isInitialized;
    private float efficiency;
    //max speed is MAX_ROUNDS_PER_TICK rounds per tick (if the setting is optimal!)
    private float maxSpeed = 0.0f; //in blocks per tick
    private float currentSpeed; //in blocks per tick
    private float currentMass; //the mass of the object which will be accelerated
    private int currentRingSide;
    private int currentSidePosition;
    private int controlLength;
    private int totalLength;
    private boolean isCollideMode;
    private IAccelerator accelerator;
    private float producedHeatLastTick;
    private float decelerationLastRound;
    private float startSpeedLastRound;
    private float endSpeedLastRound;

    public Accelerator(IAccelerator collisionAccelerator, AcceleratorConfig config) {
        this.config = config;
        this.accelerator = collisionAccelerator;
        this.isCollideMode = accelerator.isCollideMode();
        for (int i = 0; i < AcceleratorConfig.ACCELERATOR_SIDE_COUNT; i++) {
            coolant[i] = new CoolantStack();
        }
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
                this.currentMass += accelerator.injectMass();
                this.currentSpeed = 0.0f;
            }
        } else {
            float energy = 0.5f * currentMass * currentSpeed * currentSpeed;
            this.currentMass += accelerator.injectMass();
            this.currentSpeed = (float) Math.sqrt((energy * 2.0f) / this.currentMass);
            if (hasWork()) work();
        }
    }

    private void resetWork() {
        startSpeedLastRound = 0.0f;
        endSpeedLastRound = 0.0f;
        decelerationLastRound = 0.0f;
        producedHeatLastTick = 0.0f;
        currentMass = 0;
        currentSpeed = 0.0f;
        setCenter();
    }

    private boolean hasWork() {
        return currentMass > 0.0f;
    }

    private void work() {
        int maxSteps = (int) currentSpeed;
        producedHeatLastTick = 0;
        if (maxSteps <= 0) {
            if (currentSpeed == 0.0f) {
                //initial acceleration for the mass
                accelerate();
                maxSteps = (int) currentSpeed;
            } else {
                decelerationLastRound = (startSpeedLastRound - currentSpeed) / currentSpeed;
                accelerator.onToSlow();
                resetWork();
                return;
            }

        }
        for (int s = 0; s < maxSteps; s++) {
            moveToNextPosition();
            heatUpSide();
            deceleration();
            if (isControlCenter()) {
                if (isCollideMode) {
                    if (doCollide()) {
                        decelerationLastRound = (startSpeedLastRound - currentSpeed) / startSpeedLastRound;
                        accelerator.onRoundFinished();
                        accelerator.collide();
                        resetWork();
                        return;
                    } else if (accelerate()) {
                        return;
                    }
                } else {
                    this.currentMass -= accelerator.produce();
                    if (currentMass <= 0.0f) {
                        decelerationLastRound = (startSpeedLastRound - currentSpeed) / startSpeedLastRound;
                        accelerator.onRoundFinished();
                        resetWork();
                        return;
                    } else if (accelerate()) {
                        return;
                    }
                }
            }
        }
    }

    private boolean accelerate() {
        accelerator.preAcceleration();
        if (currentSpeed > 0.0f) {
            decelerationLastRound = (startSpeedLastRound - currentSpeed) / startSpeedLastRound;
            accelerator.onRoundFinished();
            if (Math.abs(endSpeedLastRound - currentSpeed) < 0.5f) {
                accelerator.onToSlow();
                resetWork();
                return true;
            }
        }

        endSpeedLastRound = currentSpeed;
        float energy = 0.5f * currentMass * currentSpeed * currentSpeed;
        float maxInjectableEnergy = config.ENERGY_BASE * (float) Math.pow(config.ENERGY_MULTIPLIER, setup.coreUpgradesEnergy) * (float) Math.pow(efficiency, 2);
        energy += Math.min(maxInjectableEnergy, accelerator.getAccelerationEnergy(maxInjectableEnergy));
        currentSpeed = (float) Math.sqrt(2.0f * energy / currentMass);
        startSpeedLastRound = currentSpeed;
        accelerator.postAcceleration();
        return false;
    }

    public float getLastRoundDeceleration() {
        return decelerationLastRound;
    }

    public float getEndSpeedLastRound() {
        return endSpeedLastRound;
    }

    public float getStartSpeedLastRound() {
        return startSpeedLastRound;
    }

    private void deceleration() {
        //control side does not need to be cooled -> no deceleration
        if (!isControlSide()) {
            double temperature = coolant[currentRingSide].getTemperature();
            //the coolant does only work if there are more than 100 mB in the side
            if (coolant[currentRingSide].getAmount() <= 100) temperature = CoolantStack.celsiusToKelvin(20.0f);
            double tempDiff = Math.abs(CoolantStack.MIN_TEMPERATURE - temperature);
            currentSpeed = currentSpeed * (1.0f - ((((float) tempDiff) * config.DECELERATION_PER_CENTIGRADE_IN_PERCENT * efficiency) / totalLength));
        }
    }

    private void heatUpSide() {
        if (!isControlSide()) {
            float heatEnergy = ((config.HEAT_ENERGY_PER_SPEED * currentSpeed) / (efficiency * totalLength));
            coolant[currentRingSide].addEnergy(heatEnergy);
            producedHeatLastTick += heatEnergy;
        }
    }

    public boolean isCollideMode() {
        return accelerator.isCollideMode();
    }

    public float getMass() {
        return currentMass;
    }

    public float getSpeed() {
        return currentSpeed;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public float getProducedHeatLastTick() {
        return producedHeatLastTick;
    }

    public float getEfficiency() {
        return efficiency;
    }

    public int getTotalLength() {
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

        if (currentSidePosition >= sideLength) {
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
            resetWork();
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

            avgDev /= AcceleratorConfig.ACCELERATOR_SIDE_COUNT;

            efficiency = 1.0f;
            if (avgDev != 0.0f) efficiency = 1.0f - 2.0f * (avgDev / avgLength);
            efficiency += config.EFFICIENCY_PER_UPGRADE * setup.coreUpgradesEfficiency;
            if (efficiency > 1.0f) efficiency = 1.0f;

            //max speed is MAX_ROUNDS_PER_TICK rounds per tick (if the setting is optimal!)
            maxSpeed = totalLength * efficiency * config.MAX_ROUNDS_PER_TICK;
            accelerator.onInitialize();
            isInitialized = true;

            for (int i = 0; i < AcceleratorConfig.ACCELERATOR_SIDE_COUNT; i++) {
                maxCoolantStorage[i] = config.COOLANT_STORAGE * setup.lengths[i];
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

    public int receiveCoolant(int side, int maxAmount, double temperature, boolean simulate) {
        CoolantStack stack = coolant[side];
        maxAmount = Math.min(maxAmount, maxCoolantStorage[side] - stack.getAmount());
        if (!simulate) {
            stack.add(maxAmount, temperature);
        }
        return maxAmount;
    }


    public CoolantStack getCoolant(int side) {
        return coolant[side];
    }

    public int getMaxCoolant(int side) {
        return maxCoolantStorage[side];
    }

    public AcceleratorConfig getConfig() {
        return config;
    }

    public void readData(IOStream data) throws IOException {
        efficiency = data.readFirstFloat();
        currentSpeed = data.readFirstFloat();
        maxSpeed = data.readFirstFloat();
        for (int i = 0; i < AcceleratorConfig.ACCELERATOR_SIDE_COUNT; i++) {
            coolant[i].set(data.readFirstInt(), data.readFirstDouble());
            maxCoolantStorage[i] = data.readFirstInt();
        }
    }

    public void writeData(IOStream data) {
        data.writeLast(efficiency);
        data.writeLast(currentSpeed);
        data.writeLast(maxSpeed);
        for (int i = 0; i < AcceleratorConfig.ACCELERATOR_SIDE_COUNT; i++) {
            data.writeLast(coolant[i].getAmount());
            data.writeLast(coolant[i].getTemperature());
            data.writeLast(maxCoolantStorage[i]);
        }
    }
}
