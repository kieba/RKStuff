package com.rk.rkstuff.core.block;

import cofh.api.tileentity.IReconfigurableFacing;
import cofh.lib.util.helpers.MathHelper;
import com.rk.rkstuff.core.tile.TileRK;
import com.rk.rkstuff.util.CreativeTabRKStuff;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockRK extends Block {

    public BlockRK(Material material, String blockName) {
        super(material);
        this.setCreativeTab(CreativeTabRKStuff.RK_STUFF_TAB);
        this.setHardness(0.5f);
        this.setBlockName(blockName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(Reference.MOD_ID + ":" + getUnwrappedUnlocalizedName(this.getUnlocalizedName()));
    }

    protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }

    @Override
    public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
        int offsetX = tileX - x;
        int offsetY = tileY - y;
        int offsetZ = tileZ - z;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (dir.offsetX == offsetX && dir.offsetY == offsetY && dir.offsetZ == offsetZ) {
                TileEntity te = world.getTileEntity(x, y, z);
                if (te instanceof TileRK) ((TileRK) te).onNeighborChange(dir);
                break;
            }
        }
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
