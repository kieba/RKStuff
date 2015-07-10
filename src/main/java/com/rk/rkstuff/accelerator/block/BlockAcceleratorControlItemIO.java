package com.rk.rkstuff.accelerator.block;

import com.rk.rkstuff.accelerator.tile.TileAcceleratorCaseFluidIO;
import com.rk.rkstuff.accelerator.tile.TileAcceleratorControlItemIO;
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

public class BlockAcceleratorControlItemIO extends BlockAcceleratorControlCase implements ITileEntityProvider {

    private IIcon[] icons = new IIcon[2];

    public BlockAcceleratorControlItemIO() {
        super(Material.iron, Reference.BLOCK_ACCELERATOR_CONTROL_ITEM_IO);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileAcceleratorControlItemIO();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons[0] = iconRegister.registerIcon(Reference.MOD_ID + ":accelerator/" + Reference.BLOCK_ACCELERATOR_CONTROL_ITEM_IO + "Input");
        icons[1] = iconRegister.registerIcon(Reference.MOD_ID + ":accelerator/" + Reference.BLOCK_ACCELERATOR_CONTROL_ITEM_IO + "Output");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!world.isRemote && tile instanceof TileAcceleratorCaseFluidIO) {
            ((TileAcceleratorCaseFluidIO) tile).toggleIOMode();
        }
        return true;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta == 0) return icons[0];
        return icons[1];
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        return icons[((TileAcceleratorControlItemIO) world.getTileEntity(x, y, z)).isOutput() ? 0 : 1];
    }
}
