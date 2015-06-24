package com.rk.rkstuff.fusion.block;

import com.rk.rkstuff.core.block.BlockRK;
import com.rk.rkstuff.fusion.tile.TileAcceleratorControlMaster;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockAcceleratorControlMaster extends BlockRK implements ITileEntityProvider, IAcceleratorControlCaseBlock {

    private IIcon[] icons = new IIcon[2];

    public BlockAcceleratorControlMaster() {
        super(Material.iron, Reference.BLOCK_FUSION_CONTROL_MASTER);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileAcceleratorControlMaster();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons[0] = iconRegister.registerIcon(Reference.MOD_ID + ":fusion/" + Reference.BLOCK_FUSION_CONTROL_MASTER + 1);
        icons[1] = iconRegister.registerIcon(Reference.MOD_ID + ":fusion/" + Reference.BLOCK_FUSION_CONTROL_MASTER + 2);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta == 0) return icons[0];
        return icons[1];
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileAcceleratorControlMaster tile = (TileAcceleratorControlMaster) world.getTileEntity(x, y, z);
            tile.onBlockActivated(player.isSneaking());
        }
        return true;
    }
}
