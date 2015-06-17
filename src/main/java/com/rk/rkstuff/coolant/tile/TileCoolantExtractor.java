package com.rk.rkstuff.coolant.tile;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.coolant.CoolantStack;
import com.rk.rkstuff.core.tile.TileRK;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileCoolantExtractor extends TileRK implements IFluidHandler, ICoolantReceiver {

    private static final int MAX_COOLANT_AMOUNT = 2000;
    private CoolantStack coolantStack = new CoolantStack(0, 20.0f);

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        coolantStack.writeToNBT("coolant", tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        coolantStack.readFromNBT("coolant", tag);
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
    public int receiveCoolant(ForgeDirection from, int maxAmount, float temperature, boolean simulate) {
        int amount = Math.min(maxAmount, MAX_COOLANT_AMOUNT - coolantStack.getAmount());
        if (!simulate) {
            coolantStack.add(amount, coolantStack.getTemperature());
        }
        return amount;
    }

    @Override
    public boolean canReceive(ForgeDirection from) {
        return true;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource.getFluid() != RkStuff.fluidUsedCoolant) return null;
        return drain(from, resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        int amount = Math.min(coolantStack.getAmount(), maxDrain);
        if (doDrain) {
            coolantStack.remove(amount);
        }
        return new FluidStack(RkStuff.fluidUsedCoolant, amount);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return fluid == RkStuff.fluidUsedCoolant;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[]{
                new FluidTankInfo(new FluidStack(RkStuff.fluidUsedCoolant, coolantStack.getAmount()), MAX_COOLANT_AMOUNT)
        };
    }
}
