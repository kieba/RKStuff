package com.rk.rkstuff.client.gui;

import com.rk.rkstuff.coolant.CoolantStack;
import com.rk.rkstuff.distribution.ContainerDistributionCoolant;
import com.rk.rkstuff.distribution.tile.TileDistributionCoolant;
import net.minecraft.entity.player.EntityPlayer;

public class GuiDistributionCoolant extends GuiDistribution<TileDistributionCoolant> {

    public GuiDistributionCoolant(EntityPlayer player, TileDistributionCoolant tile) {
        super(new ContainerDistributionCoolant(player, tile), tile);
    }

    @Override
    protected String getAvgOutputString(TileDistributionCoolant tile) {
        return String.format("Output: %.2f mB/t @ " + CoolantStack.toFormattedString(tile.getAvgTemperature()), tile.getAvgOutputPerTick());
    }

}
