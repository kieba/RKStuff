package com.rk.rkstuff.solar;

import com.rk.rkstuff.core.ContainerRK;
import com.rk.rkstuff.solar.tile.TileSolarMaster;
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
