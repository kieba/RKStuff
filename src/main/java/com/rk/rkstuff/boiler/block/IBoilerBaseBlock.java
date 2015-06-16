package com.rk.rkstuff.boiler.block;

import com.rk.rkstuff.boiler.tile.TileBoilerBaseMaster;
import net.minecraft.world.World;

public interface IBoilerBaseBlock {

    public TileBoilerBaseMaster getMaster(World world, int x, int y, int z);

}
