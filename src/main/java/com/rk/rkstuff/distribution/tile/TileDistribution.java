package com.rk.rkstuff.distribution.tile;

import com.rk.rkstuff.core.tile.TileRK;
import com.rk.rkstuff.helper.CCHelper;
import com.rk.rkstuff.network.message.IGuiActionMessage;
import com.rk.rkstuff.util.RKLog;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import rk.com.core.io.IOStream;

import java.io.IOException;

@Optional.InterfaceList({
        @Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = "ComputerCraft")
})
public abstract class TileDistribution extends TileRK implements IGuiActionMessage, IPeripheral {

    protected static final LuaException INVALID_PRIORITY_EXCEPTION = new LuaException("Invalid priority! Valid:\n[1 .. 5]");
    protected static final LuaException INVALID_ABS_OUTPUT_EXCEPTION = new LuaException("Invalid absolute output! Valid:\n>=0 OR -1 = Infinite");
    protected static final LuaException INVALID_REL_OUTPUT_EXCEPTION = new LuaException("Invalid relative output! Valid:\n[0.0 .. 1.0]");

    public static final int ABS_OUTPUT_INFINITE = -1;

    protected static CCHelper.CCMethods METHODS = new CCHelper.CCMethods();
    static {
        METHODS.add(new CCHelper.CCMethodDoc(METHODS));
        METHODS.add(new CCMethodGetSideType());
        METHODS.add(new CCMethodSetSideType());
        METHODS.add(new CCMethodGetPriority());
        METHODS.add(new CCMethodSetPriority());
        METHODS.add(new CCMethodGetOutputAbs());
        METHODS.add(new CCMethodSetOutputAbs());
        METHODS.add(new CCMethodGetOutputRel());
        METHODS.add(new CCMethodSetOutputRel());
        METHODS.add(new CCMethodIsOutputRel());
        METHODS.add(new CCMethodUseRelOutput());
        METHODS.add(new CCMethodGetAvgOutput());
    }

    protected static int HISTORY_SIZE = 60;

    public enum SideType {
        INPUT, OUTPUT, DISABLED
    }

    protected int[] priority = new int[6];
    protected boolean isOutputLimitRelative = false;
    protected int[] maxOutputAbs = new int[6];
    protected float[] maxOutputRel = new float[6];
    protected int[] sides = new int[6];
    protected int[] outputted = new int[6];

    protected int[] history = new int[HISTORY_SIZE];
    protected int historyIdx = 0;
    protected int sum = 0;

    @Override
    public void updateEntity() {
        super.updateEntity();
        if(!worldObj.isRemote) {
            updateHistory();
            markChunkDirty();
        }
    }

    protected void updateHistory() {
        int tmp = 0;
        for (int i = 0; i < 6; i++) {
            tmp += outputted[i];
            outputted[i] = 0;
        }

        sum -= history[historyIdx];
        history[historyIdx++] = tmp;
        sum += tmp;

        if (historyIdx >= history.length) {
            historyIdx = 0;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        priority = nbt.getIntArray("prio");
        maxOutputAbs = nbt.getIntArray("maxOutputAbs");
        int[] tmp = nbt.getIntArray("maxOutputRel");
        maxOutputRel = new float[tmp.length];
        for (int i = 0; i < tmp.length; i++) {
            maxOutputRel[i] = Float.intBitsToFloat(tmp[i]);
        }
        sides = nbt.getIntArray("sides");
        isOutputLimitRelative = nbt.getBoolean("outputRelative");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setIntArray("prio", priority);
        nbt.setIntArray("maxOutputAbs", maxOutputAbs);
        int[] tmp = new int[maxOutputRel.length];
        for (int i = 0; i < tmp.length; i++) {
            tmp[i] = Float.floatToIntBits(maxOutputRel[i]);
        }
        nbt.setIntArray("maxOutputRel", tmp);
        nbt.setIntArray("sides", sides);
        nbt.setBoolean("outputRelative", isOutputLimitRelative);
    }


    @Override
    public void readData(IOStream data) throws IOException {
        for (int i = 0; i < 6; i++) {
            priority[i] = data.readFirstInt();
            maxOutputAbs[i] = data.readFirstInt();
            maxOutputRel[i] = data.readFirstFloat();
            sides[i] = data.readFirstInt();
        }
        isOutputLimitRelative = data.readFirstBoolean();
        sum = data.readFirstInt();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord); //re-render
    }

    @Override
    public void writeData(IOStream data) {
        for (int i = 0; i < 6; i++) {
            data.writeLast(priority[i]);
            data.writeLast(maxOutputAbs[i]);
            data.writeLast(maxOutputRel[i]);
            data.writeLast(sides[i]);
        }
        data.writeLast(isOutputLimitRelative);
        data.writeLast(sum);
    }

