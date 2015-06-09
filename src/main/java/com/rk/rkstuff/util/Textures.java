package com.rk.rkstuff.util;

import net.minecraft.util.ResourceLocation;


public class Textures {

    public static final String MODEL_PATH = Reference.MOD_ID.toLowerCase() + ":textures/models/";
    public static final String GUI_PATH = Reference.MOD_ID.toLowerCase() + ":textures/gui/";

    public static ResourceLocation SOLAR_GUI = new ResourceLocation(GUI_PATH + "SolarGUI.png");
    public static ResourceLocation BOILER_GUI = new ResourceLocation(GUI_PATH + "BoilerGUI.png");
    public static ResourceLocation DISTRIBUTION_GUI = new ResourceLocation(GUI_PATH + "DistributionGUI.png");

    public static ResourceLocation BEVEL_SMALL = new ResourceLocation(MODEL_PATH + "FusionCaseBevelSmall.png");
    public static ResourceLocation BEVEL_LARGE = new ResourceLocation(MODEL_PATH + "BevelLarge.png");
    public static ResourceLocation BEVEL_SMALL_INVERTED = new ResourceLocation(MODEL_PATH + "FusionCaseBevelSmallInverted.png");

}
