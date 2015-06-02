package com.rk.rkstuff.container;

import com.rk.rkstuff.tile.TileEnergyDistribution;
import com.rk.rkstuff.tile.TileFluidDistribution;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerFluidDistribution extends ContainerRK<TileFluidDistribution> {

    public ContainerFluidDistribution(EntityPlayer player, TileFluidDistribution tile) {
        super(player, tile);
        setupPlayerInventory(player);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

}
