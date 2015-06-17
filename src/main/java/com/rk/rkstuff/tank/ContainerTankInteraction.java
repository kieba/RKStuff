package com.rk.rkstuff.tank;

import com.rk.rkstuff.core.ContainerRK;
import com.rk.rkstuff.tank.tile.TileTankInteraction;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerTankInteraction extends ContainerRK<TileTankInteraction> {
    public ContainerTankInteraction(EntityPlayer player, TileTankInteraction tile) {
        super(player, tile);
        setupPlayerInventory(player);
    }

    @Override
    public boolean canInteractWith(EntityPlayer p_75145_1_) {
        return true;
    }
}
