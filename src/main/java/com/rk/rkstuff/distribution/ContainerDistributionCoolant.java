package com.rk.rkstuff.distribution;

import com.rk.rkstuff.core.ContainerRK;
import com.rk.rkstuff.distribution.tile.TileDistributionCoolant;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerDistributionCoolant extends ContainerRK<TileDistributionCoolant> {

    public ContainerDistributionCoolant(EntityPlayer player, TileDistributionCoolant tile) {
        super(player, tile);
        setupPlayerInventory(player);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

}
