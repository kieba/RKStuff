package com.rk.rkstuff.block;

import com.rk.rkstuff.tile.TileMultiBlock;
import com.rk.rkstuff.tile.TileMultiBlockTest;
import com.rk.rkstuff.util.Reference;
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

public class BlockMultiBlockTest extends BlockRK implements ITileEntityProvider {

    private IIcon[] icons = new IIcon[2];

    public BlockMultiBlockTest() {
        super(Material.iron);
        this.setBlockName(Reference.BLOCK_MULTI_BLOCK_TEST_NAME);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileMultiBlockTest();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons[0] = iconRegister.registerIcon(Reference.MOD_ID + ":" + getUnwrappedUnlocalizedName(this.getUnlocalizedName()));
        icons[1] = iconRegister.registerIcon(Reference.MOD_ID + ":" + getUnwrappedUnlocalizedName(this.getUnlocalizedName()) + "2");
        blockIcon = icons[0];
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        super.getIcon(world, x, y, z, side);
        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile != null && tile instanceof TileMultiBlock) {
            if(((TileMultiBlock) tile).hasMaster()) {
                return icons[1];
            }
        }
        return icons[0];
    }

}
