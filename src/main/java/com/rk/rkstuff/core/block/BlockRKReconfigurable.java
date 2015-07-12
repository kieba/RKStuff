package com.rk.rkstuff.core.block;

import cofh.api.tileentity.IReconfigurableFacing;
import cofh.lib.util.helpers.MathHelper;
import com.rk.rkstuff.core.tile.TileRKReconfigurable;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
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
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack item) {
        TileEntity tile = world.getTileEntity(x, y, z);

        if (tile instanceof IReconfigurableFacing) {
            IReconfigurableFacing reconfig = (IReconfigurableFacing) tile;
            int quadrant = MathHelper.floor(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

            if (reconfig.allowYAxisFacing()) {
                quadrant = entity.rotationPitch > 60 ? 4 : entity.rotationPitch < -60 ? 5 : quadrant;
            }
            switch (quadrant) {
                case 0:
                    reconfig.setFacing(2);
                    break;
                case 1:
                    reconfig.setFacing(5);
                    break;
                case 2:
                    reconfig.setFacing(3);
                    break;
                case 3:
                    reconfig.setFacing(4);
                    break;
                case 4:
                    reconfig.setFacing(1);
                    break;
                case 5:
                    reconfig.setFacing(0);
                    break;
            }
        }
    }
}
