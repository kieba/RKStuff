package com.rk.rkstuff.fusion.block;

import com.rk.rkstuff.fusion.tile.TileFusionControlEnergyIO;
import com.rk.rkstuff.util.Reference;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockFusionControlEnergyIO extends BlockFusionControlCase implements ITileEntityProvider {

    public BlockFusionControlEnergyIO() {
        super(Material.iron, Reference.BLOCK_FUSION_CONTROL_ENERGY_IO);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileFusionControlEnergyIO();
    }
}
