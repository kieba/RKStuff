package com.rk.rkstuff.helper;

import com.rk.rkstuff.RkStuff;
import javafx.stage.FileChooser;
import net.minecraftforge.fluids.Fluid;

public class FluidHelper {

    public static int steamId = -1;
    public static int waterId = -1;

    public static boolean isSteam(Fluid fluid) {
        return fluid.getID() == steamId;
    }

    public static boolean isHotCoolant(Fluid fluid) {
        return fluid.getID() == RkStuff.hotCoolant.getID();
    }

    public static boolean isColdCoolant(Fluid fluid) {
        return fluid.getID() == RkStuff.coolCoolant.getID();
    }

    public static boolean isWater(Fluid fluid) {
        return fluid.getID() == waterId;
    }
}
