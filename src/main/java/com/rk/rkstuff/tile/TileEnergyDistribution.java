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

public class TileEnergyDistribution extends TileRK implements IEnergyReceiver, IGuiActionMessage {

    private int[] priority = new int[6];
    private boolean isOutputLimitRelative = false;
    private int[] maxOutputAbs = new int[6];
    private float[] maxOutputRel = new float[6];
    private int[] sides = new int[6];
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
    protected boolean hasGui() {
        return true;
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
                        transfer = Math.min(maxOutputAbs[dir.ordinal()] - energyOutputted[dir.ordinal()], transfer);
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
                            int scaled = (int) (maxInput[i] * scale);
                            output[i] = scaled;
                            inputTotal -= scaled;
                        }
                    }
                }
            }
        } else {
            inputTotal = Math.min(inputTotal, maxReceive);
            for (int i = 0; i < 6; i++) {
                output[i] = Math.min(maxInput[i], Math.round(maxOutputRel[i] * inputTotal));
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
        return !isDisabled(from.ordinal());
    }

    private boolean isDisabled(int direction) {
        return sides[direction] == 2;
    }

    private boolean isInput(int direction) {
        return sides[direction] == 0;
    }

    private boolean isOutput(int direction) {
        return sides[direction] == 1;
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

    private void changeSide(int side) {
        this.sides[side] = (this.sides[side] + 1) % 3;
    }

    private void changePriority(int side) {
        this.priority[side] = (this.priority[side] + 1) % 5;
    }

    private void addOutputAbs(int side, int amount) {
        maxOutputAbs[side] += amount;
    }

    private void addOutputRel(int side, float amount) {
        maxOutputRel[side] += amount;
        if(maxOutputRel[side] > 1.0f) maxOutputRel[side] = 1.0f;
    }

    private void subtractOutputAbs(int side, int amount) {
        maxOutputAbs[side] -= amount;
        if(maxOutputAbs[side] < 0) maxOutputAbs[side] = 0;
    }

    private void subtractOutputRel(int side, float amount) {
        maxOutputRel[side] -= amount;
        if(maxOutputRel[side] < 0) maxOutputRel[side] = 0.0f;
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

    @Override
    public void receiveGuiAction(IOStream data) throws IOException {
        int id = data.readFirstInt();
        if(id >= 0 && id < 6) {
            //SIDE BUTTONS
            this.changeSide(id);
            worldObj.notifyBlockChange(xCoord, yCoord, zCoord, RkStuff.blockEnergyDistribution);
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
                    if (id == 19) this.subtractOutputRel(selectedOutputSide, 0.1f);
                    if (id == 20) this.subtractOutputRel(selectedOutputSide, 0.05f);
                    if (id == 21) this.subtractOutputRel(selectedOutputSide, 0.025f);
                    if (id == 22) this.addOutputRel(selectedOutputSide, 0.025f);
                    if (id == 23) this.addOutputRel(selectedOutputSide, 0.05f);
                    if (id == 24) this.addOutputRel(selectedOutputSide, 0.1f);
                } else {
                    if (id == 19) this.subtractOutputAbs(selectedOutputSide, 5000);
                    if (id == 20) this.subtractOutputAbs(selectedOutputSide, 500);
                    if (id == 21) this.subtractOutputAbs(selectedOutputSide, 50);
                    if (id == 22) this.addOutputAbs(selectedOutputSide, 50);
                    if (id == 23) this.addOutputAbs(selectedOutputSide, 500);
                    if (id == 24) this.addOutputAbs(selectedOutputSide, 5000);
                }
            }
        }
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
}
