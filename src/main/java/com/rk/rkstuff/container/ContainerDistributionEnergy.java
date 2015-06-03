package com.rk.rkstuff.container;

import com.rk.rkstuff.tile.TileDistributionEnergy;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerDistributionEnergy extends ContainerRK<TileDistributionEnergy> {

    public ContainerDistributionEnergy(EntityPlayer player, TileDistributionEnergy tile) {
        super(player, tile);
        setupPlayerInventory(player);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

}
