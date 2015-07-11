package com.rk.rkstuff.accelerator.block;

import com.rk.rkstuff.accelerator.AcceleratorControlCoreTypes;
import com.rk.rkstuff.core.block.BlockRK;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

public class BlockAcceleratorControlCore extends BlockRK implements IAcceleratorControlCoreBlock {

    private IIcon[] icons = new IIcon[AcceleratorControlCoreTypes.values().length];

    public BlockAcceleratorControlCore() {
        super(Material.iron, Reference.BLOCK_ACCELERATOR_CONTROL_CORE);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        for (AcceleratorControlCoreTypes type : AcceleratorControlCoreTypes.values()) {
            icons[type.ordinal()] = iconRegister.registerIcon(Reference.MOD_ID + ":accelerator/" + Reference.BLOCK_ACCELERATOR_CONTROL_CORE + type.name);
        }
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return icons[meta];
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (AcceleratorControlCoreTypes type : AcceleratorControlCoreTypes.values()) {
            list.add(new ItemStack(this, 1, type.ordinal()));
        }
    }


}
