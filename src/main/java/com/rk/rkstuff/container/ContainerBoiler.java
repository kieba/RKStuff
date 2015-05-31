package com.rk.rkstuff.container;


import com.rk.rkstuff.tile.TileBoilerBaseMaster;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerBoiler extends ContainerRK<TileBoilerBaseMaster> {

    public ContainerBoiler(EntityPlayer player, TileBoilerBaseMaster tile) {
        super(player, tile);
        setupPlayerInventory(player);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

}
