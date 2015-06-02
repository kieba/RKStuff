package com.rk.rkstuff.util;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.util.ResourceLocation;


public class Textures {

    public static final String GUI_PATH = "textures/gui/";
    public static ResourceLocation SOLAR_GUI = new ResourceLocation(Reference.MOD_ID + ":" + GUI_PATH + "SolarGUI.png");
    public static ResourceLocation BOILER_GUI = new ResourceLocation(Reference.MOD_ID + ":" + GUI_PATH + "BoilerGUI.png");
    public static ResourceLocation ENERGY_DISTRIBUTION_GUI = new ResourceLocation(Reference.MOD_ID + ":" + GUI_PATH + "DistributionGUI.png");

}
