package com.rk.rkstuff.tile;

import com.rk.rkstuff.RkStuff;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileSolarOutput extends TileRK implements IFluidHandler{
    private int masterX;
    private int masterY;
    private int masterZ;
    private boolean hasMaster = false;



    public void setMaster(int x, int y, int z){
        masterX = x;
        masterY = y;
        masterZ = z;
        hasMaster = true;
    }

    public void resetMaster(){
        hasMaster = false;
        masterX = 0;
        masterY = 0;
        masterZ = 0;
    }

    @Override
    public void writeToNBT(NBTTagCompound par1)
    {
        super.writeToNBT(par1);
    }

    @Override
    public void readFromNBT(NBTTagCompound par1)
    {
        super.readFromNBT(par1);
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
}
