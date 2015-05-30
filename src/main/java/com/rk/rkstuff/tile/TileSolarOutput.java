package com.rk.rkstuff.tile;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.helper.Pos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileSolarOutput extends TileRK implements IFluidHandler, IMultiBlockMasterListener {
    private Pos masterPosition;
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
        return new FluidStack(RkStuff.coolCoolant, 10);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return new FluidStack(RkStuff.coolCoolant, 10);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return true;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[]{new FluidTankInfo(new FluidStack(RkStuff.coolCoolant, 100), 1000)};
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
}
