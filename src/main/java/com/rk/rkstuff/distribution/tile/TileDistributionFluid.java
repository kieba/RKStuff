package com.rk.rkstuff.distribution.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

import java.util.Arrays;

public class TileDistributionFluid extends TileDistribution implements IFluidHandler {

    private int[] maxOutput = new int[6];
    private int[] count = new int[5];
    private int[][] sidesByPrio = new int[5][6];

    @Override
    protected void addOutputAbs(int side, int mode) {
        int amount;
        if(mode == 0) {
            amount = 10;
        } else if(mode == 1) {
            amount = 50;
        } else {
            amount = 250;
        }
        maxOutputAbs[side] += amount;
    }

    @Override
    protected void addOutputRel(int side, int mode) {
        float amount;
        if(mode == 0) {
            amount = 0.001f;
        } else if(mode == 1) {
            amount = 0.01f;
        } else {
            amount = 0.1f;
        }
        maxOutputRel[side] += amount;
        if(maxOutputRel[side] > 1.0f) maxOutputRel[side] = 1.0f;
    }

    @Override
    protected void subtractOutputAbs(int side, int mode) {
        int amount;
        if(mode == 0) {
            amount = 10;
        } else if(mode == 1) {
            amount = 50;
        } else {
            amount = 250;
        }
        maxOutputAbs[side] -= amount;
        if(maxOutputAbs[side] < 0) maxOutputAbs[side] = 0;
    }

    @Override
    protected void subtractOutputRel(int side, int mode) {
        float amount;
        if(mode == 0) {
            amount = 0.001f;
        } else if(mode == 1) {
            amount = 0.01f;
        } else {
            amount = 0.1f;
        }
        maxOutputRel[side] -= amount;
        if(maxOutputRel[side] < 0) maxOutputRel[side] = 0.0f;
    }


    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if(!isInput(from.ordinal())) return 0;

        int maxReceive = resource.amount;
        int maxReceiveCpy = maxReceive;

        Arrays.fill(count, 0);

        //compute max outputs for each side
        for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            int side = dir.ordinal();
            maxOutput[side] = 0;
            if(isOutput(side)) {
                int maxInput = fill(dir, resource.getFluid(), Integer.MAX_VALUE, false);
                if(isOutputLimitRelative()) {
                    maxOutput[side] = Math.min(Math.round(maxOutputRel[side] * maxReceive), maxInput);
                } else {
                    maxOutput[side] = Math.min(maxOutputAbs[side] - outputted[side], maxInput);
                }
            }
        }

        //sort sides by prio
        for (int s = 0; s < 6; s++) {
            if(!isOutput(s) || maxOutput[s] == 0) continue;
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

            if(outputSum == 0) continue;

            float scale = maxReceive / (float)outputSum;
            if(scale >= 1.0f)  scale = 1.0f;

            for (int i = 0; i < count[p]; i++) {
                int side = sidesByPrio[p][i];
                int ret = fill(ForgeDirection.VALID_DIRECTIONS[i], resource.getFluid(), Math.round(maxOutput[side] * scale), doFill);
                if(doFill) {
                    outputted[side] += ret;
                }
                maxReceive -= ret;
            }

            if(maxReceive == 0) break;
        }
        return maxReceiveCpy - maxReceive;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return !isDisabled(from.ordinal());
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[0];
    }

    private int fill(ForgeDirection dir, Fluid fluid, int amount, boolean doFill) {
        TileEntity tile = getNeighbour(dir.ordinal());
        if(tile instanceof IFluidHandler) {
            return ((IFluidHandler) tile).fill(dir.getOpposite(), new FluidStack(fluid, amount), doFill);
        }
        if(tile instanceof IFluidTank) {
            return ((IFluidTank) tile).fill(new FluidStack(fluid, amount), doFill);
        }
        return 0;
    }

}
