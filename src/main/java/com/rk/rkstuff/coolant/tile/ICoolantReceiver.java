package com.rk.rkstuff.coolant.tile;

import net.minecraftforge.common.util.ForgeDirection;

public interface ICoolantReceiver {

    public int receiveCoolant(ForgeDirection from, int maxAmount, float temperature, boolean simulate);

    public boolean canReceive(ForgeDirection from);

}
