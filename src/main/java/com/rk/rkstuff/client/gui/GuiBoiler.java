package com.rk.rkstuff.client.gui;

import com.rk.rkstuff.container.ContainerBoiler;
import com.rk.rkstuff.tile.TileBoilerBaseMaster;
import com.rk.rkstuff.util.Textures;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

public class GuiBoiler extends GuiContainer {

    private TileBoilerBaseMaster tile;
    private static final int SIZE_X = 176;
    private static final int SIZE_Y = 166;
    private static final int MAX = 53;

    public GuiBoiler(EntityPlayer player, TileBoilerBaseMaster tile) {
        super(new ContainerBoiler(player, tile));
        this.tile = tile;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float tick, int mouseX, int mouseY) {
        this.mc.getTextureManager().bindTexture(Textures.BOILER_GUI);
        int x = (this.width - SIZE_X) / 2;
        int y = (this.height - SIZE_Y) / 2;
        drawTexturedModalRect(x, y, 0, 0, SIZE_X,  SIZE_Y);

        int coolCoolantPx = Math.round(MAX * (float) tile.getCoolCoolantStorage() / (float) tile.getMaxCoolantStorage());
        int hotCoolantPx = Math.round(MAX * (float) tile.getHotCoolantStorage() / (float) tile.getMaxCoolantStorage());
        int waterPx = Math.round(MAX * (float) tile.getWaterStorage() / (float) tile.getMaxWaterStorage());
        int steamPx = Math.round(MAX * (float) tile.getSteamStorage() / (float) tile.getMaxSteamStorage());
        int heatPx = Math.round(MAX * (float) tile.getTemperature() / (float) tile.getMaxTemperature());

        drawTexturedModalRect(x + 21, y + 71 - hotCoolantPx, 192, MAX - hotCoolantPx, 16, hotCoolantPx);
        drawTexturedModalRect(x + 44, y + 71 - coolCoolantPx, 224, MAX - coolCoolantPx, 16, coolCoolantPx);
        drawTexturedModalRect(x + 82, y + 71 - heatPx, 240, MAX - heatPx, 12, heatPx);
        drawTexturedModalRect(x + 116, y + 71 - waterPx, 176, MAX - waterPx, 16, waterPx);
        drawTexturedModalRect(x + 139, y + 71 - steamPx, 208, MAX - steamPx, 16, steamPx);

        int yMin = y + 18;
        int yMax = y + 71;
        if(isInArea(mouseX, mouseY, x + 21, yMin, x + 37, yMax)) {
            //draw tooltip cool coolant
            List<String> list = new ArrayList<>(2);
            list.add("Hot Coolant:");
            list.add(String.format("%d/%d mB", tile.getHotCoolantStorage(), tile.getMaxCoolantStorage()));
            this.func_146283_a(list, mouseX, mouseY);
        } else if(isInArea(mouseX, mouseY, x + 44, yMin, x + 60, yMax)) {
            //draw tooltip hot coolant
            List<String> list = new ArrayList<>(2);
            list.add("Cool Coolant:");
            list.add(String.format("%d/%d mB", tile.getCoolCoolantStorage(), tile.getMaxCoolantStorage()));
            this.func_146283_a(list, mouseX, mouseY);
        } else if(isInArea(mouseX, mouseY, x + 82, yMin, x + 94, yMax)) {
            //draw tooltip prod
            List<String> list = new ArrayList<>(2);
            list.add("Heat:");
            list.add(String.format("%d °C", tile.getTemperature()));
            this.func_146283_a(list, mouseX, mouseY);
        } else if(isInArea(mouseX, mouseY, x + 116, yMin, x + 132, yMax)) {
            //draw tooltip prod
            List<String> list = new ArrayList<>(2);
            list.add("Water:");
            list.add(String.format("%d/%d mB", tile.getWaterStorage(), tile.getMaxWaterStorage()));
            this.func_146283_a(list, mouseX, mouseY);
        } else if(isInArea(mouseX, mouseY, x + 139, yMin, x + 155, yMax)) {
            //draw tooltip prod
            List<String> list = new ArrayList<>(2);
            list.add("Steam:");
            list.add(String.format("%d/%d mB", tile.getSteamStorage(), tile.getMaxSteamStorage()));
            this.func_146283_a(list, mouseX, mouseY);
        }
    }

    private boolean isInArea(int x, int y, int xMin, int yMin, int xMax, int yMax) {
        if(x < xMin)return false;
        if(y < yMin)return false;
        if(x > xMax)return false;
        if(y > yMax)return false;
        return true;
    }
}
