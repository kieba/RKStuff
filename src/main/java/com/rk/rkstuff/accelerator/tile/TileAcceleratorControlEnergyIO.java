package com.rk.rkstuff.accelerator.tile;

import cofh.api.energy.IEnergyHandler;
import com.rk.rkstuff.core.tile.IMultiBlockMasterListener;
import com.rk.rkstuff.core.tile.TileMultiBlockMaster;
import com.rk.rkstuff.core.tile.TileRK;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileAcceleratorControlEnergyIO extends TileRK implements IMultiBlockMasterListener, IEnergyHandler {

    private TileAcceleratorMaster master;
    private boolean isOutput;

    public boolean hasMaster() {
        return master != null;
    }

    @Override
    public void registerMaster(TileMultiBlockMaster tileMaster) {
        master = (TileAcceleratorMaster) tileMaster;
    }

    @Override
    public void unregisterMaster() {
        master = null;
    }

    @Override
    public void readData(IOStream data) throws IOException {
        isOutput = data.readFirstBoolean();
        markBlockForUpdate();
    }

    @Override
    public void writeData(IOStream data) {
        data.writeFirst(isOutput);
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        if (hasMaster() && !isOutput()) {
            return master.receiveEnergy(maxReceive, simulate);
        }
        return 0;
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        if (hasMaster()) {
            return master.getStoredEnergyRF();
        }
        return 0;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        if (hasMaster()) {
            return master.getMaxEnergyStorageRF();
        }
        return 0;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    public void toggleIOMode() {
        if (!worldObj.isRemote) {
            isOutput = !isOutput;
            markDirty();
            markBlockForUpdate();
        }
    }

    public boolean isOutput() {
        return isOutput;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        isOutput = tag.getBoolean("isOutput");

    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean("isOutput", isOutput);
    }
}
