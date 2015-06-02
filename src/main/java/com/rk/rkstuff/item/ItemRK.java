package com.rk.rkstuff.item;

import com.rk.rkstuff.util.CreativeTabRKStuff;
import com.rk.rkstuff.util.Reference;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemRK extends Item {
    private IIcon icon;

    public ItemRK(String name) {
        setUnlocalizedName(name);
        setCreativeTab(CreativeTabRKStuff.RK_STUFF_TAB);
    }

    @Override
    public void registerIcons(IIconRegister register) {
        super.registerIcons(register);
        icon = register.registerIcon(Reference.MOD_ID + ":" + getUnwrappedUnlocalizedName(getUnlocalizedName()));
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        return icon;
    }

    @Override
    public IIcon getIconIndex(ItemStack stack) {
        return icon;
    }

    protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }
}
