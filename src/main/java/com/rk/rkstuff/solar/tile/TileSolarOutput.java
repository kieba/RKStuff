package com.rk.rkstuff.solar.tile;

import com.rk.rkstuff.coolant.CoolantStack;
import com.rk.rkstuff.coolant.tile.ICoolantConnection;
import com.rk.rkstuff.core.tile.IMultiBlockMasterListener;
import com.rk.rkstuff.core.tile.TileMultiBlockMaster;
import com.rk.rkstuff.core.tile.TileRK;
import com.rk.rkstuff.helper.FluidHelper;
import net.minecraftforge.common.util.ForgeDirection;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileSolarOutput extends TileRK implements IMultiBlockMasterListener, ICoolantConnection {
    private TileSolarMaster master;

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
    public void registerMaster(TileMultiBlockMaster tileMaster) {
        master = (TileSolarMaster) tileMaster;
        master.addOutputTrigger(this);
    }

    @Override
    public void unregisterMaster() {
        master = null;
    }

    @Override
    public void updateEntityByMaster() {
        super.updateEntity();
        if (master != null) {
            CoolantStack stack = master.getCoolantBuffer();
            stack.remove(FluidHelper.outputCoolantToNeighbours(neighbours, stack.getAmount(), stack.getTemperature()));
        }
    }

    @Override
    public boolean canConnect(ForgeDirection from) {
        if (from == ForgeDirection.UP) return false;
        return true;
    }
}
