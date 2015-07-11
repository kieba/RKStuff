package com.rk.rkstuff.coolant.block;

import cofh.lib.util.helpers.ServerHelper;
import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.coolant.tile.TilePoweredFreezer;
import com.rk.rkstuff.core.block.BlockRKReconfigurable;
import com.rk.rkstuff.util.Reference;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockPoweredFreezer extends BlockRKReconfigurable {
    public BlockPoweredFreezer() {
        super(Material.iron, Reference.BLOCK_POWERED_FREEZER);
        icons = new IIcon[3];
    }


    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons[0] = iconRegister.registerIcon(Reference.MOD_ID + ":coolant/" + Reference.BLOCK_POWERED_FREEZER + 1);
        icons[1] = iconRegister.registerIcon(Reference.MOD_ID + ":coolant/" + Reference.BLOCK_POWERED_FREEZER + 2);
        icons[2] = iconRegister.registerIcon(Reference.MOD_ID + ":coolant/" + Reference.BLOCK_POWERED_FREEZER + 3);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TilePoweredFreezer();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (!super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ)) {
            if (world.getTileEntity(x, y, z) instanceof TilePoweredFreezer && ServerHelper.isServerWorld(world)) {
                player.openGui(RkStuff.INSTANCE, Reference.GUI_ID_POWERED_FREEZER, world, x, y, z);
            }
        }
        return true;
    }
}
