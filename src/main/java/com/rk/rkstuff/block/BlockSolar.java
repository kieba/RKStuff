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
        icons[0] = loadIconById(1, iconRegister);
        icons[1] = loadIconById(2, iconRegister);


        icons[2] = loadIconById(3, iconRegister);
        icons[3] = loadIconById(4, iconRegister);
        icons[4] = loadIconById(5, iconRegister);
        icons[5] = loadIconById(6, iconRegister);
        icons[6] = loadIconById(7, iconRegister);
        icons[7] = loadIconById(8, iconRegister);
        icons[8] = loadIconById(9, iconRegister);
        icons[9] = loadIconById(10, iconRegister);
        icons[10] = loadIconById(11, iconRegister);
        icons[11] = loadIconById(12, iconRegister);
        icons[12] = loadIconById(13, iconRegister);
        icons[13] = loadIconById(14, iconRegister);
        icons[14] = loadIconById(15, iconRegister);
        icons[15] = loadIconById(16, iconRegister);
        icons[16] = loadIconById(17, iconRegister);
        icons[17] = loadIconById(18, iconRegister);
    }

    private IIcon loadIconById(int id, IIconRegister iconRegister){
        return iconRegister.registerIcon(Reference.MOD_ID + ":solar/" + getUnwrappedUnlocalizedName(this.getUnlocalizedName()) + id);
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
