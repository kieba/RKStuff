package com.rk.rkstuff.accelerator.block;

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

    private IIcon[] icons = new IIcon[6];

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
        icons[0] = iconRegister.registerIcon(Reference.MOD_ID + ":accelerator/" + Reference.BLOCK_ACCELERATOR_CONTROL_ITEM_IO + "Output");
        icons[1] = iconRegister.registerIcon(Reference.MOD_ID + ":accelerator/" + Reference.BLOCK_ACCELERATOR_CONTROL_ITEM_IO + "Slot1");
        icons[2] = iconRegister.registerIcon(Reference.MOD_ID + ":accelerator/" + Reference.BLOCK_ACCELERATOR_CONTROL_ITEM_IO + "Slot2");
        icons[3] = iconRegister.registerIcon(Reference.MOD_ID + ":accelerator/" + Reference.BLOCK_ACCELERATOR_CONTROL_ITEM_IO + "Slot3");
        icons[4] = iconRegister.registerIcon(Reference.MOD_ID + ":accelerator/" + Reference.BLOCK_ACCELERATOR_CONTROL_ITEM_IO + "Slot4");
        icons[5] = iconRegister.registerIcon(Reference.MOD_ID + ":accelerator/" + Reference.BLOCK_ACCELERATOR_CONTROL_ITEM_IO + "Slot5");
    }

    @Override
    public boolean onWrench(World world, int x, int y, int z, int side, EntityPlayer player) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!world.isRemote && tile instanceof TileAcceleratorControlItemIO) {
            ((TileAcceleratorControlItemIO) tile).incrementSlot();
        }
        return true;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return icons[0];
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        return icons[((TileAcceleratorControlItemIO) world.getTileEntity(x, y, z)).getSlot()];
    }
}
