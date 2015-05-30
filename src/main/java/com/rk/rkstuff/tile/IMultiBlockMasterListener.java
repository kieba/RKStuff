package com.rk.rkstuff.tile;

import com.rk.rkstuff.helper.Pos;

public interface IMultiBlockMasterListener {
    /**
     * Register MasterBock
     *
     * @param tileMaster MasterBlock
     */
    void registerMaster(TileMultiBlockMaster tileMaster);

    /**
     * Unregister Position
     */
    void unregisterMaster();
}
