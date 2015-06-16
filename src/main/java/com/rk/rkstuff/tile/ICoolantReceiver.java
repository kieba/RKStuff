package com.rk.rkstuff.tile;

import net.minecraftforge.common.util.ForgeDirection;

public interface ICoolantReceiver {

    public int receiveCoolant(ForgeDirection from, CoolantStack stack, boolean simulate);

    public boolean canReceive(ForgeDirection from);

}
