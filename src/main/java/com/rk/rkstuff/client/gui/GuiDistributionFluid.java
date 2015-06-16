package com.rk.rkstuff.client.gui;

import com.rk.rkstuff.distribution.ContainerDistributionFluid;
import com.rk.rkstuff.distribution.tile.TileDistributionFluid;
import net.minecraft.entity.player.EntityPlayer;

public class GuiDistributionFluid extends GuiDistribution {

    public GuiDistributionFluid(EntityPlayer player, TileDistributionFluid tile) {
        super(new ContainerDistributionFluid(player, tile), tile);
    }

    @Override
    protected String getAvgOutputString(float avgOutput) {
        return String.format("Output: %.2f mB/t", avgOutput);
    }
}
