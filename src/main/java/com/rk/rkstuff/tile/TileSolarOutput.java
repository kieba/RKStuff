package com.rk.rkstuff.tile;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.helper.Pos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileSolarOutput extends TileRK implements IFluidHandler, IMultiBlockMasterListener {
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
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (!hasMaster) return null;
        if (resource.getFluid().equals(RkStuff.hotCoolant)) {
            TileSolarMaster master = getMaster();
            int amount = resource.amount;
            amount = (int) Math.min(amount, Math.ceil(master.getHotCoolantTank()));
            if (doDrain) {
                master.setHotCoolantTank(master.getHotCoolantTank() - amount);
            }
            return new FluidStack(RkStuff.hotCoolant, amount);
        }
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        if (!hasMaster) return null;
        TileSolarMaster master = getMaster();
        int amount = (int) Math.min(maxDrain, Math.ceil(master.getHotCoolantTank()));
        if (doDrain) {
            master.setHotCoolantTank(master.getHotCoolantTank() - amount);
        }
        return new FluidStack(RkStuff.hotCoolant, amount);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        if (from == ForgeDirection.UP) return false;
        return true;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        if (!hasMaster) return null;
        return new FluidTankInfo[]{new FluidTankInfo(new FluidStack(RkStuff.hotCoolant, (int) Math.round(getMaster().getHotCoolantTank())), getMaster().getMaxTankCapacity())};
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
