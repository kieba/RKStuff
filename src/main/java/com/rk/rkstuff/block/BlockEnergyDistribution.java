package com.rk.rkstuff.block;

import com.rk.rkstuff.tile.TileEnergyDistribution;
import com.rk.rkstuff.util.Reference;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockEnergyDistribution extends BlockRK implements ITileEntityProvider {

    public BlockEnergyDistribution() {
        super(Material.iron);
        setBlockName(Reference.BLOCK_ENERGY_DISTRIBUTION);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEnergyDistribution();
    }
}
