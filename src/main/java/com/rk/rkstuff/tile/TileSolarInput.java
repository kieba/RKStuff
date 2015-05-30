package com.rk.rkstuff.tile;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.helper.Pos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import rk.com.core.io.IOStream;

import java.io.IOException;
import java.util.Map;

public class TileSolarInput extends TileRK implements IFluidHandler, IMultiBlockMasterListener {
    private Pos masterPosition = new Pos();
    private boolean hasMaster = false;

    @Override
    public void writeToNBT(NBTTagCompound data)
    {
        super.writeToNBT(data);
        data.setBoolean("hasMaster", hasMaster);
        if (hasMaster) {
            masterPosition.writeToNBT(data, "masterPos");
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound data)
    {
        super.readFromNBT(data);
        hasMaster = data.getBoolean("hasMaster");
        if (hasMaster) {
            masterPosition.readFromNBT(data, "masterPos");
        }

    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (!hasMaster) return 0;
        if (resource.getFluid().equals(RkStuff.coolCoolant)) {
            TileSolarMaster master = getMaster();
            int amount = resource.amount;
            amount = Math.min(amount, master.getMaxTankCapacity() - (int) Math.ceil(master.getCoolCoolantTank()));
            if (amount < 0) return 0;
            if (doFill) {
                master.setCoolCoolantTank(master.getCoolCoolantTank() + amount);
            }
            return amount;
        }
        return 0;
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
        if (from == ForgeDirection.UP) return false;
        return true;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        if (!hasMaster) return null;
        return new FluidTankInfo[]{new FluidTankInfo(new FluidStack(RkStuff.coolCoolant, (int) Math.round(getMaster().getCoolCoolantTank())), getMaster().getMaxTankCapacity())};
    }

    @Override
    public void registerMaster(Pos position) {
        hasMaster = true;
        masterPosition = position;
    }

    @Override
    public void unregisterMaster() {
        masterPosition = new Pos();
        hasMaster = false;
    }

    public TileSolarMaster getMaster() {
        if (hasMaster) {
            return (TileSolarMaster) worldObj.getTileEntity(masterPosition.x, masterPosition.y, masterPosition.z);
        }
        return null;
    }

    @Override
    protected boolean hasGui() {
        return false;
    }

    @Override
    public void readData(IOStream data) throws IOException {

    }

    @Override
    public void writeData(IOStream data) {

    }
}
