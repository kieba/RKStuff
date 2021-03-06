package com.rk.rkstuff.core.tile;

import com.rk.rkstuff.helper.MultiBlockHelper;
import com.rk.rkstuff.network.message.ICustomMessage;
import rk.com.core.io.IOStream;

import java.io.IOException;

public abstract class TileMultiBlockMaster extends TileRK implements ICustomMessage {

    private int interval = 40; //check structure every 40 ticks (2 seconds)
    private int tick = interval;
    protected boolean isBuild;
    protected MultiBlockHelper.Bounds bounds;

    /**
     * Check that structure is properly formed (master only)
     * returns true if correct
     */
    public abstract boolean checkMultiBlockForm();

    public void build() {
        if(!isBuild && checkMultiBlockForm()) {
            bounds = setupStructure();
            isBuild = true;
        }
    }

    public void reset() {
        if(isBuild) {
            isBuild = false;
            resetStructure();
            bounds = null;
        }
    }

    /**
     * Setup all the blocks in the structure
     */
    protected abstract MultiBlockHelper.Bounds setupStructure();

    /**
     * Reset all the parts of the structure
     */
    protected abstract void resetStructure();

    protected abstract void updateMaster();

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (!worldObj.isRemote) {
            if (isBuild) {
                updateMaster();
            } else {
                // Constantly check if structure is formed until it is.
                if (tick >= interval) {
                    build();
                    tick = 0;
                } else {
                    tick++;
                }
            }
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        reset();
    }


    @Override
    public void readData(IOStream data) throws IOException {
        isBuild = data.readFirstBoolean();
    }

    @Override
    public void writeData(IOStream data) {
        data.writeLast(isBuild);
    }

    public boolean isBuild() {
        return isBuild;
    }
}
