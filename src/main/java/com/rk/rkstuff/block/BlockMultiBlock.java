package com.rk.rkstuff.block;

import com.rk.rkstuff.tile.TileMultiBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockMultiBlock extends BlockRK implements ITileEntityProvider {

    private IIcon[] icons = new IIcon[2];

    protected BlockMultiBlock(Material material) {
        super(material);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons[0] = iconRegister.registerIcon(getUnwrappedUnlocalizedName(this.getUnlocalizedName()));
        icons[1] = iconRegister.registerIcon(getUnwrappedUnlocalizedName(this.getUnlocalizedName()) + "2");
        blockIcon = icons[0];
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile != null && tile instanceof TileMultiBlock) {
            if(((TileMultiBlock) tile).hasMaster()) {
                return icons[1];
            }
        }
        return icons[0];
    }
}
