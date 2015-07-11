package com.rk.rkstuff.api;

import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class RecipeHelper {
    public static void addLHCRecipe(ItemStack result, float requiredSpeed, float mass, ItemStack... requirements) {
        if (result != null && requirements != null) {
            if (requirements.length > 5) {
                throw new IllegalArgumentException("Only 5 requirement items are allowed.");
            }
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setFloat("reqSpeed", requiredSpeed);
            nbt.setFloat("mass", mass);
            nbt.setTag("result", new NBTTagCompound());
            result.writeToNBT(nbt.getCompoundTag("result"));
            nbt.setInteger("req", requirements.length);
            for (int i = 0; i < requirements.length; i++) {
                nbt.setTag("req" + i, new NBTTagCompound());
                requirements[i].writeToNBT(nbt.getCompoundTag("req" + i));
            }
            FMLInterModComms.sendMessage("rkstuff", "addLHCRecipe", nbt);
        }
    }
}
