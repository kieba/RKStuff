package com.rk.rkstuff.helper;

import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import org.apache.logging.log4j.Level;

public class RKLog {

    public static void log(Level logLevel, Object object) {
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            FMLLog.log(Reference.MOD_ID, logLevel, "[SERVER]" + String.valueOf(object));
        } else {
            FMLLog.log(Reference.MOD_ID, logLevel, "[CLIENT]" + String.valueOf(object));
        }
    }

    public static void debug(Object object)
    {
        log(Level.DEBUG, object);
    }

    public static void error(Object object)
    {
        log(Level.ERROR, object);
    }

    public static void info(Object object)
    {
        log(Level.INFO, object);
    }

    public static void warn(Object object)
    {
        log(Level.WARN, object);
    }

    public static void error(Exception e) {
        log(Level.ERROR, e.getClass() + " " + e.getMessage());
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            log(Level.ERROR, "\t" + stackTraceElement.toString());
        }
    }

    public static void log(Level logLevel, String format, Object... objects) {
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            FMLLog.log(Reference.MOD_ID, logLevel, "[SERVER] " + String.format(format, objects));
        } else {
            FMLLog.log(Reference.MOD_ID, logLevel, "[CLIENT] " + String.format(format, objects));
        }
    }

    public static void debug(String format, Object... objects)
    {
        log(Level.DEBUG, format, objects);
    }

    public static void error(String format, Object... objects)
    {
        log(Level.ERROR, format, objects);
    }

    public static void info(String format, Object... objects)
    {
        log(Level.INFO, format, objects);
    }

    public static void warn(String format, Object... objects)
    {
        log(Level.WARN, format, objects);
    }
}
