package com.rk.rkstuff.client.gui;

import com.rk.rkstuff.container.ContainerSolar;
import com.rk.rkstuff.util.Textures;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

public class GuiSolar extends GuiContainer {

    private static final int SIZE_X = 176;
    private static final int SIZE_Y = 166;

    public GuiSolar(InventoryPlayer inventoryPlayer) {
        super(new ContainerSolar(inventoryPlayer));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float tick, int mouseX, int mouseY) {
        this.mc.getTextureManager().bindTexture(Textures.SOLAR_GUI);
        int x = (this.width - SIZE_X) / 2;
        int y = (this.height - SIZE_Y) / 2;
        drawTexturedModalRect(x, y, 0, 0, SIZE_X,  SIZE_Y);
    }

}
