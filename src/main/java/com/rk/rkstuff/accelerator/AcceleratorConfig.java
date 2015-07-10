package com.rk.rkstuff.accelerator;

import java.io.Serializable;

public class AcceleratorConfig implements Serializable {

    public static final int ACCELERATOR_SIDE_COUNT = 9;

    public static final int MIN_START_END_LENGTH = 2;
    public static final int MIN_SIDE_LENGTH = 5;
    public static final int CONTROL_HEIGHT = 5;
    public static final int CONTROL_WIDTH = 5;
    public static final int MIN_CONTROL_LENGTH = CONTROL_WIDTH;

    public static final int DEFAULT_MAX_CONTROL_LENGTH = 15;
    public static final int DEFAULT_MAX_SIDE_LENGTH = 25;
    public static final float DEFAULT_ENERGY_BASE = 5.0f;
    public static final float DEFAULT_ENERGY_MULTIPLIER = 1.015f;
    public static final int DEFAULT_COOLANT_PER_FLUID_IO = 1000;
    public static final float DEFAULT_MAX_ROUNDS_PER_TICK = 1.5f;
    public static final float DEFAULT_ENERGY_TO_RF_FACTOR = 70.0f;
    public static final float DEFAULT_EFFICIENCY_UPGRADE = 0.5f;
    public static final float DEFAULT_DECELERATION_PER_CENTIGRADE_IN_PERCENT = 0.00015f;
    public static final float DEFAULT_HEAT_ENERGY_PER_SPEED = 10.0f;

    public int MAX_SIDE_LENGTH = DEFAULT_MAX_SIDE_LENGTH;
    public int MAX_CONTROL_LENGTH = DEFAULT_MAX_CONTROL_LENGTH;

    public float ENERGY_TO_RF_FACTOR = DEFAULT_ENERGY_TO_RF_FACTOR;
    public float EFFICIENCY_PER_UPGRADE = DEFAULT_EFFICIENCY_UPGRADE;

    public float ENERGY_BASE = DEFAULT_ENERGY_BASE;
    public float ENERGY_MULTIPLIER = DEFAULT_ENERGY_MULTIPLIER;
    public int COOLANT_STORAGE = DEFAULT_COOLANT_PER_FLUID_IO;
    public float MAX_ROUNDS_PER_TICK = DEFAULT_MAX_ROUNDS_PER_TICK;

    //deceleration per °C temperature difference between CoolantStack.MIN_TEMPERATURE and the current coolant temperature at the side
    //the deceleration is applied after each movement! This will be called ((int)currentSpeed)-times per tick.
    public float DECELERATION_PER_CENTIGRADE_IN_PERCENT = DEFAULT_DECELERATION_PER_CENTIGRADE_IN_PERCENT;

    //the amount of heat energy produced when the mass travels though a block with a speed of 1
    //the heat energy will added to the coolantStack of the side (coolantEnergy = coolant.amount * coolant.temperature)
    //1 FluidIO Block has a capacity of 1000mB!
    public float HEAT_ENERGY_PER_SPEED = DEFAULT_HEAT_ENERGY_PER_SPEED;

}
