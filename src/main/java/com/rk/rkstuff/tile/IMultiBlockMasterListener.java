package com.rk.rkstuff.tile;

import com.rk.rkstuff.helper.Pos;

public interface IMultiBlockMasterListener {
    /**
     * Register Position
     *
     * @param position Position of MasterBlock
     */
    void registerMaster(Pos position);

    /**
     * Unregister Position
     */
    void unregisterMaster();
}
