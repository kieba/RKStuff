package com.rk.rkstuff.accelerator.block;

import com.rk.rkstuff.core.block.BlockRK;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;

public class BlockAcceleratorControlCore extends BlockRK implements IAcceleratorControlCoreBlock {

    public BlockAcceleratorControlCore() {
        super(Material.iron, Reference.BLOCK_ACCELERATOR_CONTROL_CORE);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(Reference.MOD_ID + ":accelerator/" + Reference.BLOCK_ACCELERATOR_CONTROL_CORE);
    }

}
