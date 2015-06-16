package com.rk.rkstuff.helper;

import com.rk.rkstuff.core.tile.TileRK;
import com.rk.rkstuff.distribution.tile.TileDistribution;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Arrays;

public class CCHelper {

    public static LuaException INVALID_ARGUMENT_EXCEPTION = new LuaException("Invalid arguments!");
    public static LuaException INVALID_DIRECTION_EXCEPTION = new LuaException("Invalid direction! Valid:\n" + Arrays.toString(ForgeDirection.VALID_DIRECTIONS));
    public static LuaException INVALID_SIDE_TYPE_EXCEPTION = new LuaException("Invalid side type! Valid:\n" + Arrays.toString(TileDistribution.SideType.values()));

    public static ForgeDirection argumentToDirection(Object arg) {
        if(arg instanceof String) {
            String dir = ((String) arg).toLowerCase();
            for(ForgeDirection d : ForgeDirection.VALID_DIRECTIONS) {
                if(dir.equals(d.name().toLowerCase())) {
                    return d;
                }
            }
        }
        return ForgeDirection.UNKNOWN;
    }

    public static TileDistribution.SideType argumentToSideType(Object arg) {
        if(arg instanceof String) {
            String dir = ((String) arg).toLowerCase();
            for(TileDistribution.SideType t : TileDistribution.SideType.values()) {
                if(dir.equals(t.name().toLowerCase())) {
                    return t;
                }
            }
        }
        return null;
    }

    public interface ICCMethod<T extends TileRK> {
        public String getMethodName();
        public String getMethodDescription();
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, T tile) throws LuaException;
    }

    public static class CCMethodDoc<T extends TileRK & IPeripheral> implements CCHelper.ICCMethod<T> {

        private CCMethods methods;

        public CCMethodDoc(CCMethods methods) {
            this.methods = methods;
        }

        @Override
        public String getMethodName() {
            return "doc";
        }

        @Override
        public String getMethodDescription() {
            return "\tShows the description for all available methods.\n\tUsage: doc(<method_name>); OR doc();";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, T tile) throws LuaException {
            if(arguments == null || arguments.length > 1) {
                throw CCHelper.INVALID_ARGUMENT_EXCEPTION;
            }
            if(arguments.length == 0) {
                StringBuffer sb = new StringBuffer();
                sb.append("Available methods:\n");
                for (int i = 0; i < methods.getMethodCount(); i++) {
                    sb.append(methods.getMethodName(i) + ":\n");
                    sb.append(methods.getMethodDescription(i) + "\n");
                }
                return new Object[] { sb.toString() };
            } else if(arguments.length == 1) {
                if(arguments[0] instanceof String) {
                    ICCMethod<T> m = methods.getMethodByName((String)arguments[0]);
                    if(m == null) throw CCHelper.INVALID_ARGUMENT_EXCEPTION;
                    return new Object[] { m.getMethodName() + ":\n" + m.getMethodDescription() };
                }  else {
                    throw CCHelper.INVALID_ARGUMENT_EXCEPTION;
                }
            }
            return new Object[] { getMethodName() + ":\n" + getMethodDescription() };
        }
    }

    public static class CCMethods<T extends TileRK & IPeripheral> {

        private ArrayList<ICCMethod<T>> methods = new ArrayList<ICCMethod<T>>(1);

        public void add(ICCMethod<T> method) {
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

        public ICCMethod<T> getMethodByName(String name) {
            for (int i = 0; i < methods.size(); i++) {
                if (methods.get(i).getMethodName().equals(name)) {
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
}
