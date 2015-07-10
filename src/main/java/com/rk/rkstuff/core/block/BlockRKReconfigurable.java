package com.rk.rkstuff.core.block;

import com.rk.rkstuff.core.tile.TileRKReconfigurable;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockRKReconfigurable extends BlockRK implements ITileEntityProvider {
    protected IIcon[] icons;

    public BlockRKReconfigurable(Material material, String blockName) {
        super(material, blockName);
    }

    @Override
    public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side) {
        TileRKReconfigurable tile = (TileRKReconfigurable) access.getTileEntity(x, y, z);
        return tile.getTexture(side, 0);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return icons[0];
    }

    public IIcon getIconForGui(int side, int config) {
        return icons[config];
    }

    @Override
    public boolean onWrench(World world, int x, int y, int z, int side, EntityPlayer player) {
        TileRKReconfigurable tile = (TileRKReconfigurable) world.getTileEntity(x, y, z);
        tile.incrSide(side);
        return true;
    }

    @Override
    public boolean canBeWrenched(World world, int x, int y, int z, int side, EntityPlayer player) {
        return false;
    }
}
