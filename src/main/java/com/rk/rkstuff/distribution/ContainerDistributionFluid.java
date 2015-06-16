package com.rk.rkstuff.distribution;

import com.rk.rkstuff.core.ContainerRK;
import com.rk.rkstuff.distribution.tile.TileDistributionFluid;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerDistributionFluid extends ContainerRK<TileDistributionFluid> {

    public ContainerDistributionFluid(EntityPlayer player, TileDistributionFluid tile) {
        super(player, tile);
        setupPlayerInventory(player);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

}
