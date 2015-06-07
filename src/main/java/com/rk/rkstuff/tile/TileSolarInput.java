package com.rk.rkstuff.tile;

import com.rk.rkstuff.RkStuff;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileSolarInput extends TileRK implements IFluidHandler, IMultiBlockMasterListener {
    private TileSolarMaster master;


    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (from == ForgeDirection.UP) return 0;
        if (master == null) return 0;
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
        if (master == null) return null;
        return new FluidTankInfo[]{new FluidTankInfo(new FluidStack(RkStuff.coolCoolant, (int) Math.round(getMaster().getCoolCoolantTank())), getMaster().getMaxTankCapacity())};
    }

    @Override
    public void registerMaster(TileMultiBlockMaster tileMaster) {
        master = (TileSolarMaster) tileMaster;
    }

    @Override
    public void unregisterMaster() {
        master = null;
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
}
