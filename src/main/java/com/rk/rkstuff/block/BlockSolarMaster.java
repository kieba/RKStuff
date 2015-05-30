package com.rk.rkstuff.block;

import com.rk.rkstuff.tile.TileSolarInput;
import com.rk.rkstuff.tile.TileSolarMaster;
import com.rk.rkstuff.tile.TileSolarOutput;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockSolarMaster extends BlockSolar implements ISolarBlock{
    public BlockSolarMaster() {
        setBlockName(Reference.BLOCK_SOLAR_MASTER);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        super.registerBlockIcons(iconRegister);
        icons[1] = iconRegister.registerIcon(Reference.MOD_ID + ":solar/" + Reference.BLOCK_SOLAR_MASTER);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata){
        return new TileSolarMaster();
    }
}
