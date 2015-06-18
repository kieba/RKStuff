package com.rk.rkstuff.solar.tile;

import com.rk.rkstuff.coolant.tile.ICoolantReceiver;
import com.rk.rkstuff.core.tile.IMultiBlockMasterListener;
import com.rk.rkstuff.core.tile.TileMultiBlockMaster;
import com.rk.rkstuff.core.tile.TileRK;
import net.minecraftforge.common.util.ForgeDirection;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileSolarInput extends TileRK implements ICoolantReceiver, IMultiBlockMasterListener {
    private TileSolarMaster master;


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

    @Override
    public int receiveCoolant(ForgeDirection from, int maxAmount, float temperature, boolean simulate) {
        if (master == null) return 0;
        return master.receiveCoolant(from, maxAmount, temperature, simulate);
    }

    @Override
    public boolean canReceive(ForgeDirection from) {
        if (from == ForgeDirection.UP) {
            return false;
        }
        return true;
    }
}
