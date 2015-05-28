package com.rk.rkstuff.util;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class CreativeTabRKStuff extends CreativeTabs {

    public static final CreativeTabs RK_STUFF_TAB = new CreativeTabRKStuff();

    private CreativeTabRKStuff() {
        super(Reference.MOD_ID);
    }

    @Override
    public Item getTabIconItem() {
        return Items.golden_apple;
    }

    @Override
    public String getTranslatedTabLabel() {
        return Reference.MOD_ID;
    }
}
