package com.rk.rkstuff.block;

import com.rk.rkstuff.tile.TileBoilerBaseOutput;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockBoilerBaseOutput extends BlockRK implements ITileEntityProvider, IBoilerBaseBlock {

    private IIcon[] icons = new IIcon[14];

    public BlockBoilerBaseOutput() {
        super(Material.iron);
        setBlockName(Reference.BLOCK_BOILER_BASE_OUTPUT);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        for (int i = 0; i < icons.length; i++) {
            icons[i] = iconRegister.registerIcon(Reference.MOD_ID + ":" + getUnwrappedUnlocalizedName(this.getUnlocalizedName()) + (i+1));
        }
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        //TODO: map icons to side
        return Blocks.sand.getIcon(side, meta);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileBoilerBaseOutput();
    }
}
