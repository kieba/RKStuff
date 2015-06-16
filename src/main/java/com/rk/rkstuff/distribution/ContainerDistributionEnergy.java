package com.rk.rkstuff.distribution;

import com.rk.rkstuff.core.ContainerRK;
import com.rk.rkstuff.distribution.tile.TileDistributionEnergy;
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
