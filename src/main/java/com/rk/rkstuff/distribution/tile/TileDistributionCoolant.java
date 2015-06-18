package com.rk.rkstuff.distribution.tile;

import com.rk.rkstuff.coolant.tile.ICoolantReceiver;
import net.minecraftforge.common.util.ForgeDirection;

public class TileDistributionCoolant extends TileDistribution implements ICoolantReceiver {

    @Override
    protected void addOutputAbs(int side, int mode) {

    }

    @Override
    protected void addOutputRel(int side, int mode) {

    }

    @Override
    protected void subtractOutputAbs(int side, int mode) {

    }

    @Override
    protected void subtractOutputRel(int side, int mode) {

    }

    @Override
    public int receiveCoolant(ForgeDirection from, int maxAmount, float temperature, boolean simulate) {
        return 0;
    }

    @Override
    public boolean canReceive(ForgeDirection from) {
        return false;
    }
}
