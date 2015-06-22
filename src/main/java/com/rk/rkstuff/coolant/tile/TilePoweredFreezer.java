package com.rk.rkstuff.coolant.tile;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.core.tile.TileRKReconfigurable;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TilePoweredFreezer extends TileRKReconfigurable {
    public static byte[] defaultConfig = {0, 0, 0, 0, 0, 0};

    public TilePoweredFreezer() {
        super(RkStuff.blockPoweredFreezer);
    }

    @Override
    protected boolean hasGui() {
        return true;
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
    protected byte[] getDefaultSideConfig() {
        return defaultConfig;
    }

    @Override
    public int getNumConfig(int i) {
        return 3;
    }
}
