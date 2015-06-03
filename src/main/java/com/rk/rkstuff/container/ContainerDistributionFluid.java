package com.rk.rkstuff.container;

import com.rk.rkstuff.tile.TileDistributionFluid;
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
