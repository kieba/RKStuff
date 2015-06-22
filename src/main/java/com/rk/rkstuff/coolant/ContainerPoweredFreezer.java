package com.rk.rkstuff.coolant;

import com.rk.rkstuff.coolant.tile.TilePoweredFreezer;
import com.rk.rkstuff.core.ContainerRK;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerPoweredFreezer extends ContainerRK<TilePoweredFreezer> {

    public ContainerPoweredFreezer(EntityPlayer player, TilePoweredFreezer tile) {
        super(player, tile);
        setupPlayerInventory(player);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

}