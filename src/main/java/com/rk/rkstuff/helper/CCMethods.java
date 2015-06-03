package com.rk.rkstuff.helper;


import com.rk.rkstuff.tile.TileRK;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

import java.util.ArrayList;

public class CCMethods<T extends TileRK & IPeripheral> {

    private ArrayList<CCHelper.ICCMethod<T>> methods = new ArrayList<CCHelper.ICCMethod<T>>(1);

    public void add(CCHelper.ICCMethod<T> method) {
        methods.add(method);
    }

    public Object[] execute(int method, IComputerAccess computer, ILuaContext context, Object[] arguments, T tile) throws LuaException {
        return methods.get(method).callMethod(computer, context, arguments, tile);
    }

    public String[] getMethodNames() {
        String[] names = new String[methods.size()];
        for (int i = 0; i < methods.size(); i++) {
            names[i] = methods.get(i).getMethodName();
        }
        return names;
    }

    public CCHelper.ICCMethod<T> getMethodByName(String name) {
        for (int i = 0; i < methods.size(); i++) {
            if(methods.get(i).getMethodName().equals(name)) {
                return methods.get(i);
            }
        }
        return null;
    }

    public String getMethodName(int method) {
        return methods.get(method).getMethodName();
    }

    public String getMethodDescription(int method) {
        return methods.get(method).getMethodDescription();
    }

    public int getMethodCount() {
        return methods.size();
    }
}
