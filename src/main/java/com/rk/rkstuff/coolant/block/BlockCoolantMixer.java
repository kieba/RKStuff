package com.rk.rkstuff.coolant.block;

import cofh.lib.util.helpers.ServerHelper;
import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.coolant.tile.TileCoolantMixer;
import com.rk.rkstuff.core.block.BlockRKReconfigurable;
import com.rk.rkstuff.util.Reference;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockCoolantMixer extends BlockRKReconfigurable {
    public BlockCoolantMixer() {
        super(Material.iron, Reference.BLOCK_COOLANT_MIXER);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileCoolantMixer();
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons = new IIcon[5];
        icons[0] = iconRegister.registerIcon(Reference.MOD_ID + ":coolant/" + Reference.BLOCK_COOLANT_MIXER + "Disabled");
        icons[1] = iconRegister.registerIcon(Reference.MOD_ID + ":coolant/" + Reference.BLOCK_COOLANT_MIXER + "Input1");
        icons[2] = iconRegister.registerIcon(Reference.MOD_ID + ":coolant/" + Reference.BLOCK_COOLANT_MIXER + "Input2");
        icons[3] = iconRegister.registerIcon(Reference.MOD_ID + ":coolant/" + Reference.BLOCK_COOLANT_MIXER + "Output");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (!super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ)) {
            if (world.getTileEntity(x, y, z) instanceof TileCoolantMixer && ServerHelper.isServerWorld(world)) {
                player.openGui(RkStuff.INSTANCE, Reference.GUI_ID_COOLANT_MIXER, world, x, y, z);
            }
        }
        return true;
    }
}
