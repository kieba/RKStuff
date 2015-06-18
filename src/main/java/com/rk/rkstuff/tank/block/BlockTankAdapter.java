package com.rk.rkstuff.tank.block;

import com.rk.rkstuff.core.block.BlockRK;
import com.rk.rkstuff.tank.tile.TileTankAdapter;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockTankAdapter extends BlockRK implements ITileEntityProvider, ITankBlock {
    private IIcon[] icons = new IIcon[2];

    public BlockTankAdapter() {
        super(Material.iron, Reference.BLOCK_TANK_ADAPTER);
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
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileTankAdapter();
    }

    @Override
    public boolean isBlockNormalCube() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons[0] = iconRegister.registerIcon(Reference.MOD_ID + ":tank/" + Reference.BLOCK_TANK_ADAPTER + 1);
        icons[1] = iconRegister.registerIcon(Reference.MOD_ID + ":tank/" + Reference.BLOCK_TANK_ADAPTER + 2);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta == 0) return icons[0];
        return icons[1];
    }

}
