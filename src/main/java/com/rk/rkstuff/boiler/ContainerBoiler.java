package com.rk.rkstuff.boiler;


import com.rk.rkstuff.boiler.tile.TileBoilerBaseMaster;
import com.rk.rkstuff.core.ContainerRK;
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
