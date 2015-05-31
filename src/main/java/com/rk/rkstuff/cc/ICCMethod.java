package com.rk.rkstuff.cc;

import com.rk.rkstuff.tile.TileRK;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;

public interface ICCMethod<T extends TileRK> {

    public String getMethodName();
    public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, T tile);

}
