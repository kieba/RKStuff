package com.rk.rkstuff.block.fusion;

import com.rk.rkstuff.tile.fusion.TileFusionCaseFluidIO;
import com.rk.rkstuff.util.Reference;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockFusionCaseFluidIO extends BlockFusionCase implements ITileEntityProvider {

    public BlockFusionCaseFluidIO() {
        super(Material.iron, Reference.BLOCK_FUSION_CASE_FLUID_IO);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileFusionCaseFluidIO();
    }

}
