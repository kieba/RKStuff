package com.rk.rkstuff.solar.tile;

import com.rk.rkstuff.core.tile.IMultiBlockMasterListener;
import com.rk.rkstuff.core.tile.TileMultiBlockMaster;
import com.rk.rkstuff.core.tile.TileRK;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileSolarOutput extends TileRK implements IMultiBlockMasterListener {
    private TileSolarMaster master;

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
    public void registerMaster(TileMultiBlockMaster tileMaster) {
        master = (TileSolarMaster) tileMaster;
    }

    @Override
    public void unregisterMaster() {
        master = null;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (master != null && master.getCoolantBuffer().getAmount() > 1) {
            //TODO: RICTAS
        }
    }
}
