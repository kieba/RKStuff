package com.rk.rkstuff.tank.block;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.core.block.BlockRK;
import com.rk.rkstuff.tank.tile.TileTankValve;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTankValve extends BlockRK implements ITankBlock, ITileEntityProvider {
    private IIcon[] icons = new IIcon[3];

    public BlockTankValve() {
        super(Material.iron, Reference.BLOCK_TANK_VALVE);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileTankValve();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons[0] = iconRegister.registerIcon(Reference.MOD_ID + ":tank/" + Reference.BLOCK_TANK_VALVE);
        icons[1] = iconRegister.registerIcon(Reference.MOD_ID + ":tank/" + Reference.BLOCK_TANK_VALVE + "Input");
        icons[2] = iconRegister.registerIcon(Reference.MOD_ID + ":tank/" + Reference.BLOCK_TANK_VALVE + "Output");
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return icons[0];
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileTankValve) {
            TileTankValve valveTile = (TileTankValve) tile;
            if (valveTile.getMaster() != null) {
                if (!valveTile.isOutput()) {
                    return icons[1];
                } else {
                    return icons[2];
                }
            }
        }
        return getIcon(side, 0);
    }

    @Override
    public boolean onWrench(World world, int x, int y, int z, int side, EntityPlayer player) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileTankValve) {
            ((TileTankValve) tile).toggleOutput();
        }
        return true;
    }

    @Override
    public TileEntity getMasterTileEntity(IBlockAccess access, int x, int y, int z, int meta) {
        if (meta == 0) return null;
        return ((BlockTank) RkStuff.blockTank).getMasterTileEntity(access, x, y, z, meta);
    }
}
