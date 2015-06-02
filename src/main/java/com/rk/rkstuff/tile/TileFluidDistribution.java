package com.rk.rkstuff.tile;

import cofh.api.energy.IEnergyReceiver;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

public class TileFluidDistribution extends TileDistribution implements IFluidHandler {

    @Override
    protected void addOutputAbs(int side, int mode) {
        int amount;
        if(mode == 0) {
            amount = 10;
        } else if(mode == 1) {
            amount = 100;
        } else {
            amount = 1000;
        }
        maxOutputAbs[side] += amount;
    }

    @Override
    protected void addOutputRel(int side, int mode) {
        float amount;
        if(mode == 0) {
            amount = 0.025f;
        } else if(mode == 1) {
            amount = 0.05f;
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
            amount = 100;
        } else {
            amount = 1000;
        }
        maxOutputAbs[side] -= amount;
        if(maxOutputAbs[side] < 0) maxOutputAbs[side] = 0;
    }

    @Override
    protected void subtractOutputRel(int side, int mode) {
        float amount;
        if(mode == 0) {
            amount = 0.025f;
        } else if(mode == 1) {
            amount = 0.05f;
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

        int[] maxInput = new int[6];
        int inputTotal = 0;

        //get max inputs from all directions
        for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if(isOutput(dir.ordinal())) {
                int transfer = fill(dir.getOpposite(), resource.getFluid(), Integer.MAX_VALUE, false);
                if(!isOutputLimitRelative) {
                    transfer = Math.min(maxOutputAbs[dir.ordinal()] - outputted[dir.ordinal()], transfer);
                }
                maxInput[dir.ordinal()] = transfer;
                inputTotal += transfer;
            }
        }

        //compute the outputs for each side
        int[] output = new int[6];
        if(!isOutputLimitRelative) {
            if(inputTotal <= maxReceive) {
                for (int i = 0; i < 6; i++) {
                    output[i] = maxInput[i];
                }
            } else {
                inputTotal = maxReceive;

                for (int p = 5; p >= 0; p--) {
                    int sum = 0;
                    for (int i = 0; i < 6; i++) {
                        if(priority[i] == p) {
                            sum += maxInput[i];
                        }
                    }

                    float scale = (sum == 0) ? 0 : inputTotal / (float)sum;
                    if(scale > 1.0) scale = 1.0f;

                    for (int i = 0; i < 6; i++) {
                        if(priority[i] == p) {
                            output[i]= (int) (maxInput[i] * scale);
                            inputTotal -= output[i];
                        }
                    }
                }
            }
        } else {
            int tmp = Math.min(inputTotal, maxReceive);
            for (int i = 0; i < 6; i++) {
                output[i] = Math.min(maxInput[i], Math.min(Math.round(maxOutputRel[i] * tmp), inputTotal));
                inputTotal -= output[i];
            }
        }

        //output the energy
        int totalOutput = 0;
        for (int i = 0; i < 6; i++) {
            if(output[i] > 0) {
                int ret = fill(ForgeDirection.VALID_DIRECTIONS[i].getOpposite(), resource.getFluid(), output[i], doFill);
                if(doFill) {
                    outputted[i] += ret;
                }
                totalOutput += ret;
            }
        }
        return totalOutput;
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
        TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
        if(tile instanceof IFluidHandler) {
            return ((IFluidHandler) tile).fill(dir, new FluidStack(fluid, amount), doFill);
        }
        if(tile instanceof IFluidTank) {
            return ((IFluidTank) tile).fill(new FluidStack(fluid, amount), doFill);
        }
        return 0;
    }

}
