package com.rk.rkstuff.coolant.tile;

import com.rk.rkstuff.coolant.CoolantStack;
import com.rk.rkstuff.core.tile.TileRK;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileCoolantPipe extends TileRK implements ICoolantReceiver {

    private static final int COOLANT_CAPACITY = 2000;
    private boolean[] isConnected = new boolean[6];
    private boolean[] hasAdapter = new boolean[6];
    private int pressure = Integer.MAX_VALUE;
    private CoolantStack coolant = new CoolantStack(0, 0);

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (worldObj.isRemote) return;

        int[] maxInput = new int[6];
        int[] outputSides = new int[6];
        int index = 0;
        int minPressure = Integer.MAX_VALUE;
        for (int i = 0; i < 6; i++) {
            ICoolantReceiver rcv = getICoolantReceiver(i);
            if (rcv == null) {
                maxInput[i] = 0;
            } else if (rcv.canConnect(ForgeDirection.values()[i])) {
                int p;
                if (rcv instanceof TileCoolantPipe) {
                    p = ((TileCoolantPipe) rcv).getPressure();
                } else {
                    p = rcv.receiveCoolant(ForgeDirection.values()[i], Integer.MAX_VALUE, 0.0f, true) > 0 ? 0 : Integer.MAX_VALUE;
                }
                if (p < minPressure) {
                    minPressure = p;
                    index = 0;
                    outputSides[index++] = i;
                } else if (p == minPressure) {
                    outputSides[index++] = i;
                }
            }
        }

        if (index > 0) {
            int totalInput = 0;
            for (int i = 0; i < index; i++) {
                int side = outputSides[i];
                ICoolantReceiver rcv = getICoolantReceiver(side);
                maxInput[i] = rcv.receiveCoolant(ForgeDirection.values()[i], Integer.MAX_VALUE, 0.0f, true);
                totalInput += maxInput[i];
            }

            float scale = coolant.getAmount() / (float) totalInput;
            if (scale > 1.0f) scale = 1.0f;

            for (int i = 0; i < index; i++) {
                int side = outputSides[i];
                ICoolantReceiver rcv = getICoolantReceiver(side);
                int amount = (int) Math.floor(maxInput[i] * scale);
                int received = rcv.receiveCoolant(ForgeDirection.values()[i], amount, coolant.getTemperature(), false);
                coolant.remove(received);
            }
        }


        if (minPressure == Integer.MAX_VALUE) {
            pressure = Integer.MAX_VALUE;
        } else {
            pressure = minPressure + 1;
        }
    }

    private ICoolantReceiver getICoolantReceiver(int side) {
        if (getNeighbour(side) instanceof ICoolantReceiver) return (ICoolantReceiver) getNeighbour(side);
        return null;
    }

    public int getPressure() {
        return pressure;
    }

    @Override
    public void onNeighborChange(ForgeDirection dir) {
        super.onNeighborChange(dir);
        if (worldObj.isRemote) return;

        int side = dir.ordinal();
        TileEntity te = getNeighbour(side);
        if (te instanceof ICoolantConnection && ((ICoolantConnection) te).canConnect(dir)) {
            isConnected[side] = true;
            hasAdapter[side] = !(te instanceof TileCoolantPipe);
        } else {
            isConnected[side] = false;
            hasAdapter[side] = false;
        }
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    protected boolean cacheNeighbours() {
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        int connected = tag.getInteger("isConnected");
        int adapter = tag.getInteger("hasAdapter");
        for (int i = 0; i < 6; i++) {
            isConnected[i] = ((connected >> i) & 0x01) == 0x01;
            hasAdapter[i] = ((adapter >> i) & 0x01) == 0x01;
        }
        coolant.readFromNBT("coolant", tag);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        int connected = 0;
        int adapter = 0;

        for (int i = 0; i < 6; i++) {
            if (isConnected[i]) connected |= 0x01 << i;
            if (hasAdapter[i]) adapter |= 0x01 << i;
        }

        tag.setInteger("isConnected", connected);
        tag.setInteger("hasAdapter", adapter);
        coolant.writeToNBT("coolant", tag);
    }

    @Override
    public void readData(IOStream data) throws IOException {
        for (int i = 0; i < 6; i++) {
            isConnected[i] = data.readFirstBoolean();
            hasAdapter[i] = data.readFirstBoolean();
        }
    }

    @Override
    public void writeData(IOStream data) {
        for (int i = 0; i < 6; i++) {
            data.writeLast(isConnected[i]);
            data.writeLast(hasAdapter[i]);
        }
    }

    public boolean[] getConnectedSides() {
        return isConnected;
    }

    public boolean[] getAdapterSides() {
        return hasAdapter;
    }

    @Override
    public int receiveCoolant(ForgeDirection from, int maxAmount, float temperature, boolean simulate) {
        int amount = Math.min(maxAmount, COOLANT_CAPACITY - coolant.getAmount());
        if (!simulate) {
            coolant.add(amount, temperature);
        }
        return amount;
    }

    @Override
    public boolean canConnect(ForgeDirection from) {
        return true;
    }

}
