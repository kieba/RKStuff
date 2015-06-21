package com.rk.rkstuff.core.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public interface IBlockMulti {
    TileEntity getMasterTileEntity(IBlockAccess access, int x, int y, int z, int meta);
}
