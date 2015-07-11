package com.rk.rkstuff.accelerator;

import com.rk.rkstuff.accelerator.tile.TileLHCMaster;
import com.rk.rkstuff.core.ContainerRK;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerLHCMaster extends ContainerRK<TileLHCMaster> {

    public ContainerLHCMaster(EntityPlayer player, TileLHCMaster tile) {
        super(player, tile);
        setupPlayerInventory(player);
        //Output
        this.addSlotToContainer(new Slot(tile.getInventory(), 0, 79, 29) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }
        });
        this.addSlotToContainer(new Slot(tile.getInventory(), 1, 79, 6));
        this.addSlotToContainer(new Slot(tile.getInventory(), 2, 103, 27));
        this.addSlotToContainer(new Slot(tile.getInventory(), 3, 92, 52));
        this.addSlotToContainer(new Slot(tile.getInventory(), 4, 67, 52));
        this.addSlotToContainer(new Slot(tile.getInventory(), 5, 55, 27));
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

}
