package com.rk.rkstuff.client.gui;

import com.rk.rkstuff.accelerator.ContainerLHCMaster;
import com.rk.rkstuff.accelerator.tile.TileLHCMaster;
import com.rk.rkstuff.coolant.CoolantStack;
import com.rk.rkstuff.helper.GuiHelper;
import com.rk.rkstuff.util.Textures;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

public class GuiLHCMaster extends GuiContainer {

    private static final int SIZE_X = 176;
    private static final int SIZE_Y = 166;

    private TileLHCMaster tile;

    public GuiLHCMaster(EntityPlayer player, TileLHCMaster tile) {
        super(new ContainerLHCMaster(player, tile));
        this.tile = tile;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float tick, int mouseX, int mouseY) {
        this.mc.getTextureManager().bindTexture(Textures.LHC_MASTER_GUI);
        int x = (this.width - SIZE_X) / 2;
        int y = (this.height - SIZE_Y) / 2;
        drawTexturedModalRect(x, y, 0, 0, SIZE_X, SIZE_Y);

        int energyPx = Math.round(53 * (float) tile.getStoredEnergyRF() / (float) tile.getMaxEnergyStorageRF());
        int speedPx = Math.round(80 * tile.getSpeed() / tile.getMaxSpeed());
        if (speedPx > 80) speedPx = 80;
        int coolantPx = Math.round(53 * (float) tile.getTotalCoolant() / (float) tile.getTotalMaxCoolant());

        drawTexturedModalRect(x + 12, y + 69 - energyPx, 176, 53 - energyPx, 12, energyPx);

        drawTexturedModalRect(x + 116, y + 73, 176, 53, speedPx, 5);

        drawTexturedModalRect(x + 152, y + 69 - coolantPx, 188, 53 - coolantPx, 12, coolantPx);

        if (GuiHelper.isInArea(mouseX, mouseY, x + 12, y + 16, x + 24, y + 69)) {
            //draw tooltip cool coolant
            List<String> list = new ArrayList<String>(2);
            list.add("Energy:");
            list.add(String.format("%d/%d RF", tile.getStoredEnergyRF(), tile.getMaxEnergyStorageRF()));
            this.func_146283_a(list, mouseX, mouseY);
        } else if (GuiHelper.isInArea(mouseX, mouseY, x + 47, y + 73, x + 127, y + 78)) {
            //draw tooltip hot coolant
            List<String> list = new ArrayList<String>(2);
            list.add("Speed:");
            list.add(String.format("%.2f/%.2f bps", tile.getSpeed(), tile.getMaxSpeed()));
            this.func_146283_a(list, mouseX, mouseY);
        } else if (GuiHelper.isInArea(mouseX, mouseY, x + 152, y + 16, x + 164, y + 69)) {
            //draw tooltip prod
            List<String> list = new ArrayList<String>(2);
            list.add("Coolant:");
            list.add(String.format("%d/%d mB", tile.getTotalCoolant(), tile.getTotalMaxCoolant()));
            list.add("Temperature: " + CoolantStack.toFormattedString(tile.getAvgCoolantTemp()));
            this.func_146283_a(list, mouseX, mouseY);
        }
    }
}
