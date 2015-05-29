package com.rk.rkstuff.block;

import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;


public class BlockSolar extends BlockRK implements ISolarBlock{
    private IIcon[] icons = new IIcon[18];

    public BlockSolar() {
        super(Material.iron);
        setBlockName(Reference.BLOCK_SOLAR);

    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        for (int i = 0; i < 18; i++) {
            icons[i] = iconRegister.registerIcon(Reference.MOD_ID + ":solar/" + getUnwrappedUnlocalizedName(this.getUnlocalizedName()) + (i+1));
        }

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
