package com.rk.rkstuff.fusion.tile;

import com.rk.rkstuff.coolant.tile.ICoolantReceiver;
import com.rk.rkstuff.core.tile.IMultiBlockMasterListener;
import com.rk.rkstuff.core.tile.TileMultiBlockMaster;
import com.rk.rkstuff.core.tile.TileRK;
import com.rk.rkstuff.util.RKLog;
import net.minecraftforge.common.util.ForgeDirection;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileFusionCaseFluidIO extends TileRK implements IMultiBlockMasterListener, ICoolantReceiver {

    private TileFusionControlMaster master;
    private int side;

    public boolean hasMaster() {
        return master != null;
    }

    public int getSide() {
        return side;
    }

    @Override
    public void registerMaster(TileMultiBlockMaster tileMaster) {
        master = (TileFusionControlMaster) tileMaster;
        side = master.getSideForFluidIO(this);
        if (side == -1) {
            RKLog.error("Invalid side for TileFusionCaseFluidIO!");
        }
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

    @Override
    public int receiveCoolant(ForgeDirection from, int maxAmount, float temperature, boolean simulate) {
        if (!hasMaster()) return 0;
        return master.receiveCoolant(side, maxAmount, temperature, simulate);
    }

    @Override
    public boolean canConnect(ForgeDirection from) {
        return hasMaster();
    }
}
