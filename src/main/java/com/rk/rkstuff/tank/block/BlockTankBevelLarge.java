package com.rk.rkstuff.tank.block;

import com.rk.rkstuff.core.block.IBlockBevelLarge;
import com.rk.rkstuff.proxy.ClientProxy;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockTankBevelLarge extends BlockTank implements IBlockBevelLarge {
    public BlockTankBevelLarge() {
        super(Material.iron, Reference.BLOCK_TANK_BEVEL_LARGE);
    }


    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isBlockNormalCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return ClientProxy.blockBevelRenderId;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(Reference.MOD_ID + ":tank/" + getUnwrappedUnlocalizedName(this.getUnlocalizedName()));
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return blockIcon;
    }

    @Override
    public int getRenderBlockPass() {
        return 0;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    protected boolean isBuild(int meta) {
        return true;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return false;
    }
}
