package com.rk.rkstuff.accelerator;

import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.Comparator;

public class LHCRecipe {
    private ItemStack[] requirements;
    private ItemStack result;

    public LHCRecipe(ItemStack result, ItemStack... requirements) {
        this.result = result;
        this.requirements = requirements;
        Arrays.sort(requirements, new Comparator<ItemStack>() {
            @Override
            public int compare(ItemStack o1, ItemStack o2) {
                return o1.getItem().getUnlocalizedName().compareTo(o2.getUnlocalizedName());
            }
        });
    }

    public ItemStack getResult() {
        return result;
    }

    public ItemStack[] getRequirements() {
        return requirements;
    }
}
