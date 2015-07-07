package com.rk.rkstuff.accelerator;

import java.io.Serializable;

public class AcceleratorConfig implements Serializable {

    public static final int ACCELERATOR_SIDE_COUNT = 9;

    public static final int MIN_START_END_LENGTH = 2;
    public static final int MIN_SIDE_LENGTH = 5;

    public int MAX_START_END_LENGTH = 22;
    public int MAX_SIDE_LENGTH = 25;

    public int COOLANT_PER_FLUID_IO = 1000;
    public float MAX_ROUNDS_PER_TICK = 0.75f;

    //deceleration per °C temperature difference between CoolantStack.MIN_TEMPERATURE and the current coolant temperature at the side
    //the deceleration is applied after each movement! This will be called ((int)currentSpeed)-times per tick.
    //if we have a efficiency of 0.9 and and a temperature of -200 °C we have a speed lost of about ~50% per round
    public float DECELERATION_PER_CENTIGRADE_IN_PERCENT = 0.015f;

    //the amount of heat energy produced when the mass travels though a block with a speed of 1
    //the heat energy will added to the coolantStack of the side (coolantEnergy = coolant.amount * coolant.temperature)
    //1 FluidIO Block has a capacity of 1000mB!
    //if we have 1000mB coolant (at each side) with a temp of -270°C we can run the accelerator reactor at max speed for ~25 sec without cooling! (side length = 5)
    public float HEAT_ENERGY_PER_SPEED = 100.0f;

}
