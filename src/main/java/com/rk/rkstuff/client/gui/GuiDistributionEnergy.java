package com.rk.rkstuff.client.gui;

import com.rk.rkstuff.distribution.ContainerDistributionEnergy;
import com.rk.rkstuff.distribution.tile.TileDistributionEnergy;
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