    public boolean isOutputLimitRelative() {
        return this.isOutputLimitRelative;
    }

    public int[] getMaxOutputAbs() {
        return maxOutputAbs;
    }

    public float[] getMaxOutputRel() {
        return maxOutputRel;
    }

    public int[] getSides() {
        return sides;
    }

    public int[] getPriorities() {
        return priority;
    }

    public boolean isDisabled(int direction) {
        return sides[direction] == SideType.DISABLED.ordinal();
    }

    public boolean isInput(int direction) {
        return sides[direction] == SideType.INPUT.ordinal();
    }

    public boolean isOutput(int direction) {
        return sides[direction] == SideType.OUTPUT.ordinal();
    }

    public float getAvgOutputPerTick() {
        return (sum / (float)history.length);
    }

    private void changeSide(int side) {
        this.sides[side] = (this.sides[side] + 1) % 3;
    }

    private void changePriority(int side) {
        this.priority[side] = (this.priority[side] + 1) % 5;
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    protected boolean cacheNeighbours() {
        return true;
    }

    @Override
    public void receiveGuiAction(IOStream data) throws IOException {
        RKLog.info("Gui action!!!!");
        int id = data.readFirstInt();
        if(id >= 0 && id < 6) {
            //SIDE BUTTONS
            this.changeSide(id);
            markDirty();
        } else if(id >= 6 && id < 12) {
            //PRIORITY BUTTONS
            this.changePriority(id - 6);
        } else if(id >= 18 && id < 25) {
            //OUTPUT BUTTONS (other)
            if (id == 18) { //ABSOLUTE - PERCENT
                isOutputLimitRelative = !isOutputLimitRelative;
            } else {
                int selectedOutputSide = data.readFirstInt();
                if (this.isOutputLimitRelative()) {
                    if (id == 19) this.subtractOutputRel(selectedOutputSide, 2);
                    if (id == 20) this.subtractOutputRel(selectedOutputSide, 1);
                    if (id == 21) this.subtractOutputRel(selectedOutputSide, 0);
                    if (id == 22) this.addOutputRel(selectedOutputSide, 0);
                    if (id == 23) this.addOutputRel(selectedOutputSide, 1);
                    if (id == 24) this.addOutputRel(selectedOutputSide, 2);
                } else {
                    if (id == 19) this.subtractOutputAbs(selectedOutputSide, 2);
                    if (id == 20) this.subtractOutputAbs(selectedOutputSide, 1);
                    if (id == 21) this.subtractOutputAbs(selectedOutputSide, 0);
                    if (id == 22) this.addOutputAbs(selectedOutputSide, 0);
                    if (id == 23) this.addOutputAbs(selectedOutputSide, 1);
                    if (id == 24) this.addOutputAbs(selectedOutputSide, 2);
                }
            }
        }
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        markChunkDirty();
    }

    private void setSide(int side, int type) {
        sides[side] = type;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    private void setPriority(int side, int priority) {
        this.priority[side] = priority;
    }

    private void setOutputAbs(int side, int output) {
        maxOutputAbs[side] = output;
    }

    private void setOutputRel(int side, float output) {
        maxOutputRel[side] = output;
    }

    private void setUseRelOutput(boolean useRelOutput) {
        this.isOutputLimitRelative = useRelOutput;
    }

    protected abstract void addOutputAbs(int side, int mode);
    protected abstract void addOutputRel(int side, int mode);
    protected abstract void subtractOutputAbs(int side, int mode);
    protected abstract void subtractOutputRel(int side, int mode);

    @Override
    @Optional.Method(modid = "ComputerCraft")
    public String getType() {
        return Reference.TILE_BOILER_BASE_MASTER;
    }

    @Override
    @Optional.Method(modid = "ComputerCraft")
    public String[] getMethodNames() {
        return METHODS.getMethodNames();
    }

    @Override
    @Optional.Method(modid = "ComputerCraft")
    public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
        return METHODS.execute(method, computer, context, arguments, this);
    }

    @Override
    @Optional.Method(modid = "ComputerCraft")
    public void attach(IComputerAccess computer) {

    }

    @Override
    @Optional.Method(modid = "ComputerCraft")
    public void detach(IComputerAccess computer) {

    }

    @Override
    @Optional.Method(modid = "ComputerCraft")
    public boolean equals(IPeripheral other) {
        if(other == null) return false;
        return this.hashCode() == other.hashCode();
    }

    private static class CCMethodGetSideType implements CCHelper.ICCMethod<TileDistribution> {

        @Override
        public String getMethodName() {
            return "getSideType";
        }

        @Override
        public String getMethodDescription() {
            return "\tReturns the type of the side.\n\tUsage: getSideType(<direction>);";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileDistribution tile) throws LuaException {
            if(arguments == null || arguments.length != 1) {
                throw CCHelper.INVALID_ARGUMENT_EXCEPTION;
            }

            ForgeDirection dir = CCHelper.argumentToDirection(arguments[0]);
            if(dir == ForgeDirection.UNKNOWN) {
                throw CCHelper.INVALID_DIRECTION_EXCEPTION;
            }

            return new Object[] { SideType.values()[tile.sides[dir.ordinal()]].name().toLowerCase() };
        }
    }

    private static class CCMethodSetSideType implements CCHelper.ICCMethod<TileDistribution> {

        @Override
        public String getMethodName() {
            return "setSideType";
        }

        @Override
        public String getMethodDescription() {
            return "\tSet's the type of the side.\n\tUsage: setSideType(<direction>, <side_type>);";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileDistribution tile) throws LuaException {
            if(arguments == null || arguments.length != 2) {
                throw CCHelper.INVALID_ARGUMENT_EXCEPTION;
            }

            ForgeDirection dir = CCHelper.argumentToDirection(arguments[0]);
            if(dir == ForgeDirection.UNKNOWN) {
                throw CCHelper.INVALID_DIRECTION_EXCEPTION;
            }

            SideType type = CCHelper.argumentToSideType(arguments[1]);
            if(type == null) {
                throw CCHelper.INVALID_SIDE_TYPE_EXCEPTION;
            }

            tile.setSide(dir.ordinal(), type.ordinal());
            return new Object[] { true };
        }
    }

    private static class CCMethodGetPriority implements CCHelper.ICCMethod<TileDistribution> {

        @Override
        public String getMethodName() {
            return "getPriority";
        }

        @Override
        public String getMethodDescription() {
            return "\tReturns the priority of the side.\n\tUsage: getPriority(<direction>);";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileDistribution tile) throws LuaException {
            if(arguments == null || arguments.length != 1) {
                throw CCHelper.INVALID_ARGUMENT_EXCEPTION;
            }

            ForgeDirection dir = CCHelper.argumentToDirection(arguments[0]);
            if(dir == ForgeDirection.UNKNOWN) {
                throw CCHelper.INVALID_DIRECTION_EXCEPTION;
            }

            return new Object[] { tile.priority[dir.ordinal()] + 1 };
        }
    }

    private static class CCMethodSetPriority implements CCHelper.ICCMethod<TileDistribution> {

        @Override
        public String getMethodName() {
            return "setPriority";
        }

        @Override
        public String getMethodDescription() {
            return "\tSet's the priority of the side.\n\tUsage: setPriority(<direction>, <priority>);";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileDistribution tile) throws LuaException {
            if(arguments == null || arguments.length != 2) {
                throw CCHelper.INVALID_ARGUMENT_EXCEPTION;
            }

            ForgeDirection dir = CCHelper.argumentToDirection(arguments[0]);
            if(dir == ForgeDirection.UNKNOWN) {
                throw CCHelper.INVALID_DIRECTION_EXCEPTION;
            }

            if(!(arguments[1] instanceof Double)) {
                throw INVALID_PRIORITY_EXCEPTION;
            }

            int priority = ((Double) arguments[1]).intValue();
            if(priority < 1 || priority > 5) {
                throw INVALID_PRIORITY_EXCEPTION;
            }

            tile.setPriority(dir.ordinal(), priority - 1);
            return new Object[] { true };
        }
    }

    private static class CCMethodGetOutputAbs implements CCHelper.ICCMethod<TileDistribution> {

        @Override
        public String getMethodName() {
            return "getOutputAbs";
        }

        @Override
        public String getMethodDescription() {
            return "\tReturns the absolute output of the side in RF/t.\n\tUsage: getOutputAbs(<direction>);";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileDistribution tile) throws LuaException {
            if(arguments == null || arguments.length != 1) {
                throw CCHelper.INVALID_ARGUMENT_EXCEPTION;
            }

            ForgeDirection dir = CCHelper.argumentToDirection(arguments[0]);
            if(dir == ForgeDirection.UNKNOWN) {
                throw CCHelper.INVALID_DIRECTION_EXCEPTION;
            }

            return new Object[] { tile.maxOutputAbs[dir.ordinal()] };
        }
    }

    private static class CCMethodSetOutputAbs implements CCHelper.ICCMethod<TileDistribution> {

        @Override
        public String getMethodName() {
            return "setOutputAbs";
        }

        @Override
        public String getMethodDescription() {
            return "\tSet's the absolute output of the side in RF/t.\n\tUsage: setSideType(<direction>, <output>);";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileDistribution tile) throws LuaException {
            if (arguments == null || arguments.length != 2) {
                throw CCHelper.INVALID_ARGUMENT_EXCEPTION;
            }

            ForgeDirection dir = CCHelper.argumentToDirection(arguments[0]);
            if (dir == ForgeDirection.UNKNOWN) {
                throw CCHelper.INVALID_DIRECTION_EXCEPTION;
            }

            if (!(arguments[1] instanceof Double)) {
                throw INVALID_ABS_OUTPUT_EXCEPTION;
            }

            int output = ((Double) arguments[1]).intValue();
            if (output < 0 && output != ABS_OUTPUT_INFINITE) {
                throw INVALID_ABS_OUTPUT_EXCEPTION;
            }

            tile.setOutputAbs(dir.ordinal(), output);
            return new Object[]{true};
        }
    }

    private static class CCMethodGetOutputRel implements CCHelper.ICCMethod<TileDistribution> {

        @Override
        public String getMethodName() {
            return "getOutputRel";
        }

        @Override
        public String getMethodDescription() {
            return "\tReturns the relative output of the side.\n\tUsage: getOutputRel(<direction>);";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileDistribution tile) throws LuaException {
            if (arguments == null || arguments.length != 1) {
                throw CCHelper.INVALID_ARGUMENT_EXCEPTION;
            }

            ForgeDirection dir = CCHelper.argumentToDirection(arguments[0]);
            if (dir == ForgeDirection.UNKNOWN) {
                throw CCHelper.INVALID_DIRECTION_EXCEPTION;
            }

            return new Object[]{tile.maxOutputRel[dir.ordinal()]};
        }
    }

    private static class CCMethodSetOutputRel implements CCHelper.ICCMethod<TileDistribution> {

        @Override
        public String getMethodName() {
            return "setOutputRel";
        }

        @Override
        public String getMethodDescription() {
            return "\tSet's the relative output of the side.\n\tUsage: setOutputRel(<direction>, <output>);";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileDistribution tile) throws LuaException {
            if (arguments == null || arguments.length != 2) {
                throw CCHelper.INVALID_ARGUMENT_EXCEPTION;
            }

            ForgeDirection dir = CCHelper.argumentToDirection(arguments[0]);
            if (dir == ForgeDirection.UNKNOWN) {
                throw CCHelper.INVALID_DIRECTION_EXCEPTION;
            }

            if (!(arguments[1] instanceof Double)) {
                throw INVALID_REL_OUTPUT_EXCEPTION;
            }

            float output = ((Double) arguments[1]).floatValue();
            if (output < 0.0f || output > 1.0f) {
                throw INVALID_REL_OUTPUT_EXCEPTION;
            }

            tile.setOutputRel(dir.ordinal(), output);
            return new Object[]{true};
        }
    }

    private static class CCMethodIsOutputRel implements CCHelper.ICCMethod<TileDistribution> {

        @Override
        public String getMethodName() {
            return "isOutputRel";
        }

        @Override
        public String getMethodDescription() {
            return "\tReturns true if the output-calculation uses relative values.\n\tUsage: isOutputRel();";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileDistribution tile) throws LuaException {
            if (arguments == null || arguments.length != 0) {
                throw CCHelper.INVALID_ARGUMENT_EXCEPTION;
            }
            return new Object[]{ tile.isOutputLimitRelative() };
        }
    }

    private static class CCMethodUseRelOutput implements CCHelper.ICCMethod<TileDistribution> {

        @Override
        public String getMethodName() {
            return "useRelOutput";
        }

        @Override
        public String getMethodDescription() {
            return "\tUse relative/absolute values for output.\n\tUsage: useRelOutput(<true|false>);";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileDistribution tile) throws LuaException {
            if (arguments == null || arguments.length != 1) {
                throw CCHelper.INVALID_ARGUMENT_EXCEPTION;
            }

            if (!(arguments[0] instanceof Boolean)) {
                throw CCHelper.INVALID_ARGUMENT_EXCEPTION;
            }

            tile.setUseRelOutput((Boolean) arguments[0]);
            return new Object[]{true};
        }
    }

    private static class CCMethodGetAvgOutput implements CCHelper.ICCMethod<TileDistribution> {

        @Override
        public String getMethodName() {
            return "getAvgOutput";
        }

        @Override
        public String getMethodDescription() {
            return "\tReturns the average output over the last " + HISTORY_SIZE + " ticks in RF/t.\n\tUsage: getAvgOutput();";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileDistribution tile) throws LuaException {
            if (arguments == null || arguments.length != 0) {
                throw CCHelper.INVALID_ARGUMENT_EXCEPTION;
            }
            return new Object[]{tile.getAvgOutputPerTick()};
        }
    }


}
