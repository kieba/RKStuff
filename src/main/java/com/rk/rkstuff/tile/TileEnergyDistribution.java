package com.rk.rkstuff.tile;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileEnergyDistribution extends TileRK implements IEnergyReceiver {

    private int[] priority = new int[6];
    private boolean isOutputLimitRelative = false;
    private int[] maxOutput = new int[6]; //if isOutputLimitRelative == true then output is between 0 ... 100
    private byte[] sides = new byte[6];
    private int[] energyOutputted = new int[6];

    private int[] history = new int[60];
    private int historyIdx = 0;
    private int sum = 0;

    @Override
    public void updateEntity() {
        super.updateEntity();
        if(!worldObj.isRemote) {

            int tmp = 0;
            for (int i = 0; i < 6; i++) {
                tmp += energyOutputted[i];
                energyOutputted[i] = 0;
            }

            sum -= history[historyIdx];
            history[historyIdx++] = tmp;
            sum += tmp;

            if(historyIdx >= history.length) {
                historyIdx = 0;
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        priority = nbt.getIntArray("prio");
        maxOutput = nbt.getIntArray("output");
        sides = nbt.getByteArray("sides");
        isOutputLimitRelative = nbt.getBoolean("outputRelative");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setIntArray("prio", priority);
        nbt.setIntArray("output", maxOutput);
        nbt.setByteArray("sides", sides);
        nbt.setBoolean("outputRelative", isOutputLimitRelative);
    }

    @Override
    protected boolean hasGui() {
        return true;
    }

    @Override
    public void readData(IOStream data) throws IOException {
        for (int i = 0; i < 6; i++) {
            priority[i] = data.readFirstInt();
            maxOutput[i] = data.readFirstInt();
            sides[i] = data.readFirstByte();
        }
        isOutputLimitRelative = data.readFirstBoolean();
        sum = data.readFirstInt();
    }

    @Override
    public void writeData(IOStream data) {
        for (int i = 0; i < 6; i++) {
            data.writeLast(priority[i]);
            data.writeLast(maxOutput[i]);
            data.writeLast(sides[i]);
        }
        data.writeLast(isOutputLimitRelative);
        data.writeLast(sum);
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        if(!isInput(from.getOpposite())) return 0;

        int[] maxInput = new int[6];
        int inputTotal = 0;
        IEnergyReceiver[] receiver = new IEnergyReceiver[6];

        //get max inputs from all directions
        for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if(isOutput(dir)) {
                receiver[dir.ordinal()] = getIEnergyReceiver(dir);
                if(receiver[dir.ordinal()] != null) {
                    int transfer = receiver[dir.ordinal()].receiveEnergy(dir, Integer.MAX_VALUE, true);
                    if(!isOutputLimitRelative) {
                        transfer = Math.min(maxOutput[dir.ordinal()] - energyOutputted[dir.ordinal()], transfer);
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
                for (int p = 5; p >= 0; p--) {
                    int sum = 0;
                    for (int i = 0; i < 6; i++) {
                        if(priority[i] == p) {
                            sum = maxInput[i];
                        }
                    }

                    float scale = inputTotal / sum;
                    if(scale > 1.0) scale = 1.0f;

                    for (int i = 0; i < 6; i++) {
                        if(priority[i] == p) {
                            output[i] = (int) (maxInput[i] * scale);
                        }
                    }

                    inputTotal -= sum;
                }
            }
        } else {
            for (int i = 0; i < 6; i++) {
                output[i] = Math.min(maxInput[i], (int) (maxOutput[i] / 100.0f * inputTotal));
            }
        }

        //output the energy
        int totalOutput = 0;
        for (int i = 0; i < 6; i++) {
            if(receiver[i] != null && output[i] > 0) {
                int ret = receiver[i].receiveEnergy(ForgeDirection.VALID_DIRECTIONS[i].getOpposite(), output[i], simulate);
                if(!simulate) {
                    energyOutputted[i] += ret;
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
        return isDisabled(from.getOpposite());
    }

    private boolean isDisabled(ForgeDirection direction) {
        return isDisabled(direction.ordinal());
    }

    private boolean isInput(ForgeDirection direction) {
        return isInput(direction.ordinal());
    }

    private boolean isOutput(ForgeDirection direction) {
        return isOutput(direction.ordinal());
    }

    private boolean isDisabled(int direction) {
        return sides[direction] == 0;
    }

    private boolean isInput(int direction) {
        return sides[direction] == 1;
    }

    private boolean isOutput(int direction) {
        return sides[direction] == 2;
    }

    public void setDisabled(int direction) {
        sides[direction] = 0;
    }

    public void setInput(int direction) {
        sides[direction] = 1;
    }

    public void setOutput(int direction) {
        sides[direction] = 2;
    }

    private IEnergyReceiver getIEnergyReceiver(ForgeDirection dir) {
        TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
        if(tile instanceof IEnergyReceiver) {
            return (IEnergyReceiver) tile;
        }
        return null;
    }

    public float getAvgOutputPerTick() {
        return (sum / (float)history.length);
    }

}
