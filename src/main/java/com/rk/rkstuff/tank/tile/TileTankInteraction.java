package com.rk.rkstuff.tank.tile;

import com.rk.rkstuff.core.tile.IMultiBlockMasterListener;
import com.rk.rkstuff.core.tile.TileMultiBlockMaster;
import com.rk.rkstuff.core.tile.TileRK;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileTankInteraction extends TileRK implements IMultiBlockMasterListener {
    private TileTankAdapter master;

    @Override
    public void registerMaster(TileMultiBlockMaster tileMaster) {
        master = (TileTankAdapter) tileMaster;
    }

    @Override
    public void unregisterMaster() {
        master = null;
    }


    @Override
    protected boolean hasGui() {
        return true;
    }

    @Override
    public void readData(IOStream data) throws IOException {

    }

    public boolean hasMaster() {
        return master != null;
    }

    public TileTankAdapter getMaster() {
        return master;
    }

    @Override
    public void writeData(IOStream data) {

    }
}
