package com.rk.rkstuff.block;

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

public class BlockSolarMaster extends BlockRK implements ISolarBlock{
    private IIcon[] icons = new IIcon[18];

    public BlockSolarMaster() {
        super(Material.iron);
        setBlockName(Reference.BLOCK_SOLAR_MASTER);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons[0] = loadIconById(1, iconRegister);
        icons[1] = loadIconById(2, iconRegister);
        icons[2] = loadIconById(3, iconRegister);


        icons[3] = loadIconById(9, iconRegister);
        icons[4] = loadIconById(7, iconRegister);
        icons[5] = loadIconById(8, iconRegister);
        icons[6] = loadIconById(6, iconRegister);
        icons[7] = loadIconById(18, iconRegister);
        icons[8] = loadIconById(12, iconRegister);
        icons[9] = loadIconById(15, iconRegister);
        icons[10] = loadIconById(4, iconRegister);
        icons[11] = loadIconById(16, iconRegister);
        icons[12] = loadIconById(10, iconRegister);
        icons[13] = loadIconById(13, iconRegister);
        icons[14] = loadIconById(5, iconRegister);
        icons[15] = loadIconById(17, iconRegister);
        icons[16] = loadIconById(11, iconRegister);
        icons[17] = loadIconById(14, iconRegister);
    }

    private IIcon loadIconById(int id, IIconRegister iconRegister){
        return iconRegister.registerIcon(Reference.MOD_ID + ":solar/" + Reference.BLOCK_SOLAR + id);
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

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata){
        return new TileSolarMaster();
    }
}
