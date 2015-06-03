package com.rk.rkstuff.client.gui;

import com.rk.rkstuff.container.ContainerDistributionEnergy;
import com.rk.rkstuff.tile.TileDistributionEnergy;
import net.minecraft.entity.player.EntityPlayer;

public class GuiDistributionEnergy extends GuiDistribution {

    public GuiDistributionEnergy(EntityPlayer player, TileDistributionEnergy tile) {
        super(new ContainerDistributionEnergy(player, tile), tile);
    }

    @Override
    protected String getAvgOutputString(float avgOutput) {
        return String.format("Output: %.2f RF/t", avgOutput);
    }
}
