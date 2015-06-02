package com.rk.rkstuff.client.gui;

import com.rk.rkstuff.helper.GuiHelper;
import com.rk.rkstuff.util.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;

import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiButtonRK extends GuiButton {

	private boolean isSelected = false;

    private ResourceLocation texture;
    private TextureBounds btn;
    private TextureBounds overlay;

    public GuiButtonRK(int id, ResourceLocation texture, TextureBounds btn) {
        this(id, texture, btn, null, "");
    }

    public GuiButtonRK(int id, ResourceLocation texture, TextureBounds btn, String text) {
        this(id, texture, btn, null, text);
    }

    public GuiButtonRK(int id,ResourceLocation texture,  TextureBounds btn, TextureBounds overlay) {
        this(id, texture, btn, overlay, "");
    }

    public GuiButtonRK(int id, ResourceLocation texture, TextureBounds btn, TextureBounds overlay, String text) {
        super(id, btn.xPos, btn.yPos, btn.width, btn.height, text);
        this.texture = texture;
    	this.btn = btn;
        this.overlay = overlay;
    }

    public int getButtonIndex() {
        return btn.index;
    }

    public int getOverlayIndex() {
        if(overlay != null) return overlay.index;
        return 0;
    }

    public void setButtonIndex(int index) {
    	btn.setIndex(index);
    }

    public void setOverlayIndex(int index) {
        if(overlay != null) overlay.setIndex(index);
    }

    public void setSelected(boolean selected) {
    	isSelected = selected;
    }

    @Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (this.visible) {
            mc.getTextureManager().bindTexture(texture);
            drawTextureBounds(btn, mouseX, mouseY);

            if(overlay != null) {
                drawTextureBounds(overlay, mouseX, mouseY);
            }
		}
	}

    private void drawTextureBounds(TextureBounds bounds, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        int texOffsetY = 0;
        if(bounds.enableSelect && isSelected) {
            texOffsetY = 2 * bounds.texHeight;
        } else if(bounds.enableHover && GuiHelper.isInArea(mouseX, mouseY, bounds.xPos, bounds.yPos, bounds.xPos + bounds.width, bounds.yPos + bounds.height)) {
            texOffsetY = bounds.texHeight;
        }

        int texOffsetX = bounds.index * bounds.texWidth;

        float scaleX = 1.0f / bounds.totalSizeX;
        float scaleY = 1.0f / bounds.totalSizeY;

        double top = (bounds.texPosY + texOffsetY) * scaleY;
        double bottom = (bounds.texPosY + bounds.texHeight + texOffsetY) * scaleY;
        double left = (bounds.texPosX + texOffsetX) * scaleX;
        double right =  (bounds.texPosX + bounds.texWidth + texOffsetX) * scaleX;

        Tessellator var9 = Tessellator.instance;
        var9.startDrawingQuads();
        var9.addVertexWithUV(bounds.xPos, bounds.yPos, (double) this.zLevel, left, top);
        var9.addVertexWithUV(bounds.xPos, bounds.yPos + bounds.height, (double) this.zLevel, left, bottom);
        var9.addVertexWithUV(bounds.xPos + bounds.width, bounds.yPos + bounds.height, (double) this.zLevel, right, bottom);
        var9.addVertexWithUV(bounds.xPos + bounds.width, bounds.yPos, (double) this.zLevel,  right, top);
        var9.draw();
    }

}

