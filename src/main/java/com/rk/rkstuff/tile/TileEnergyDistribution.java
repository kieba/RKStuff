package com.rk.rkstuff.tile;

import cofh.api.energy.IEnergyReceiver;
import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.helper.RKLog;
import com.rk.rkstuff.network.message.IGuiActionMessage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import rk.com.core.io.IOStream;

import java.io.IOException;
import java.util.Arrays;

public class TileEnergyDistribution extends TileDistribution implements IEnergyReceiver {

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        if(!isInput(from.ordinal())) return 0;

        int[] maxInput = new int[6];
        int inputTotal = 0;
        IEnergyReceiver[] receiver = new IEnergyReceiver[6];

        //get max inputs from all directions
        for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if(isOutput(dir.ordinal())) {
                receiver[dir.ordinal()] = getIEnergyReceiver(dir);
                if(receiver[dir.ordinal()] != null) {
                    int transfer = receiver[dir.ordinal()].receiveEnergy(dir.getOpposite(), Integer.MAX_VALUE, true);
                    if(!isOutputLimitRelative) {
                        transfer = Math.min(maxOutputAbs[dir.ordinal()] - outputted[dir.ordinal()], transfer);
                    }
                    maxInput[dir.ordinal()] = transfer;
                    inputTotal += transfer;
                }
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
            if(receiver[i] != null && output[i] > 0) {
                int ret = receiver[i].receiveEnergy(ForgeDirection.VALID_DIRECTIONS[i].getOpposite(), output[i], simulate);
                if(!simulate) {
                    outputted[i] += ret;
                }
                totalOutput += ret;
            }
        }
        return totalOutput;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return 0;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return !isDisabled(from.ordinal());
    }

    private IEnergyReceiver getIEnergyReceiver(ForgeDirection dir) {
        TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
        if(tile instanceof IEnergyReceiver) {
            return (IEnergyReceiver) tile;
        }
        return null;
    }


    @Override
    protected void addOutputAbs(int side, int mode) {
        int amount;
        if(mode == 0) {
            amount = 50;
        } else if(mode == 1) {
            amount = 500;
        } else {
            amount = 5000;
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
            amount = 50;
        } else if(mode == 1) {
            amount = 500;
        } else {
            amount = 5000;
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


}
