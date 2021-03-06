package com.rk.rkstuff.coolant.tile;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.coolant.CoolantStack;
import com.rk.rkstuff.core.tile.TileRK;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileCoolantInjector extends TileRK implements IFluidHandler, ICoolantConnection {

    private static final int MAX_COOLANT_AMOUNT = 2000;
    private CoolantStack coolantStack = new CoolantStack();

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (worldObj.isRemote) return;
        if (coolantStack.getAmount() <= 0) return;

        int totalInput = 0;
        int[] maxInput = new int[6];
        for (int i = 0; i < 6; i++) {
            TileEntity te = getNeighbour(i);
            if (te == null || !(te instanceof ICoolantReceiver)) continue;
            ICoolantReceiver rcv = (ICoolantReceiver) te;
            maxInput[i] = rcv.receiveCoolant(ForgeDirection.values()[i].getOpposite(), Integer.MAX_VALUE, 0.0f, true);
            totalInput += maxInput[i];
        }

        float scale = coolantStack.getAmount() / (float) totalInput;
        if (scale > 1.0f) scale = 1.0f;

        for (int i = 0; i < 6; i++) {
            if (maxInput[i] == 0) continue;
            ICoolantReceiver rcv = (ICoolantReceiver) getNeighbour(i);
            int amount = (int) Math.floor(maxInput[i] * scale);
            int received = rcv.receiveCoolant(ForgeDirection.values()[i].getOpposite(), amount, CoolantStack.celsiusToKelvin(20.0f), false);
            coolantStack.remove(received);
        }
        markChunkDirty();
    }

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
    protected boolean cacheNeighbours() {
        return true;
    }

    @Override
    public void readData(IOStream data) throws IOException {

    }

    @Override
    public void writeData(IOStream data) {

    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (resource.getFluid() != RkStuff.fluidCoolant) return 0;
        int amount = Math.min(resource.amount, MAX_COOLANT_AMOUNT - coolantStack.getAmount());
        if (doFill) {
            coolantStack.add(amount, CoolantStack.celsiusToKelvin(20.0f));
            markChunkDirty();
        }
        return amount;
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
        return fluid == RkStuff.fluidCoolant;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[]{
                new FluidTankInfo(new FluidStack(RkStuff.fluidCoolant, coolantStack.getAmount()), MAX_COOLANT_AMOUNT)
        };
    }

    @Override
    public boolean canConnect(ForgeDirection from) {
        return true;
    }
}
