package com.rk.rkstuff.tank.block;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.core.block.BlockRK;
import com.rk.rkstuff.tank.tile.TileTankInteraction;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTankInteraction extends BlockRK implements ITankBlock, ITileEntityProvider {

    public BlockTankInteraction() {
        super(Material.iron, Reference.BLOCK_TANK_INTERACTION);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileTankInteraction();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
        if (world.getTileEntity(x, y, z) instanceof TileTankInteraction) {
            player.openGui(RkStuff.INSTANCE, Reference.GUI_ID_TANK_INTERACTION, world, x, y, z);
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(Reference.MOD_ID + ":tank/" + Reference.BLOCK_TANK_INTERACTION);
    }

    @Override
    public int getRenderBlockPass() {
        return 0;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public TileEntity getMasterTileEntity(IBlockAccess access, int x, int y, int z, int meta) {
        if (meta == 0) return null;
        return ((TileTankInteraction) access.getTileEntity(x, y, z)).getMaster();
    }
}
