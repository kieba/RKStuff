package com.rk.rkstuff.client.gui.elements;

import cofh.api.energy.IEnergyStorage;
import cofh.lib.gui.GuiBase;
import cofh.lib.gui.element.ElementEnergyStored;
import cofh.lib.render.RenderHelper;
import com.rk.rkstuff.util.Reference;
import net.minecraft.util.ResourceLocation;

public class ElementEnergyStoredScaled extends ElementEnergyStored {
    public static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(Reference.MOD_ID + ":textures/gui/elements/EnergyScaled.png");

    public ElementEnergyStoredScaled(GuiBase guiBase, int i, int i1, IEnergyStorage iEnergyStorage) {
        super(guiBase, i, i1, iEnergyStorage);
        texture = DEFAULT_TEXTURE;
        sizeY = 60;
    }

    @Override
    public void drawBackground(int var1, int var2, float var3) {
        int var4 = this.getScaled();
        RenderHelper.bindTexture(this.texture);
        this.drawTexturedModalRect(this.posX, this.posY, 0, 0, this.sizeX, this.sizeY);
        this.drawTexturedModalRect(this.posX, this.posY + sizeY - var4, 16, sizeY - var4, this.sizeX, var4);
    }
}
