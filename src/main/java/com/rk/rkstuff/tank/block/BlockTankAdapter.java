package com.rk.rkstuff.tank.block;

import com.rk.rkstuff.core.block.BlockRK;
import com.rk.rkstuff.tank.tile.TileTankAdapter;
import com.rk.rkstuff.util.Reference;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockTankAdapter extends BlockRK implements ITileEntityProvider, ITankBlock {
    public BlockTankAdapter() {
        super(Material.iron, Reference.BLOCK_TANK_ADAPTER);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileTankAdapter();
    }

    @Override
    public boolean isBlockNormalCube() {
        return false;
    }
}
