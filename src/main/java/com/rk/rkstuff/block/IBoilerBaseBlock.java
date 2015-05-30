package com.rk.rkstuff.block;

import com.rk.rkstuff.tile.TileBoilerBaseMaster;
import net.minecraft.world.World;

public interface IBoilerBaseBlock {

    public TileBoilerBaseMaster getMaster(World world, int x, int y, int z);

}
