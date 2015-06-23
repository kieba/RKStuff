package com.rk.rkstuff.coolant.block;

import com.rk.rkstuff.coolant.tile.TileCoolantMixer;
import com.rk.rkstuff.core.block.BlockRKReconfigurable;
import com.rk.rkstuff.util.Reference;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockCoolantMixer extends BlockRKReconfigurable {
    public BlockCoolantMixer() {
        super(Material.iron, Reference.BLOCK_COOLANT_MIXER);
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileCoolantMixer(this);
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons = new IIcon[4];
        icons[0] = iconRegister.registerIcon(Reference.MOD_ID + ":coolant/" + Reference.BLOCK_COOLANT_MIXER + 1);
        icons[1] = iconRegister.registerIcon(Reference.MOD_ID + ":coolant/" + Reference.BLOCK_COOLANT_MIXER + 2);
        icons[2] = iconRegister.registerIcon(Reference.MOD_ID + ":coolant/" + Reference.BLOCK_COOLANT_MIXER + 3);
        icons[3] = iconRegister.registerIcon(Reference.MOD_ID + ":coolant/" + Reference.BLOCK_COOLANT_MIXER + 4);
    }
}
