package com.rk.rkstuff.coolant.tile;

import com.rk.rkstuff.coolant.CoolantStack;
import com.rk.rkstuff.core.modinteraction.IWailaBodyProvider;
import com.rk.rkstuff.core.tile.TileRK;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import rk.com.core.io.IOStream;

import java.io.IOException;
import java.util.List;

public class TileCoolantPipe extends TileRK implements ICoolantReceiver, IWailaBodyProvider {

    private static final int COOLANT_CAPACITY = 2000;
    private boolean[] isConnected = new boolean[6];
    private boolean[] hasAdapter = new boolean[6];
    private int[] received = new int[6];
    //private int pressure = Integer.MAX_VALUE;
    private CoolantStack coolant = new CoolantStack();

    public TileCoolantPipe() {
        //this.setUpdateInterval(20);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (worldObj.isRemote) return;

        int[] maxInput = new int[6];
        int totalInput = 0;
        for (int i = 0; i < 6; i++) {
            if (received[i] > 0) {
                received[i]--;
                maxInput[i] = 0;
            } else {
                ICoolantReceiver rcv = getICoolantReceiver(i);
                if (rcv != null) {
                    maxInput[i] = rcv.receiveCoolant(ForgeDirection.values()[i].getOpposite(), Integer.MAX_VALUE, 0.0f, true);
                    totalInput += maxInput[i];
                } else {
                    maxInput[i] = 0;
                }
            }
        }

        if (totalInput > 0) {
            float scale = coolant.getAmount() / (float) totalInput;
            if (scale > 1.0f) scale = 1.0f;
            for (int i = 0; i < 6; i++) {
                ICoolantReceiver rcv = getICoolantReceiver(i);
                if (rcv != null && maxInput[i] > 0) {
                    int amount = (int) Math.floor(maxInput[i] * scale);
                    int received = rcv.receiveCoolant(ForgeDirection.values()[i].getOpposite(), amount, coolant.getTemperature(), false);
                    coolant.remove(received);
                }
            }
        }
        markChunkDirty();


        /*
        int[] maxInput = new int[6];
        int[] outputSides = new int[6];
        int index = 0;
        int minPressure = Integer.MAX_VALUE;
        int totalInput = 0;
        for (int i = 0; i < 6; i++) {
            ICoolantReceiver rcv = getICoolantReceiver(i);
            if (rcv == null) {
                maxInput[i] = 0;
            } else if (rcv.canConnect(ForgeDirection.values()[i].getOpposite())) {
                maxInput[i] = rcv.receiveCoolant(ForgeDirection.values()[i].getOpposite(), Integer.MAX_VALUE, 0.0f, true);

                int p;
                if (rcv instanceof TileCoolantPipe) {
                    p = ((TileCoolantPipe) rcv).getPressure();
                } else {
                    p = maxInput[i] == 0 ? Integer.MAX_VALUE : 0;
                }

                if (p < minPressure) {
                    minPressure = p;
                    index = 0;
                    totalInput = maxInput[i];
                    if (maxInput[i] > 0) outputSides[index++] = i;
                } else if (p == minPressure && p != Integer.MAX_VALUE) {
                    if (maxInput[i] > 0) outputSides[index++] = i;
                    totalInput += maxInput[i];
                }
            }
        }

        if (index > 0 && totalInput > 0) {
            float scale = coolant.getAmount() / (float) totalInput;
            if (scale > 1.0f) scale = 1.0f;
            for (int i = 0; i < index; i++) {
                int side = outputSides[i];
                ICoolantReceiver rcv = getICoolantReceiver(side);
                int amount = (int) Math.floor(maxInput[side] * scale);
                int received = rcv.receiveCoolant(ForgeDirection.values()[side].getOpposite(), amount, coolant.getTemperature(), false);
                coolant.remove(received);
            }
        }

        if (minPressure == Integer.MAX_VALUE) {
            pressure = Integer.MAX_VALUE;
        } else {
            pressure = minPressure + 1;
        }
        */
    }

    private ICoolantReceiver getICoolantReceiver(int side) {
        if (getNeighbour(side) instanceof ICoolantReceiver) return (ICoolantReceiver) getNeighbour(side);
        return null;
    }

    /*
    public int getPressure() {
        return pressure;
    }
    */

    @Override
    public void onNeighborChange(ForgeDirection dir) {
        super.onNeighborChange(dir);
        if (worldObj.isRemote) return;

        int side = dir.ordinal();
        TileEntity te = getNeighbour(side);
        if (te instanceof ICoolantConnection && ((ICoolantConnection) te).canConnect(dir.getOpposite())) {
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
        coolant.readData(data);
        //pressure = data.readFirstInt();
    }

    @Override
    public void writeData(IOStream data) {
        for (int i = 0; i < 6; i++) {
            data.writeLast(isConnected[i]);
            data.writeLast(hasAdapter[i]);
        }
        coolant.writeData(data);
        //data.writeLast(pressure);
    }

    public boolean[] getConnectedSides() {
        return isConnected;
    }

    public boolean[] getAdapterSides() {
        return hasAdapter;
    }

    @Override
    public int receiveCoolant(ForgeDirection from, int maxAmount, double temperature, boolean simulate) {
        int amount = Math.min(maxAmount, COOLANT_CAPACITY - coolant.getAmount());
        if (!simulate && amount > 0) {
            coolant.add(amount, temperature);
            received[from.ordinal()] = 20;
        }
        return amount;
    }

    @Override
    public boolean canConnect(ForgeDirection from) {
        return true;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentBody, IWailaDataAccessor accessor, IWailaConfigHandler configHandler) {
        //currentBody.add("Pressure " + pressure);
        //currentBody.add("Coolant " + coolant);
        return currentBody;
    }
}
