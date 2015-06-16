package com.rk.rkstuff.boiler.tile;

import com.rk.rkstuff.core.tile.IMultiBlockMasterListener;
import com.rk.rkstuff.core.tile.TileMultiBlockMaster;
import com.rk.rkstuff.core.tile.TileRK;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileBoilerBaseInput extends TileRK implements IMultiBlockMasterListener, IFluidHandler {

    private TileBoilerBaseMaster master;

    public boolean hasMaster() {
        return master != null;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if(hasMaster()) {
            return master.fill(resource, doFill);
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
        return hasMaster() && master.canFill(fluid);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        if(hasMaster()) {
            return master.getTankInfoInput();
        }
        return new FluidTankInfo[0];
    }

    @Override
    protected boolean hasGui() {
        return false;
    }


    @Override
    public void registerMaster(TileMultiBlockMaster tileMaster) {
        master = (TileBoilerBaseMaster) tileMaster;
    }

    @Override
    public void unregisterMaster() {
        master = null;
    }

    @Override
    public void readData(IOStream data) throws IOException {
    }

    @Override
    public void writeData(IOStream data) {
    }
}
