package com.rk.rkstuff.tank.tile;

import com.rk.rkstuff.coolant.tile.ICoolantReceiver;
import com.rk.rkstuff.core.tile.IMultiBlockMasterListener;
import com.rk.rkstuff.core.tile.TileMultiBlockMaster;
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

public class TileTankValve extends TileRK implements IMultiBlockMasterListener, IFluidHandler, ICoolantReceiver {
    private TileTankAdapter master;
    private boolean isOutput;
    private TileEntity[] neighbours = new TileEntity[6];

    @Override
    public void registerMaster(TileMultiBlockMaster tileMaster) {
        master = (TileTankAdapter) tileMaster;
    }

    @Override
    public void unregisterMaster() {
        master = null;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("isOutput", isOutput);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        isOutput = data.getBoolean("isOutput");
    }

    @Override
    protected boolean hasGui() {
        return false;
    }

    @Override
    public void readData(IOStream data) throws IOException {
        isOutput = data.readFirstBoolean();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void writeData(IOStream data) {
        data.writeLast(isOutput);
    }

    @Override
    public int receiveCoolant(ForgeDirection from, int maxAmount, float temperature, boolean simulate) {
        if (master == null) return 0;
        if (isOutput()) return 0;
        return master.receiveCoolant(maxAmount, temperature, simulate);
    }

    @Override
    public boolean canReceive(ForgeDirection from) {
        if (master == null) return false;
        if (isOutput()) return false;
        return master.canReceive();
    }

    @Override
    public void updateEntity() {
        if (master == null) return;
        if (worldObj.isRemote) return;

        if (isOutput()) {
            if (master.isCoolantStack() && master.getCurrentCoolantStack().getAmount() > 0) {
                int totalInput = 0;
                int[] maxInput = new int[6];
                for (int i = 0; i < 6; i++) {
                    if (!(neighbours[i] instanceof ICoolantReceiver)) continue;
                    ICoolantReceiver rcv = (ICoolantReceiver) neighbours[i];
                    maxInput[i] = rcv.receiveCoolant(ForgeDirection.values()[i], Integer.MAX_VALUE, 0.0f, true);
                    totalInput += maxInput[i];
                }

                float scale = master.getCurrentCoolantStack().getAmount() / (float) totalInput;
                if (scale > 1.0f) scale = 1.0f;

                for (int i = 0; i < 6; i++) {
                    if (!(neighbours[i] instanceof ICoolantReceiver)) continue;
                    ICoolantReceiver rcv = (ICoolantReceiver) neighbours[i];
                    int amount = (int) Math.floor(maxInput[i] * scale);
                    int received = rcv.receiveCoolant(ForgeDirection.values()[i], amount, master.getCurrentCoolantStack().getTemperature(), false);
                    master.removeCoolant(received);
                }
            }

            if (master.isFluidStack() && master.getCurrentFluidStack().amount > 0) {
                int totalInput = 0;
                int[] maxInput = new int[6];
                for (int i = 0; i < 6; i++) {
                    if (!(neighbours[i] instanceof IFluidHandler)) continue;
                    IFluidHandler rcv = (IFluidHandler) neighbours[i];
                    maxInput[i] = rcv.fill(ForgeDirection.values()[i], master.getCurrentFluidStack(), false);
                    totalInput += maxInput[i];
                }

                float scale = master.getCurrentFluidStack().amount / (float) totalInput;
                if (scale > 1.0f) scale = 1.0f;

                for (int i = 0; i < 6; i++) {
                    if (!(neighbours[i] instanceof IFluidHandler)) continue;
                    IFluidHandler rcv = (IFluidHandler) neighbours[i];
                    int amount = (int) Math.floor(maxInput[i] * scale);
                    rcv.fill(ForgeDirection.values()[i], master.drainFluid(amount, true), true);
                }
            }
        }
    }

    public void onNeighborTileChange(ForgeDirection dir) {
        if (worldObj.isRemote) return;
        int side = dir.ordinal();
        int x = xCoord + dir.offsetX;
        int y = yCoord + dir.offsetY;
        int z = zCoord + dir.offsetZ;
        TileEntity te = worldObj.getTileEntity(x, y, z);
        neighbours[side] = te;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void onBlockPlaced() {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            onNeighborTileChange(dir);
        }
    }

    public void toggleOutput() {
        isOutput = !isOutput;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public boolean isOutput() {
        return isOutput;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (isOutput()) return 0;
        if (master == null) return 0;
        return master.addFluid(resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (!isOutput()) return null;
        if (master == null) return null;
        return master.drainFluid(resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        if (!isOutput()) return null;
        if (master == null) return null;
        return master.drainFluid(maxDrain, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        if (master.isCoolantStack()) return false;
        if (master == null) return false;
        if (master.isFluidStack() && master.getCurrentFluidStack().getFluid().equals(fluid)) {
            return !isOutput();
        }
        return false;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        if (master.isCoolantStack()) return false;
        if (master == null) return false;
        if (master.isFluidStack() && master.getCurrentFluidStack().getFluid().equals(fluid)) {
            return isOutput();
        }
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        if (master.isFluidStack()) {
            return new FluidTankInfo[]{new FluidTankInfo(master.getCurrentFluidStack(), master.getMaxStorage())};
        }
        return null;
    }
}
