package com.rk.rkstuff.fusion.block;

import com.rk.rkstuff.core.block.IBlockBevelSmallInverted;
import com.rk.rkstuff.proxy.ClientProxy;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockFusionCaseBevelSmallInverted extends BlockFusionCase implements IBlockBevelSmallInverted {

    public BlockFusionCaseBevelSmallInverted() {
        super(Material.iron, Reference.BLOCK_FUSION_CASE_BEVEL_SMALL_INVERTED);
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
        blockIcon = iconRegister.registerIcon(Reference.MOD_ID + ":fusion/" + getUnwrappedUnlocalizedName(this.getUnlocalizedName()));
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return blockIcon;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            int meta = world.getBlockMetadata(x, y, z);
            meta = (meta + 1) % 8;
            world.setBlockMetadataWithNotify(x, y, z, meta, 3);
        }
        return true;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return false;
    }
}
