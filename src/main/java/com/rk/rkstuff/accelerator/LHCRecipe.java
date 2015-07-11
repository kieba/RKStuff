package com.rk.rkstuff.accelerator;

import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.Comparator;

public class LHCRecipe {
    private ItemStack[] requirements;
    private ItemStack result;
    private float requiredSpeed;
    private float mass;

    public LHCRecipe(ItemStack result, float reqSpeed, ItemStack... requirements) {
        this.result = result;
        this.requirements = requirements;
        this.requiredSpeed = reqSpeed;
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

    public float getRequiredSpeed() {
        return requiredSpeed;
    }

    public float getMass() {
        return mass;
    }

    public ItemStack[] getRequirements() {
        return requirements;
    }
}
