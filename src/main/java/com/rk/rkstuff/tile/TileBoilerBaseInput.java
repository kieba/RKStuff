package com.rk.rkstuff.tile;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileBoilerBaseInput extends TileRK implements IBoilerBaseTile, IFluidHandler {

    private TileBoilerBaseMaster master;

    @Override
    public void setMaster(int masterX, int masterY, int masterZ) {
        master = (TileBoilerBaseMaster) worldObj.getTileEntity(masterX, masterY, masterZ);
    }

    @Override
    public void resetMaster() {
        master = null;
    }

    public boolean hasMaster() {
        return master != null;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if(hasMaster()) {
            return master.fill(from, resource, doFill);
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
        return hasMaster() && master.canFill(from, fluid);
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
}
