package com.rk.rkstuff.block;

import com.rk.rkstuff.tile.TileTankAdapter;
import com.rk.rkstuff.util.Reference;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockTankAdapter extends BlockRK implements ITileEntityProvider {
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
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean isBlockNormalCube() {
        return false;
    }
}
