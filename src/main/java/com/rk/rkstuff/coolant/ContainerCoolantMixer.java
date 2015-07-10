package com.rk.rkstuff.coolant;

import com.rk.rkstuff.coolant.tile.TileCoolantMixer;
import com.rk.rkstuff.core.ContainerRK;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerCoolantMixer extends ContainerRK<TileCoolantMixer> {

    public ContainerCoolantMixer(EntityPlayer player, TileCoolantMixer tile) {
        super(player, tile);
        setupPlayerInventory(player);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }
}
