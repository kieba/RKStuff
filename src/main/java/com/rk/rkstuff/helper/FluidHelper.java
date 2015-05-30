package com.rk.rkstuff.helper;

import com.rk.rkstuff.RkStuff;
import javafx.stage.FileChooser;
import net.minecraftforge.fluids.Fluid;

public class FluidHelper {

    public static Fluid steam = null;
    public static Fluid water = null;

    public static boolean isSteam(Fluid fluid) {
        return fluid == steam;
    }

    public static boolean isHotCoolant(Fluid fluid) {
        return fluid.getID() == RkStuff.hotCoolant.getID();
    }

    public static boolean isCoolCoolant(Fluid fluid) {
        return fluid.getID() == RkStuff.coolCoolant.getID();
    }

    public static boolean isWater(Fluid fluid) {
        return fluid == water;
    }
}
