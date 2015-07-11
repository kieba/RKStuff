package com.rk.rkstuff.accelerator.block;

import com.rk.rkstuff.accelerator.tile.TileAcceleratorCaseFluidIO;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAcceleratorCaseFluidIO extends BlockAcceleratorCase implements ITileEntityProvider {

    private IIcon[] icons = new IIcon[2];

    public BlockAcceleratorCaseFluidIO() {
        super(Material.iron, Reference.BLOCK_ACCELERATOR_CASE_FLUID_IO);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileAcceleratorCaseFluidIO();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons[0] = iconRegister.registerIcon(Reference.MOD_ID + ":accelerator/" + Reference.BLOCK_ACCELERATOR_CASE_FLUID_IO + "Input");
        icons[1] = iconRegister.registerIcon(Reference.MOD_ID + ":accelerator/" + Reference.BLOCK_ACCELERATOR_CASE_FLUID_IO + "Output");
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return icons[0];
    }

    @Override
    public boolean onWrench(World world, int x, int y, int z, int side, EntityPlayer player) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!world.isRemote && tile instanceof TileAcceleratorCaseFluidIO) {
            ((TileAcceleratorCaseFluidIO) tile).toggleIOMode();
        }
        return true;
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        return icons[((TileAcceleratorCaseFluidIO) world.getTileEntity(x, y, z)).isOutput() ? 1 : 0];
    }
}
