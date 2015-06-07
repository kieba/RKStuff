package com.rk.rkstuff.tile;

import net.minecraft.util.AxisAlignedBB;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileTankAdapter extends TileRK {
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
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 4, zCoord + 1);
    }
}
