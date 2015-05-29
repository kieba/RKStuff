package com.rk.rkstuff.block;

import com.rk.rkstuff.RkStuff;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class BlockSolarOutput extends BlockRK implements ISolarBlock{
    private IIcon[] icons = new IIcon[18];

    public BlockSolarOutput() {
        super(Material.iron);
        setBlockName(Reference.BLOCK_SOLAR_OUTPUT);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons[0] = iconRegister.registerIcon(Reference.MOD_ID + ":solar/" + Reference.BLOCK_SOLAR + 1);
        icons[1] = iconRegister.registerIcon(Reference.MOD_ID + ":solar/" + getUnwrappedUnlocalizedName(this.getUnlocalizedName()));
        for (int i = 2; i < 18; i++) {
            icons[i] = iconRegister.registerIcon(Reference.MOD_ID + ":solar/" + Reference.BLOCK_SOLAR + (i+1));
        }
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata){
        return new TileSolarOutput();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if(ForgeDirection.UP.ordinal() == side){
            return icons[2+meta];
        }else if(ForgeDirection.DOWN.ordinal() == side){
            return icons[0];
        }else{
            return icons[1];
        }
    }
}
