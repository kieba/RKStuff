package com.rk.rkstuff.coolant.block;

import com.rk.rkstuff.coolant.tile.TileCoolantInjector;
import com.rk.rkstuff.core.block.BlockRK;
import com.rk.rkstuff.util.Reference;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockCoolantInjector extends BlockRK implements ITileEntityProvider {

    public BlockCoolantInjector() {
        super(Material.iron, Reference.BLOCK_COOLANT_INJECTOR);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileCoolantInjector();
    }

    @Override
    public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
        int offsetX = x - tileX;
        int offsetY = y - tileY;
        int offsetZ = z - tileZ;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (dir.offsetX == offsetX && dir.offsetY == offsetY && dir.offsetZ == offsetZ) {
                TileEntity tileEntity = world.getTileEntity(x, y, z);
                if (tileEntity instanceof TileCoolantInjector) {
                    ((TileCoolantInjector) tileEntity).onNeighborTileChange(dir.getOpposite());
                    break;
                }
            }
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileCoolantInjector) ((TileCoolantInjector) te).onBlockPlaced();
    }
}
