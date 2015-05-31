package com.rk.rkstuff.container;

import com.rk.rkstuff.tile.TileSolarMaster;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerSolar extends ContainerRK<TileSolarMaster> {

    public ContainerSolar(EntityPlayer player, TileSolarMaster tile) {
        super(player, tile);
        setupPlayerInventory(player);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

}
