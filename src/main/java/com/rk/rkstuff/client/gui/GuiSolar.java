package com.rk.rkstuff.client.gui;

import com.rk.rkstuff.helper.GuiHelper;
import com.rk.rkstuff.solar.ContainerSolar;
import com.rk.rkstuff.solar.tile.TileSolarMaster;
import com.rk.rkstuff.util.Textures;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;

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

        int coolantPx = Math.round(MAX * tile.getCoolantBuffer().getAmount() / tile.getMaxTankCapacity());
        int prodPx = (int)Math.round(MAX * tile.getProductionLastTick() / tile.getProductionMaximal());

        drawTexturedModalRect(x + 26, y + 71 - coolantPx, 176, MAX - coolantPx, 34, coolantPx);
        drawTexturedModalRect(x + 82, y + 71 - prodPx, 244, MAX - prodPx, 12,  prodPx);

        int yMin = y + 18;
        int yMax = y + 71;
        if(GuiHelper.isInArea(mouseX, mouseY, x + 26, yMin, x + 60, yMax)) {
            //draw tooltip cool coolant
            List<String> list = new ArrayList<String>(2);
            list.add("Coolant:");
            list.add(String.format("%d/%d mB(%.2f °C)", Math.round(tile.getCoolantBuffer().getAmount()), Math.round(tile.getMaxTankCapacity()), tile.getCoolantBuffer().getTemperature()));
            this.func_146283_a(list, mouseX, mouseY);
        } else if(GuiHelper.isInArea(mouseX, mouseY, x + 82, yMin, x + 94, yMax)) {
            //draw tooltip prod
            List<String> list = new ArrayList<String>(2);
            list.add("Production:");
            list.add(String.format("%.2f/%.2f mB", tile.getProductionLastTick(), tile.getProductionMaximal()));
            this.func_146283_a(list, mouseX, mouseY);
        }
    }

}
