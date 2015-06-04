package com.rk.rkstuff.item;

import com.rk.rkstuff.util.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import java.util.List;

public class ItemLinker extends ItemRK {

    public ItemLinker() {
        super(Reference.ITEM_LINKER_NAME);
        this.setNoRepair();
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote && player.isSneaking()) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag != null && tag.hasKey("uuidMSB")) {
                tag.removeTag("uuidMSB");
                tag.removeTag("uuidLSB");
                tag.removeTag("pos");
                tag.removeTag("dim");
                tag.removeTag("type");
                player.addChatMessage(new ChatComponentText("Cleared linker position!"));
            }
        }
        return stack;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.hasKey("uuidMSB")) {
            int[] pos = tag.getIntArray("pos");
            list.add("Teleporter linked to x: " + pos[0] + " y: " + pos[1] + " z: " + pos[2] + " dimension: " + tag.getString("dim"));
        }
    }
}
