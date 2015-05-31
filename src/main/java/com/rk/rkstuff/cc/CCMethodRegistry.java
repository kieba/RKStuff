package com.rk.rkstuff.cc;

import com.rk.rkstuff.helper.RKLog;
import com.rk.rkstuff.tile.TileRK;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;

import java.util.ArrayList;
import java.util.HashMap;

public class CCMethodRegistry {

    private static HashMap<Class, ArrayList<ICCMethod>> TILE_TO_METHODS = new HashMap<Class, ArrayList<ICCMethod>>();

    public static <T extends TileRK> void registerCCMethod(Class<T> clazz, ICCMethod<T> method) {
        if(TILE_TO_METHODS.containsKey(clazz)) {
            ArrayList<ICCMethod> methods = TILE_TO_METHODS.get(clazz);
            methods.add(method);
        } else {
            ArrayList<ICCMethod> methods = new ArrayList<ICCMethod>(1);
            methods.add(method);
            TILE_TO_METHODS.put(clazz, methods);
        }
    }

    public static <T extends TileRK> Object[] executeMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, T tile, int method) {
        ArrayList<ICCMethod> methods = TILE_TO_METHODS.get(tile.getClass());
        if(methods != null && methods.size() >= method) {
            return methods.get(method).callMethod(computer, context, arguments, tile);
        } else {
            RKLog.error("Cannot execute method " + method + " for tile: " + tile.getClass());
            return new Object[0];
        }
    }

    public static <T extends TileRK> String[] getMethods(T tile) {
        ArrayList<ICCMethod> methods = TILE_TO_METHODS.get(tile.getClass());
        if(methods != null) {
            String[] result = new String[methods.size()];
            int index = 0;
            for (ICCMethod m : methods) {
                result[index++] = m.getMethodName();
            }
            return result;
        }
        return new String[0];
    }

}
