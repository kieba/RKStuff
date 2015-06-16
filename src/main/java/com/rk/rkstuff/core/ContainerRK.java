package com.rk.rkstuff.core;


import com.rk.rkstuff.core.tile.TileRK;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class ContainerRK<T extends TileRK> extends Container {

    protected T tile;

    public ContainerRK(EntityPlayer player, T tile) {
        this.tile = tile;
        if(player instanceof EntityPlayerMP) {
            tile.registerPlayerGui((EntityPlayerMP) player);
        }
    }

    protected void setupPlayerInventory(EntityPlayer player) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        if(player instanceof EntityPlayerMP) {
            tile.unregisterPlayerGui((EntityPlayerMP) player);
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIdx)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(slotIdx);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (slotIdx >= 0 && slotIdx < 27) {
                if (!this.mergeItemStack(itemstack1, 27, 36, false)) {
                    return null;
                }
            } else if (slotIdx >= 27 && slotIdx < 36 && !this.mergeItemStack(itemstack1, 0, 27, false)) {
                return null;
            }

            if (itemstack1.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(player, itemstack1);
        }

        return itemstack;
    }
}
