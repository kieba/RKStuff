package com.rk.rkstuff.accelerator;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.Comparator;

public class LHCRecipeRegistry {
    private static ArrayList<LHCRecipe> recipes = new ArrayList<LHCRecipe>();

    public static void addRecipe(LHCRecipe recipe) {
        recipes.add(recipe);
        recipes.sort(new Comparator<LHCRecipe>() {
            @Override
            public int compare(LHCRecipe o1, LHCRecipe o2) {
                return o1.getRequirements().length - o2.getRequirements().length;
            }
        });
    }

    public static void addRecipe(ItemStack result, ItemStack... requirements) {
        addRecipe(new LHCRecipe(result, requirements));
    }

    public static void addRecipe(NBTTagCompound data) {
        ItemStack result = new ItemStack(Blocks.air);
        result.readFromNBT(data.getCompoundTag("result"));

        ItemStack[] requirements = new ItemStack[data.getInteger("req")];
        for (int i = 0; i < requirements.length; i++) {
            requirements[i].readFromNBT(data.getCompoundTag("req" + i));
        }
        addRecipe(result, requirements);
    }

    public static LHCRecipe getRecipeExact(ItemStack result, ItemStack... requirements) {
        for (LHCRecipe recipe : recipes) {
            if (!ItemStack.areItemStacksEqual(recipe.getResult(), result)) continue;
            if (requirements == null) continue;
            if (recipe.getRequirements().length != requirements.length) continue;
            for (int i = 0; i < recipe.getRequirements().length; i++) {
                if (!ItemStack.areItemStacksEqual(recipe.getRequirements()[i], requirements[i])) continue;
            }
            return recipe;
        }
        return null;
    }

    public static LHCRecipe getRecipeCrafting(ItemStack[] currentResources) {
        recipe:
        for (LHCRecipe recipe : recipes) {
            for (ItemStack reqIS : recipe.getRequirements()) {
                for (ItemStack curIS : currentResources) {
                    if (reqIS.getItem() != curIS.getItem() || reqIS.stackSize > curIS.stackSize) {
                        continue recipe;
                    }
                }
            }
            return recipe;
        }
        return null;
    }

}
