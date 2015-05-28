package com.rk.rkstuff.item;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BucketBase extends ItemBucket {
    private IIcon image;
    private String name;

    public BucketBase(Block block, String name) {
        super(block);
        setUnlocalizedName(name);
        setContainerItem(Items.bucket);
        this.name = name;
    }


    @Override
    public void registerIcons(IIconRegister register) {
        image = register.registerIcon(Reference.MOD_ID + ":bucket/" + name);
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        return image;
    }

    @Override
    public IIcon getIconIndex(ItemStack stack) {
        return image;
    }
}
