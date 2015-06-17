package com.rk.rkstuff.tank.tile;

import com.rk.rkstuff.coolant.tile.ICoolantReceiver;
import com.rk.rkstuff.core.tile.IMultiBlockMasterListener;
import com.rk.rkstuff.core.tile.TileMultiBlockMaster;
import com.rk.rkstuff.core.tile.TileRK;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileTankValve extends TileRK implements IMultiBlockMasterListener, IFluidTank, ICoolantReceiver {
    private TileTankAdapter master;
    private boolean isOutput;
    private ICoolantReceiver[] neighbours = new ICoolantReceiver[6];

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

    }

    @Override
    public void writeData(IOStream data) {

    }

    @Override
    public FluidStack getFluid() {
        if (master == null) return null;
        if (master.isFluidStack()) {
            return master.getCurrentFluidStack();
        }
        return null;
    }

    @Override
    public int getFluidAmount() {
        if (master == null) return 0;
        return master.getCurrentStorage();
    }

    @Override
    public int getCapacity() {
        if (master == null) return 0;
        return master.getMaxStorage();
    }

    @Override
    public FluidTankInfo getInfo() {
        if (master == null) return null;
        if (master.isFluidStack()) {
            return new FluidTankInfo(master.getCurrentFluidStack(), master.getMaxStorage());
        }
        return null;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (master == null) return 0;
        return master.addFluid(resource, doFill);
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (master == null) return null;
        return master.drainFluid(maxDrain, doDrain);
    }


    @Override
    public int receiveCoolant(ForgeDirection from, int maxAmount, float temperature, boolean simulate) {
        if (master == null) return 0;
        return master.receiveCoolant(maxAmount, temperature, simulate);
    }

    @Override
    public boolean canReceive(ForgeDirection from) {
        if (master == null) return false;
        return master.canReceive();
    }

    public void onNeighborTileChange(ForgeDirection dir) {
        if (worldObj.isRemote) return;
        int side = dir.ordinal();
        int x = xCoord + dir.offsetX;
        int y = yCoord + dir.offsetY;
        int z = zCoord + dir.offsetZ;
        TileEntity te = worldObj.getTileEntity(x, y, z);
        if (te instanceof ICoolantReceiver) {
            neighbours[side] = (ICoolantReceiver) te;
        } else {
            neighbours[side] = null;
        }
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void onBlockPlaced() {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            onNeighborTileChange(dir);
        }
    }
}
