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
    private TileSolarMaster master;

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (master == null) return null;
        if (from == ForgeDirection.UP) return null;
        if (resource.getFluid().equals(RkStuff.hotCoolant)) {
            TileSolarMaster master = getMaster();
            int amount = resource.amount;
            amount = (int) Math.min(amount, Math.floor(master.getHotCoolantTank()));
            if (amount < 0) return null;
            if (doDrain) {
                master.setHotCoolantTank(master.getHotCoolantTank() - amount);
            }
            return new FluidStack(RkStuff.hotCoolant, amount);
        }
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        if (master == null) return null;
        if (from == ForgeDirection.UP) return null;
        TileSolarMaster master = getMaster();
        int amount = (int) Math.min(maxDrain, Math.floor(master.getHotCoolantTank()));
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
        if (master == null) return null;
        return new FluidTankInfo[]{new FluidTankInfo(new FluidStack(RkStuff.hotCoolant, (int) Math.round(getMaster().getHotCoolantTank())), getMaster().getMaxTankCapacity())};
    }

    public TileSolarMaster getMaster() {
        return master;
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

    @Override
    public void registerMaster(TileMultiBlockMaster tileMaster) {
        master = (TileSolarMaster) tileMaster;
    }

    @Override
    public void unregisterMaster() {
        master = null;
    }
}
