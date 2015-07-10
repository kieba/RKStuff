package com.rk.rkstuff.accelerator.tile;

import com.rk.rkstuff.accelerator.AcceleratorHelper;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

public class TileFusionReactorMaster extends TileAcceleratorMaster {

    @Override
    public float injectMass() {
        return 0;
    }

    @Override
    public void preAcceleration() {

    }

    @Override
    public void postAcceleration() {

    }

    @Override
    public void onInitialize() {

    }

    @Override
    public void onUnInitialize() {

    }

    @Override
    public void onRoundFinished() {

    }

    @Override
    public void onToSlow() {

    }

    @Override
    public float getAccelerationEnergy(float maxEnergy) {
        return 0;
    }

    @Override
    public void collide() {

    }

    @Override
    public float produce() {
        return 0;
    }

    @Override
    public boolean isCollideMode() {
        return false;
    }

    @Override
    protected void setup(AcceleratorHelper.AcceleratorPos pos, Block block, TileEntity tile) {

    }

    @Override
    protected void reset(AcceleratorHelper.AcceleratorPos pos, Block block, TileEntity tile) {

    }
}
