package com.rk.rkstuff.core.tile;

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
