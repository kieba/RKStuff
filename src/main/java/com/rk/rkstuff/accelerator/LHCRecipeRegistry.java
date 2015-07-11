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

    public static void addRecipe(ItemStack result, float reqSpeed, float mass, ItemStack... requirements) {
        addRecipe(new LHCRecipe(result, reqSpeed, mass, requirements));
    }

    public static void addRecipe(NBTTagCompound data) {
        float reqSpeed = data.getFloat("reqSpeed");
        float mass = data.getFloat("mass");
        ItemStack result = new ItemStack(Blocks.air);
        result.readFromNBT(data.getCompoundTag("result"));

        ItemStack[] requirements = new ItemStack[data.getInteger("req")];
        for (int i = 0; i < requirements.length; i++) {
            requirements[i].readFromNBT(data.getCompoundTag("req" + i));
        }
        addRecipe(result, reqSpeed, mass, requirements);
    }

    public static LHCRecipe getRecipeExact(ItemStack result, float requiredSpeed, ItemStack... requirements) {
        for (LHCRecipe recipe : recipes) {
            if (!ItemStack.areItemStacksEqual(recipe.getResult(), result)) continue;
            if (requirements == null) continue;
            if (recipe.getRequirements().length != requirements.length) continue;
            if (recipe.getRequiredSpeed() != requiredSpeed) continue;
            for (int i = 0; i < recipe.getRequirements().length; i++) {
                if (!ItemStack.areItemStacksEqual(recipe.getRequirements()[i], requirements[i])) continue;
            }
            return recipe;
        }
        return null;
    }

    public static LHCRecipe getRecipeCrafting(float speed, ItemStack[] currentResources, int from, int to) {
        recipe:
        for (LHCRecipe recipe : recipes) {
            for (ItemStack reqIS : recipe.getRequirements()) {
                for (int i = from; i <= to; i++) {
                    if (reqIS.getItem() != currentResources[i].getItem() || reqIS.stackSize > currentResources[i].stackSize) {
                        continue recipe;
                    }
                }
            }
            if (recipe.getRequiredSpeed() > speed) continue recipe;
            return recipe;
        }
        return null;
    }

}
