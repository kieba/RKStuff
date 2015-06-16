package com.rk.rkstuff.util;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;


public class Textures {

    public static final String MODEL_PATH = Reference.MOD_ID.toLowerCase() + ":textures/models/";
    public static final String GUI_PATH = Reference.MOD_ID.toLowerCase() + ":textures/gui/";

    public static ResourceLocation SOLAR_GUI = new ResourceLocation(GUI_PATH + "SolarGUI.png");
    public static ResourceLocation BOILER_GUI = new ResourceLocation(GUI_PATH + "BoilerGUI.png");
    public static ResourceLocation DISTRIBUTION_GUI = new ResourceLocation(GUI_PATH + "DistributionGUI.png");

    @SideOnly(Side.CLIENT)
    public static void loadTexture(ResourceLocation location) {
        FMLClientHandler.instance().getClient().getTextureManager().bindTexture(location);
    }

}
