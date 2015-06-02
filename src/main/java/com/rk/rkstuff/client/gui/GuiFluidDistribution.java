package com.rk.rkstuff.client.gui;

import com.rk.rkstuff.container.ContainerEnergyDistribution;
import com.rk.rkstuff.container.ContainerFluidDistribution;
import com.rk.rkstuff.tile.TileEnergyDistribution;
import com.rk.rkstuff.tile.TileFluidDistribution;
import net.minecraft.entity.player.EntityPlayer;

public class GuiFluidDistribution extends GuiDistribution {

    public GuiFluidDistribution(EntityPlayer player, TileFluidDistribution tile) {
        super(new ContainerFluidDistribution(player, tile), tile);
    }

    @Override
    protected String getAvgOutputString(float avgOutput) {
        return String.format("Output: %.2f mB/t", avgOutput);
    }
}
