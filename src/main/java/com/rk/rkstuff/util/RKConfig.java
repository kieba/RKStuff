package com.rk.rkstuff.util;

import com.rk.rkstuff.accelerator.Accelerator;
import net.minecraftforge.common.config.Configuration;

public class RKConfig {
    private RKConfig() {
    }

    public static Configuration rawConfig;

    //General
    private static String CATEGORY_GENERAL = "general";
    public static boolean useCelsius;

    private static void load() {
        useCelsius = rawConfig.getBoolean("useCelsius", CATEGORY_GENERAL, true, "To use fahrenheit set to false");
    }

    public static void init(Configuration config) {
        rawConfig = config;
        load();
        Accelerator.DEFAULT_CONFIG.loadFromConfig(config);
    }

    public static void save() {
        rawConfig.save();
    }
}
