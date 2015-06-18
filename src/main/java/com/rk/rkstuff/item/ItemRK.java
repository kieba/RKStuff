package com.rk.rkstuff.item;

import com.rk.rkstuff.util.CreativeTabRKStuff;
import com.rk.rkstuff.util.Reference;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;

public class ItemRK extends Item {
    public ItemRK(String name) {
        setUnlocalizedName(name);
        setCreativeTab(CreativeTabRKStuff.RK_STUFF_TAB);
    }

    @Override
    public void registerIcons(IIconRegister register) {
        itemIcon = register.registerIcon(Reference.MOD_ID + ":" + getUnwrappedUnlocalizedName(getUnlocalizedName()));
    }

    protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }
}
