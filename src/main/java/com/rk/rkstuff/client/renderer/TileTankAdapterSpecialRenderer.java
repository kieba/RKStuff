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
        int radius = tankAdapter.getInnerRadius();
        float fillHeight = tankAdapter.getFillHeight();

        if (fillHeight == 0.0f) return;

        this.bindTexture(TextureMap.locationBlocksTexture);
        IIcon fluidIicon;
        if (tankAdapter.isFluidStack()) {
            fluidIicon = tankAdapter.getCurrentFluidStack().getFluid().getIcon();
        } else {
            fluidIicon = RkStuff.fluidCoolant.getIcon();
        }

        Tessellator tessellator = Tessellator.instance;
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        tessellator.startDrawingQuads();


        GL11.glEnable(GL11.GL_BLEND);

        //North
        for (int yOffset = 0; yOffset < fillHeight; yOffset++) {
            for (int xOffset = -radius; xOffset < radius + 1; xOffset++) {
                tessellator.addVertexWithUV(xOffset, yOffset + 1, -radius, fluidIicon.getMinU(), fluidIicon.getMinV());
                tessellator.addVertexWithUV(xOffset, yOffset + 1 + (yOffset + 1 > fillHeight ? fillHeight % 1 : 1), -radius, fluidIicon.getMinU(), fluidIicon.getMinV() + (fluidIicon.getMaxV() - fluidIicon.getMinV()) * (yOffset + 1 > fillHeight ? fillHeight % 1 : 1));
                tessellator.addVertexWithUV(xOffset + 1, yOffset + 1 + (yOffset + 1 > fillHeight ? fillHeight % 1 : 1), -radius, fluidIicon.getMaxU(), fluidIicon.getMinV() + (fluidIicon.getMaxV() - fluidIicon.getMinV()) * (yOffset + 1 > fillHeight ? fillHeight % 1 : 1));
                tessellator.addVertexWithUV(xOffset + 1, yOffset + 1, -radius, fluidIicon.getMaxU(), fluidIicon.getMinV());
            }
        }

        //West
        for (int yOffset = 0; yOffset < fillHeight; yOffset++) {
            for (int zOffset = -radius; zOffset < radius + 1; zOffset++) {
                tessellator.addVertexWithUV(-radius, yOffset + 1, zOffset + 1, fluidIicon.getMinU(), fluidIicon.getMinV());
                tessellator.addVertexWithUV(-radius, yOffset + 1 + (yOffset + 1 > fillHeight ? fillHeight % 1 : 1), zOffset + 1, fluidIicon.getMinU(), fluidIicon.getMaxV());
                tessellator.addVertexWithUV(-radius, yOffset + 1 + (yOffset + 1 > fillHeight ? fillHeight % 1 : 1), zOffset, fluidIicon.getMaxU(), fluidIicon.getMaxV());
                tessellator.addVertexWithUV(-radius, yOffset + 1, zOffset, fluidIicon.getMaxU(), fluidIicon.getMinV());
            }
        }


        //South
        for (int yOffset = 0; yOffset < fillHeight; yOffset++) {
            for (int xOffset = -radius; xOffset < radius + 1; xOffset++) {
                tessellator.addVertexWithUV(xOffset + 1, yOffset + 1, radius + 1, fluidIicon.getMaxU(), fluidIicon.getMinV());
                tessellator.addVertexWithUV(xOffset + 1, yOffset + 1 + (yOffset + 1 > fillHeight ? fillHeight % 1 : 1), radius + 1, fluidIicon.getMaxU(), fluidIicon.getMaxV());
                tessellator.addVertexWithUV(xOffset, yOffset + 1 + (yOffset + 1 > fillHeight ? fillHeight % 1 : 1), radius + 1, fluidIicon.getMinU(), fluidIicon.getMaxV());
                tessellator.addVertexWithUV(xOffset, yOffset + 1, radius + 1, fluidIicon.getMinU(), fluidIicon.getMinV());
            }
        }

        //East
        for (int yOffset = 0; yOffset < fillHeight; yOffset++) {
            for (int zOffset = -radius; zOffset < radius + 1; zOffset++) {
                tessellator.addVertexWithUV(radius + 1, yOffset + 1, zOffset, fluidIicon.getMaxU(), fluidIicon.getMinV());
                tessellator.addVertexWithUV(radius + 1, yOffset + 1 + (yOffset + 1 > fillHeight ? fillHeight % 1 : 1), zOffset, fluidIicon.getMaxU(), fluidIicon.getMaxV());
                tessellator.addVertexWithUV(radius + 1, yOffset + 1 + (yOffset + 1 > fillHeight ? fillHeight % 1 : 1), zOffset + 1, fluidIicon.getMinU(), fluidIicon.getMaxV());
                tessellator.addVertexWithUV(radius + 1, yOffset + 1, zOffset + 1, fluidIicon.getMinU(), fluidIicon.getMinV());
            }
        }

        for (int xOffset = -radius; xOffset < radius + 1; xOffset++) {
            for (int zOffset = -radius; zOffset < radius + 1; zOffset++) {
                tessellator.addVertexWithUV(xOffset, fillHeight + 1, zOffset, fluidIicon.getMinU(), fluidIicon.getMinV());
                tessellator.addVertexWithUV(xOffset, fillHeight + 1, zOffset + 1, fluidIicon.getMinU(), fluidIicon.getMaxV());
                tessellator.addVertexWithUV(xOffset + 1, fillHeight + 1, zOffset + 1, fluidIicon.getMaxU(), fluidIicon.getMaxV());
                tessellator.addVertexWithUV(xOffset + 1, fillHeight + 1, zOffset, fluidIicon.getMaxU(), fluidIicon.getMinV());
            }
        }
        tessellator.draw();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
