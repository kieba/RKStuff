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

public class TileDistributionEnergy extends TileDistribution implements IEnergyReceiver {

    private int[] maxOutput = new int[6];
    private int[] count = new int[5];
    private int[][] sidesByPrio = new int[5][6];

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        if(!isInput(from.ordinal())) return 0;

        IEnergyReceiver[] receiver = new IEnergyReceiver[6];
        int maxReceiveCpy = maxReceive;

        Arrays.fill(count, 0);

        //compute max outputs for each side
        for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            int side = dir.ordinal();
            maxOutput[side] = 0;
            if(isOutput(side)) {
                receiver[side] = getIEnergyReceiver(dir);
                if(receiver[side] != null) {
                    int maxInput = receiver[side].receiveEnergy(dir.getOpposite(), Integer.MAX_VALUE, true);
                    if(isOutputLimitRelative()) {
                        maxOutput[side] = Math.min(Math.round(maxOutputRel[side] * maxReceive), maxInput);
                    } else {
                        maxOutput[side] = Math.min(maxOutputAbs[side] - outputted[side], maxInput);
                    }
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
                int ret = receiver[side].receiveEnergy(ForgeDirection.VALID_DIRECTIONS[side].getOpposite(), Math.round(maxOutput[side] * scale), simulate);
                if(!simulate) {
                    outputted[side] += ret;
                }
                maxReceive -= ret;
            }

            if(maxReceive == 0) break;
        }
        return maxReceiveCpy - maxReceive;
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
            amount = 0.001f;
        } else if(mode == 1) {
            amount = 0.01f;
        } else {
            amount = 0.1f;
        }
        maxOutputRel[side] -= amount;
        if(maxOutputRel[side] < 0) maxOutputRel[side] = 0.0f;
    }


}
