package com.rk.rkstuff.client.gui;

import com.rk.rkstuff.tank.ContainerTankInteraction;
import com.rk.rkstuff.tank.tile.TileTankInteraction;
import com.rk.rkstuff.util.Textures;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;

public class GuiTankInteraction extends GuiContainer {
    private TileTankInteraction tile;
    private static final int SIZE_X = 176;
    private static final int SIZE_Y = 166;
    private static final int MAX = 53;

    public GuiTankInteraction(EntityPlayer player, TileTankInteraction tile) {
        super(new ContainerTankInteraction(player, tile));
        this.tile = tile;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float tick, int mouseX, int mouseY) {
        this.mc.getTextureManager().bindTexture(Textures.TANK_INTERACTION_GUI);
        int x = (this.width - SIZE_X) / 2;
        int y = (this.height - SIZE_Y) / 2;
        drawTexturedModalRect(x, y, 0, 0, SIZE_X, SIZE_Y);

        fontRendererObj.drawString("Storage: " + ((tile.getMaster() != null) ? tile.getMaster().getCurrentStorage() : "none"), x + 10, y + 10, 0x00000000);
    }
}
