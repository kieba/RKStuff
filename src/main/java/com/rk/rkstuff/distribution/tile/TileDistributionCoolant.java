package com.rk.rkstuff.distribution.tile;

import com.rk.rkstuff.coolant.CoolantStack;
import com.rk.rkstuff.coolant.tile.ICoolantReceiver;
import com.rk.rkstuff.helper.CCHelper;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Arrays;

public class TileDistributionCoolant extends TileDistribution implements ICoolantReceiver {

    static {
        METHODS.add(new CCMethodGetAvgTemperature());
    }

    private CoolantStack coolantOutputted = new CoolantStack(0, 0.0f);
    private float[] temperatureHistory = new float[HISTORY_SIZE];
    private float temperatureSum = 0.0f;

    private int[] maxOutput = new int[6];
    private int[] count = new int[5];
    private int[][] sidesByPrio = new int[5][6];

    @Override
    protected void addOutputAbs(int side, int mode) {
        int amount;
        if (mode == 0) {
            amount = 10;
        } else if (mode == 1) {
            amount = 50;
        } else {
            amount = 250;
        }
        maxOutputAbs[side] += amount;
    }

    @Override
    protected void addOutputRel(int side, int mode) {
        float amount;
        if (mode == 0) {
            amount = 0.001f;
        } else if (mode == 1) {
            amount = 0.01f;
        } else {
            amount = 0.1f;
        }
        maxOutputRel[side] += amount;
        if (maxOutputRel[side] > 1.0f) maxOutputRel[side] = 1.0f;
    }

    @Override
    protected void subtractOutputAbs(int side, int mode) {
        int amount;
        if (mode == 0) {
            amount = 10;
        } else if (mode == 1) {
            amount = 50;
        } else {
            amount = 250;
        }
        maxOutputAbs[side] -= amount;
        if (maxOutputAbs[side] < 0) maxOutputAbs[side] = 0;
    }

    @Override
    protected void subtractOutputRel(int side, int mode) {
        float amount;
        if (mode == 0) {
            amount = 0.001f;
        } else if (mode == 1) {
            amount = 0.01f;
        } else {
            amount = 0.1f;
        }
        maxOutputRel[side] -= amount;
        if (maxOutputRel[side] < 0) maxOutputRel[side] = 0.0f;
    }

    protected void updateHistory() {
        temperatureSum -= temperatureHistory[historyIdx];
        temperatureHistory[historyIdx + 1] = coolantOutputted.getTemperature();
        temperatureSum += temperatureHistory[historyIdx + 1];
        coolantOutputted.set(0, 0.0f);
        super.updateHistory();
    }

    public float getAvgTemperature() {
        return temperatureSum / history.length;
    }

    @Override
    public int receiveCoolant(ForgeDirection from, int maxAmount, float temperature, boolean simulate) {
        if (!isInput(from.ordinal())) return 0;

        ICoolantReceiver[] receiver = new ICoolantReceiver[6];
        int maxReceiveCpy = maxAmount;

        Arrays.fill(count, 0);

        //compute max outputs for each side
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            int side = dir.ordinal();
            maxOutput[side] = 0;
            if (isOutput(side)) {
                receiver[side] = getICoolantReceiver(dir.ordinal());
                if (receiver[side] != null) {
                    int maxInput = receiver[side].receiveCoolant(dir.getOpposite(), Integer.MAX_VALUE, 0.0f, true);
                    if (isOutputLimitRelative()) {
                        maxOutput[side] = Math.min(Math.round(maxOutputRel[side] * maxAmount), maxInput);
                    } else {
                        int out = maxOutputAbs[side] == ABS_OUTPUT_INFINITE ? Integer.MAX_VALUE : maxOutputAbs[side] - outputted[side];
                        maxOutput[side] = Math.min(out, maxInput);
                    }
                }
            }
        }

        //sort sides by prio
        for (int s = 0; s < 6; s++) {
            if (!isOutput(s) || maxOutput[s] == 0) continue;
            int p = priority[s];
            sidesByPrio[p][count[p]] = s;
            count[p]++;
        }

        //handle all sides by prio
        for (int p = 4; p >= 0; p--) {
            int outputSum = 0;
            for (int i = 0; i < count[p]; i++) {
                int side = sidesByPrio[p][i];
                outputSum += maxOutput[side];
            }

            if (outputSum == 0) continue;

            float scale = maxAmount / (float) outputSum;
            if (scale >= 1.0f) scale = 1.0f;

            for (int i = 0; i < count[p]; i++) {
                int side = sidesByPrio[p][i];
                int ret = receiver[side].receiveCoolant(ForgeDirection.VALID_DIRECTIONS[side].getOpposite(), Math.round(maxOutput[side] * scale), temperature, simulate);
                if (!simulate) {
                    outputted[side] += ret;
                }
                maxAmount -= ret;
            }

            if (maxAmount == 0) break;
        }
        int tmp = maxReceiveCpy - maxAmount;
        if (!simulate) coolantOutputted.add(tmp, temperature);
        return tmp;
    }

    @Override
    public boolean canConnect(ForgeDirection from) {
        return !isDisabled(from.ordinal());
    }

    private ICoolantReceiver getICoolantReceiver(int side) {
        if (getNeighbour(side) instanceof ICoolantReceiver) return (ICoolantReceiver) getNeighbour(side);
        return null;
    }

    private static class CCMethodGetAvgTemperature implements CCHelper.ICCMethod<TileDistributionCoolant> {

        @Override
        public String getMethodName() {
            return "getAvgTemperature";
        }

        @Override
        public String getMethodDescription() {
            return "\tReturns the average temperature over the last " + HISTORY_SIZE + " ticks in °C.\n\tUsage: getAvgTemperature();";
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, Object[] arguments, TileDistributionCoolant tile) throws LuaException {
            if (arguments == null || arguments.length != 0) {
                throw CCHelper.INVALID_ARGUMENT_EXCEPTION;
            }
            return new Object[]{tile.getAvgTemperature()};
        }
    }
}
