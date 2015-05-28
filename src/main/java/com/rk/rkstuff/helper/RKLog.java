package com.rk.rkstuff.helper;

import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

public class RKLog {

    public static void log(Level logLevel, Object object)
    {
        FMLLog.log(Reference.MOD_ID, logLevel, String.valueOf(object));
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

    public static void log(Level logLevel, String format, Object... objects)
    {
        FMLLog.log(Reference.MOD_ID, logLevel, String.format(format, objects));
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
