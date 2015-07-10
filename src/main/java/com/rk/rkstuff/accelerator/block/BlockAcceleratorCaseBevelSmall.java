package com.rk.rkstuff.accelerator.block;

import com.rk.rkstuff.core.block.IBlockBevelSmall;
import com.rk.rkstuff.proxy.ClientProxy;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class BlockAcceleratorCaseBevelSmall extends BlockAcceleratorCase implements IBlockBevelSmall {

    public BlockAcceleratorCaseBevelSmall() {
        super(Material.iron, Reference.BLOCK_ACCELERATOR_CASE_BEVEL_SMALL);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isBlockNormalCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return ClientProxy.blockBevelRenderId;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(Reference.MOD_ID + ":accelerator/" + getUnwrappedUnlocalizedName(this.getUnlocalizedName()));
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return blockIcon;
    }


}
