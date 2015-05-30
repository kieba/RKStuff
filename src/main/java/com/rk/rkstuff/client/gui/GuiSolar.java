package com.rk.rkstuff.client.gui;

import com.rk.rkstuff.container.ContainerSolar;
import com.rk.rkstuff.helper.RKLog;
import com.rk.rkstuff.tile.TileBoilerBaseMaster;
import com.rk.rkstuff.tile.TileSolarMaster;
import com.rk.rkstuff.util.Textures;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

import java.util.ArrayList;
import java.util.List;

public class GuiSolar extends GuiContainer {

    private TileSolarMaster tile;
    private static final int SIZE_X = 176;
    private static final int SIZE_Y = 166;
    private static final int MAX = 53;

    public GuiSolar(EntityPlayer player, TileSolarMaster tile) {
        super(new ContainerSolar(player, tile));
        this.tile = tile;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float tick, int mouseX, int mouseY) {
        this.mc.getTextureManager().bindTexture(Textures.SOLAR_GUI);
        int x = (this.width - SIZE_X) / 2;
        int y = (this.height - SIZE_Y) / 2;
        drawTexturedModalRect(x, y, 0, 0, SIZE_X,  SIZE_Y);

        int coolCoolantPx = (int)Math.round(MAX * tile.getCoolCoolantTank() / tile.getMaxTankCapacity());
        int hotCoolantPx = (int)Math.round(MAX * tile.getHotCoolantTank() / tile.getMaxTankCapacity());
        int prodPx = (int)Math.round(MAX * tile.getProductionLastTick() / tile.getProductionMaximal());

        drawTexturedModalRect(x + 26, y + 71 - coolCoolantPx, 176, MAX - coolCoolantPx, 34,  coolCoolantPx);
        drawTexturedModalRect(x + 116, y + 71 - hotCoolantPx, 210, MAX - hotCoolantPx, 34,  hotCoolantPx);
        drawTexturedModalRect(x + 82, y + 71 - prodPx, 244, MAX - prodPx, 12,  prodPx);

        int yMin = y + 18;
        int yMax = y + 71;
        if(isInArea(mouseX, mouseY, x + 26, yMin, x + 60, yMax)) {
            //draw tooltip cool coolant
            List<String> list = new ArrayList<>(2);
            list.add("Cool Coolant:");
            list.add(String.format("%d/%d mB", Math.round(tile.getCoolCoolantTank()), Math.round(tile.getMaxTankCapacity())));
            this.func_146283_a(list, mouseX, mouseY);
        } else if(isInArea(mouseX, mouseY, x + 116, yMin, x + 150, yMax)) {
            //draw tooltip hot coolant
            List<String> list = new ArrayList<>(2);
            list.add("Hot Coolant:");
            list.add(String.format("%d/%d mB", Math.round(tile.getHotCoolantTank()), Math.round(tile.getMaxTankCapacity())));
            this.func_146283_a(list, mouseX, mouseY);
        } else if(isInArea(mouseX, mouseY, x + 82, yMin, x + 94, yMax)) {
            //draw tooltip prod
            List<String> list = new ArrayList<>(2);
            list.add("Production:");
            list.add(String.format("%0.2f/%0.2f mB", tile.getProductionLastTick(), tile.getProductionMaximal()));
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
