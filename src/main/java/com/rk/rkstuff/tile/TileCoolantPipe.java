package com.rk.rkstuff.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileCoolantPipe extends TileRK implements ICoolantReceiver, INeighbourListener {

    private ICoolantReceiver[] neighbours = new ICoolantReceiver[6];
    private boolean[] isConnected = new boolean[6];
    private boolean[] hasAdapter = new boolean[6];

    @Override
    public void updateEntity() {
        super.updateEntity();

    }

    public void onBlockPlaced() {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            onNeighborTileChange(dir);
        }
    }

    @Override
    public void onNeighborTileChange(ForgeDirection dir) {
        if (worldObj.isRemote) return;
        int side = dir.ordinal();
        int x = xCoord + dir.offsetX;
        int y = yCoord + dir.offsetY;
        int z = zCoord + dir.offsetZ;
        TileEntity te = worldObj.getTileEntity(x, y, z);
        if (te instanceof ICoolantReceiver) {
            neighbours[side] = (ICoolantReceiver) te;
            isConnected[side] = true;
            hasAdapter[side] = !(te instanceof TileCoolantPipe);
        } else {
            neighbours[side] = null;
            isConnected[side] = false;
            hasAdapter[side] = false;
        }
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    protected boolean hasGui() {
        return false;
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
    public int receiveCoolant(ForgeDirection from, CoolantStack stack, boolean simulate) {
        return 0;
    }

    @Override
    public boolean canReceive(ForgeDirection from) {
        return true;
    }
}
