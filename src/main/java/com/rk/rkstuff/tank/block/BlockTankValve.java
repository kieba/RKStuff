package com.rk.rkstuff.tank.block;

import com.rk.rkstuff.core.block.BlockRK;
import com.rk.rkstuff.tank.tile.TileTankValve;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockTankValve extends BlockRK implements ITankBlock, ITileEntityProvider {
    private IIcon[] icons = new IIcon[2];

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
        icons[0] = iconRegister.registerIcon(Reference.MOD_ID + ":tank/" + Reference.BLOCK_TANK_VALVE + 1);
        icons[1] = iconRegister.registerIcon(Reference.MOD_ID + ":tank/" + Reference.BLOCK_TANK_VALVE + 2);
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileTankValve) {
            TileTankValve valveTile = (TileTankValve) tile;
            if (valveTile.isOutput()) {
                return icons[1];
            } else {
                return icons[0];
            }
        }
        return getIcon(side, 0);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return icons[0];
    }

    @Override
    public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
        int offsetX = x - tileX;
        int offsetY = y - tileY;
        int offsetZ = z - tileZ;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (dir.offsetX == offsetX && dir.offsetY == offsetY && dir.offsetZ == offsetZ) {
                TileEntity tileEntity = world.getTileEntity(x, y, z);
                if (tileEntity instanceof TileTankValve) {
                    ((TileTankValve) tileEntity).onNeighborTileChange(dir.getOpposite());
                    break;
                }
            }
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileTankValve) ((TileTankValve) te).onBlockPlaced();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileTankValve) {
                ((TileTankValve) tile).toggleOutput();
                return true;
            }
        }
        return true;
    }

}
