package com.rk.rkstuff.tile;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.network.message.IGuiActionMessage;
import net.minecraft.nbt.NBTTagCompound;
import rk.com.core.io.IOStream;

import java.io.IOException;

public abstract class TileDistribution extends TileRK implements IGuiActionMessage {

    protected int[] priority = new int[6];
    protected boolean isOutputLimitRelative = false;
    protected int[] maxOutputAbs = new int[6];
    protected float[] maxOutputRel = new float[6];
    protected int[] sides = new int[6];
    protected int[] outputted = new int[6];

    protected int[] history = new int[60];
    protected int historyIdx = 0;
    protected int sum = 0;

    @Override
    public void updateEntity() {
        super.updateEntity();
        if(!worldObj.isRemote) {

            int tmp = 0;
            for (int i = 0; i < 6; i++) {
                tmp += outputted[i];
                outputted[i] = 0;
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
        return sides[direction] == 2;
    }

    public boolean isInput(int direction) {
        return sides[direction] == 0;
    }

    public boolean isOutput(int direction) {
        return sides[direction] == 1;
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
    protected boolean hasGui() {
        return true;
    }

    @Override
    public void receiveGuiAction(IOStream data) throws IOException {
        int id = data.readFirstInt();
        if(id >= 0 && id < 6) {
            //SIDE BUTTONS
            this.changeSide(id);
            worldObj.notifyBlockChange(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
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
    }

    protected abstract void addOutputAbs(int side, int mode);
    protected abstract void addOutputRel(int side, int mode);
    protected abstract void subtractOutputAbs(int side, int mode);
    protected abstract void subtractOutputRel(int side, int mode);
}
