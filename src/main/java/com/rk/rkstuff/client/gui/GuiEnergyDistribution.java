package com.rk.rkstuff.client.gui;

import com.rk.rkstuff.container.ContainerEnergyDistribution;
import com.rk.rkstuff.tile.TileEnergyDistribution;
import net.minecraft.entity.player.EntityPlayer;

public class GuiEnergyDistribution extends GuiDistribution {

    public GuiEnergyDistribution(EntityPlayer player, TileEnergyDistribution tile) {
        super(new ContainerEnergyDistribution(player, tile), tile);
    }

    @Override
    protected String getAvgOutputString(float avgOutput) {
        return String.format("Output: %.2f RF/t", avgOutput);
    }
}
