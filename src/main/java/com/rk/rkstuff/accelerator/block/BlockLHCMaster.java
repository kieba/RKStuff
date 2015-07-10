package com.rk.rkstuff.accelerator.block;

import com.rk.rkstuff.accelerator.tile.TileAcceleratorMaster;
import com.rk.rkstuff.accelerator.tile.TileLHCMaster;
import com.rk.rkstuff.core.block.BlockRK;
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

public class BlockLHCMaster extends BlockRK implements ITileEntityProvider, IAcceleratorControlCaseBlock {

    private IIcon[] icons = new IIcon[2];

    public BlockLHCMaster() {
        super(Material.iron, Reference.BLOCK_LHC_MASTER);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileLHCMaster();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons[0] = iconRegister.registerIcon(Reference.MOD_ID + ":accelerator/" + Reference.BLOCK_LHC_MASTER + "Normal");
        icons[1] = iconRegister.registerIcon(Reference.MOD_ID + ":accelerator/" + Reference.BLOCK_LHC_MASTER + "Build");
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta == 0) return icons[0];
        return icons[1];
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileAcceleratorMaster tile = (TileAcceleratorMaster) world.getTileEntity(x, y, z);
            tile.onBlockActivated(player.isSneaking());
        }
        return true;
    }
}
