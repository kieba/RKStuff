package com.rk.rkstuff.block;

import com.rk.rkstuff.tile.TileSolarInput;
import com.rk.rkstuff.tile.TileSolarOutput;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockSolarInput extends BlockSolar implements ISolarBlock, ITileEntityProvider{

    public BlockSolarInput() {
        setBlockName(Reference.BLOCK_SOLAR_INPUT);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        super.registerBlockIcons(iconRegister);
        icons[0] = iconRegister.registerIcon(Reference.MOD_ID + ":solar/" + Reference.BLOCK_SOLAR_INPUT);
        icons[1] = iconRegister.registerIcon(Reference.MOD_ID + ":solar/" + Reference.BLOCK_SOLAR_INPUT);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileSolarInput();
    }
}
