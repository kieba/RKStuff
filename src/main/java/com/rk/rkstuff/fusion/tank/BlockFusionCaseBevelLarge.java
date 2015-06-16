package com.rk.rkstuff.fusion.tank;

import com.rk.rkstuff.core.block.IBlockBevelLarge;
import com.rk.rkstuff.proxy.ClientProxy;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockFusionCaseBevelLarge extends BlockFusionCase implements IBlockBevelLarge {

    public BlockFusionCaseBevelLarge() {
        super(Material.iron, Reference.BLOCK_FUSION_CASE_BEVEL_LARGE);
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
            meta = (meta + 1) % 12;
            world.setBlockMetadataWithNotify(x, y, z, meta, 3);
        }
        return true;
    }

}