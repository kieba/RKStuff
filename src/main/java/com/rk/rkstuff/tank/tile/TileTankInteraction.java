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
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void unregisterMaster() {
        master = null;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }


    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    public void readData(IOStream data) throws IOException {
        if (data.readFirstBoolean()) {
            master = (TileTankAdapter) worldObj.getTileEntity(data.readFirstInt(), data.readFirstInt(), data.readFirstInt());
        } else {
            master = null;
        }
    }

    public boolean hasMaster() {
        return master != null;
    }

    public TileTankAdapter getMaster() {
        return master;
    }

    @Override
    public void writeData(IOStream data) {
        if (hasMaster()) {
            data.writeLast(true);
            data.writeLast(master.xCoord);
            data.writeLast(master.yCoord);
            data.writeLast(master.zCoord);
        } else {
            data.writeLast(false);
        }
    }
}
