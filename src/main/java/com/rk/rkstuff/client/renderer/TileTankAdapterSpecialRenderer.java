package com.rk.rkstuff.client.renderer;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.tank.tile.TileTankAdapter;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;

public class TileTankAdapterSpecialRenderer extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f) {
        TileTankAdapter tankAdapter = (TileTankAdapter) tile;
        this.bindTexture(TextureMap.locationBlocksTexture);
        IIcon coolCoolant = RkStuff.coolCoolant.getIcon();
        Tessellator tessellator = Tessellator.instance;
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        tessellator.startDrawingQuads();

        int radius = tankAdapter.getInnerRadius();
        float fillHeight = tankAdapter.getFillHeight();

        GL11.glEnable(GL11.GL_BLEND);

        //North
        for (int yOffset = 0; yOffset < fillHeight; yOffset++) {
            for (int xOffset = -radius; xOffset < radius + 1; xOffset++) {
                tessellator.addVertexWithUV(xOffset, yOffset + 1, -radius, coolCoolant.getMinU(), coolCoolant.getMinV());
                tessellator.addVertexWithUV(xOffset, yOffset + 1 + (yOffset + 1 > fillHeight ? fillHeight % 1 : 1), -radius, coolCoolant.getMinU(), coolCoolant.getMinV() + (coolCoolant.getMaxV() - coolCoolant.getMinV()) * (yOffset + 1 > fillHeight ? fillHeight % 1 : 1));
                tessellator.addVertexWithUV(xOffset + 1, yOffset + 1 + (yOffset + 1 > fillHeight ? fillHeight % 1 : 1), -radius, coolCoolant.getMaxU(), coolCoolant.getMinV() + (coolCoolant.getMaxV() - coolCoolant.getMinV()) * (yOffset + 1 > fillHeight ? fillHeight % 1 : 1));
                tessellator.addVertexWithUV(xOffset + 1, yOffset + 1, -radius, coolCoolant.getMaxU(), coolCoolant.getMinV());
            }
        }

        //West
        for (int yOffset = 0; yOffset < fillHeight; yOffset++) {
            for (int zOffset = -radius; zOffset < radius + 1; zOffset++) {
                tessellator.addVertexWithUV(-radius, yOffset + 1, zOffset + 1, coolCoolant.getMinU(), coolCoolant.getMinV());
                tessellator.addVertexWithUV(-radius, yOffset + 1 + (yOffset + 1 > fillHeight ? fillHeight % 1 : 1), zOffset + 1, coolCoolant.getMinU(), coolCoolant.getMaxV());
                tessellator.addVertexWithUV(-radius, yOffset + 1 + (yOffset + 1 > fillHeight ? fillHeight % 1 : 1), zOffset, coolCoolant.getMaxU(), coolCoolant.getMaxV());
                tessellator.addVertexWithUV(-radius, yOffset + 1, zOffset, coolCoolant.getMaxU(), coolCoolant.getMinV());
            }
        }


        //South
        for (int yOffset = 0; yOffset < fillHeight; yOffset++) {
            for (int xOffset = -radius; xOffset < radius + 1; xOffset++) {
                tessellator.addVertexWithUV(xOffset + 1, yOffset + 1, radius + 1, coolCoolant.getMaxU(), coolCoolant.getMinV());
                tessellator.addVertexWithUV(xOffset + 1, yOffset + 1 + (yOffset + 1 > fillHeight ? fillHeight % 1 : 1), radius + 1, coolCoolant.getMaxU(), coolCoolant.getMaxV());
                tessellator.addVertexWithUV(xOffset, yOffset + 1 + (yOffset + 1 > fillHeight ? fillHeight % 1 : 1), radius + 1, coolCoolant.getMinU(), coolCoolant.getMaxV());
                tessellator.addVertexWithUV(xOffset, yOffset + 1, radius + 1, coolCoolant.getMinU(), coolCoolant.getMinV());
            }
        }

        //East
        for (int yOffset = 0; yOffset < fillHeight; yOffset++) {
            for (int zOffset = -radius; zOffset < radius + 1; zOffset++) {
                tessellator.addVertexWithUV(radius + 1, yOffset + 1, zOffset, coolCoolant.getMaxU(), coolCoolant.getMinV());
                tessellator.addVertexWithUV(radius + 1, yOffset + 1 + (yOffset + 1 > fillHeight ? fillHeight % 1 : 1), zOffset, coolCoolant.getMaxU(), coolCoolant.getMaxV());
                tessellator.addVertexWithUV(radius + 1, yOffset + 1 + (yOffset + 1 > fillHeight ? fillHeight % 1 : 1), zOffset + 1, coolCoolant.getMinU(), coolCoolant.getMaxV());
                tessellator.addVertexWithUV(radius + 1, yOffset + 1, zOffset + 1, coolCoolant.getMinU(), coolCoolant.getMinV());
            }
        }

        for (int xOffset = -radius; xOffset < radius + 1; xOffset++) {
            for (int zOffset = -radius; zOffset < radius + 1; zOffset++) {
                tessellator.addVertexWithUV(xOffset, fillHeight + 1, zOffset, coolCoolant.getMinU(), coolCoolant.getMinV());
                tessellator.addVertexWithUV(xOffset, fillHeight + 1, zOffset + 1, coolCoolant.getMinU(), coolCoolant.getMaxV());
                tessellator.addVertexWithUV(xOffset + 1, fillHeight + 1, zOffset + 1, coolCoolant.getMaxU(), coolCoolant.getMaxV());
                tessellator.addVertexWithUV(xOffset + 1, fillHeight + 1, zOffset, coolCoolant.getMaxU(), coolCoolant.getMinV());
            }
        }
        tessellator.draw();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
