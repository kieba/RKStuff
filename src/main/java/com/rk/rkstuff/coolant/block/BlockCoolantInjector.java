package com.rk.rkstuff.coolant.block;

import com.rk.rkstuff.coolant.tile.TileCoolantInjector;
import com.rk.rkstuff.core.block.BlockRK;
import com.rk.rkstuff.util.Reference;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCoolantInjector extends BlockRK implements ITileEntityProvider {

    public BlockCoolantInjector() {
        super(Material.iron, Reference.BLOCK_COOLANT_INJECTOR);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileCoolantInjector();
    }

}
