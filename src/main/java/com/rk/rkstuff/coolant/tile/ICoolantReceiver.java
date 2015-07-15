package com.rk.rkstuff.coolant.tile;

import net.minecraftforge.common.util.ForgeDirection;

public interface ICoolantReceiver extends ICoolantConnection {

    public int receiveCoolant(ForgeDirection from, int maxAmount, double temperature, boolean simulate);

}
