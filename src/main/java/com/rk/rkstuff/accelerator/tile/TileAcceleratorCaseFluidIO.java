package com.rk.rkstuff.accelerator.tile;

import com.rk.rkstuff.coolant.CoolantStack;
import com.rk.rkstuff.coolant.tile.ICoolantReceiver;
import com.rk.rkstuff.core.tile.IMultiBlockMasterListener;
import com.rk.rkstuff.core.tile.TileMultiBlockMaster;
import com.rk.rkstuff.core.tile.TileRK;
import com.rk.rkstuff.helper.FluidHelper;
import com.rk.rkstuff.util.RKLog;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileAcceleratorCaseFluidIO extends TileRK implements IMultiBlockMasterListener, ICoolantReceiver {

    private static final int MAX_OUTPUT = 200;
    private TileAcceleratorMaster master;
    private int side;
    private boolean isOutput;

    public boolean hasMaster() {
        return master != null;
    }

    public int getSide() {
        return side;
    }

    @Override
    public void registerMaster(TileMultiBlockMaster tileMaster) {
        master = (TileAcceleratorMaster) tileMaster;
        side = master.getSideForFluidIO(this);
        if (side == -1) {
            RKLog.error("Invalid side for TileAcceleratorCaseFluidIO!");
            master = null;
        }
    }

    @Override
    public void unregisterMaster() {
        master = null;
        side = -1;
    }

    @Override
    public void readData(IOStream data) throws IOException {
        isOutput = data.readFirstBoolean();
        markBlockForUpdate();
    }

    @Override
    public void writeData(IOStream data) {
        data.writeFirst(isOutput);
    }

    public void handleOutput() {
        if (hasMaster() && isOutput) {
            CoolantStack stack = master.getCoolantStack(side);
            stack.remove(FluidHelper.outputCoolantToNeighbours(neighbours, Math.min(MAX_OUTPUT, stack.getAmount()), stack.getTemperature()));
        }
    }

    public void toggleIOMode() {
        if (!worldObj.isRemote) {
            isOutput = !isOutput;
            markDirty();
            markBlockForUpdate();
        }
    }

    public boolean isOutput() {
        return isOutput;
    }

    @Override
    protected boolean cacheNeighbours() {
        return true;
    }

    @Override
    public int receiveCoolant(ForgeDirection from, int maxAmount, float temperature, boolean simulate) {
        if (!hasMaster() || isOutput) return 0;
        return master.receiveCoolant(side, maxAmount, temperature, simulate);
    }

    @Override
    public boolean canConnect(ForgeDirection from) {
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        isOutput = tag.getBoolean("isOutput");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean("isOutput", isOutput);
    }
}
