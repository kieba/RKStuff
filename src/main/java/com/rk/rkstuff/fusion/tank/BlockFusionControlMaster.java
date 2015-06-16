package com.rk.rkstuff.fusion.tank;

import com.rk.rkstuff.core.block.BlockRK;
import com.rk.rkstuff.fusion.tile.TileFusionControlMaster;
import com.rk.rkstuff.util.Reference;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockFusionControlMaster extends BlockRK implements ITileEntityProvider, IFusionControlCaseBlock {

    public BlockFusionControlMaster() {
        super(Material.iron, Reference.BLOCK_FUSION_CONTROL_MASTER);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileFusionControlMaster();
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta == 0) return Blocks.emerald_ore.getIcon(0, 0);
        return Blocks.emerald_block.getIcon(0, 0);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileFusionControlMaster tile = (TileFusionControlMaster) world.getTileEntity(x, y, z);
            tile.onBlockActivated(player.isSneaking());
        }
        return true;
    }
}
