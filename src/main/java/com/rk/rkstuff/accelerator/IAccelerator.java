package com.rk.rkstuff.accelerator;

public interface IAccelerator {

    //returns the amount of mass that should be injected into the system
    float injectMass();

    void preAcceleration();

    void postAcceleration();

    void onInitialize();

    void onUnInitialize();

    void onRoundFinished();

    //this is called if the speed drops below 1, which means we have not enough energy to accelerate the mass for one round
    void onToSlow();

    //maxEnergy returns the energy needed to accelerate the mass to maxSpeed
    //return the energy used per acceleration, depends on the control core setting
    float getAccelerationEnergy(float maxEnergy);

    void collide();

    //returns the mass of the material which should be removed from the current mass
    float produce();

    boolean isCollideMode();

}
