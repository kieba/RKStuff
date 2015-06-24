package com.rk.rkstuff.fusion.block;

import com.rk.rkstuff.fusion.tile.TileAcceleratorCaseFluidIO;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockAcceleratorCaseFluidIO extends BlockAcceleratorCase implements ITileEntityProvider {

    private IIcon[] icons = new IIcon[2];

    public BlockAcceleratorCaseFluidIO() {
        super(Material.iron, Reference.BLOCK_FUSION_CASE_FLUID_IO);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileAcceleratorCaseFluidIO();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons[0] = iconRegister.registerIcon(Reference.MOD_ID + ":fusion/" + Reference.BLOCK_FUSION_CASE_FLUID_IO + 1);
        icons[1] = iconRegister.registerIcon(Reference.MOD_ID + ":fusion/" + Reference.BLOCK_FUSION_CASE_FLUID_IO + 2);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta == 0) return icons[0];
        return icons[1];
    }
}
