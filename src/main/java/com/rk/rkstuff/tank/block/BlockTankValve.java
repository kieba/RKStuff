package com.rk.rkstuff.tank.block;

import com.rk.rkstuff.boiler.tile.TileBoilerBaseOutput;
import com.rk.rkstuff.core.block.BlockRK;
import com.rk.rkstuff.tank.tile.TileTankValve;
import com.rk.rkstuff.util.Reference;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockTankValve extends BlockRK implements ITankBlock, ITileEntityProvider {
    public BlockTankValve() {
        super(Material.iron, Reference.BLOCK_TANK_VALVE);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileTankValve();
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
            if (tile instanceof TileBoilerBaseOutput) {
                ((TileBoilerBaseOutput) tile).toggleOutput();
                return true;
            }
        }
        return true;
    }

}
