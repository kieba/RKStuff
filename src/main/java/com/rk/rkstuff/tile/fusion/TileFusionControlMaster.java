package com.rk.rkstuff.tile.fusion;

import com.rk.rkstuff.helper.MultiBlockHelper;
import com.rk.rkstuff.tile.TileMultiBlockMaster;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileFusionControlMaster extends TileMultiBlockMaster {
    @Override
    protected boolean hasGui() {
        return false;
    }

    @Override
    public boolean checkMultiBlockForm() {
        return false;
    }

    @Override
    protected MultiBlockHelper.Bounds setupStructure() {
        return null;
    }

    @Override
    protected void resetStructure() {

    }

    @Override
    protected void updateMaster() {

    }

    @Override
    public void readData(IOStream data) throws IOException {

    }

    @Override
    public void writeData(IOStream data) {

    }
}
