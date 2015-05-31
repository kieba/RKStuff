package com.rk.rkstuff.container;

import com.rk.rkstuff.tile.TileEnergyDistribution;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerEnergyDistribution extends ContainerRK<TileEnergyDistribution> {

    public ContainerEnergyDistribution(EntityPlayer player, TileEnergyDistribution tile) {
        super(player, tile);
        setupPlayerInventory(player);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

}
