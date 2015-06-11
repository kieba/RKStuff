package com.rk.rkstuff.block.fusion;

import com.rk.rkstuff.block.BlockRK;
import com.rk.rkstuff.tile.fusion.TileFusionControlItemIO;
import com.rk.rkstuff.util.Reference;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockFusionControlItemIO extends BlockRK implements ITileEntityProvider, IFusionControlCaseBlock {

    public BlockFusionControlItemIO() {
        super(Material.iron, Reference.BLOCK_FUSION_CONTROL_ITEM_IO);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileFusionControlItemIO();
    }
}
