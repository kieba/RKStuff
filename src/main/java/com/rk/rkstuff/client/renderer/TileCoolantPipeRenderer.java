package com.rk.rkstuff.client.renderer;

import com.rk.rkstuff.client.model.ModelPipe;
import com.rk.rkstuff.coolant.tile.TileCoolantPipe;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

public class TileCoolantPipeRenderer extends TileEntitySpecialRenderer {

    private final ModelPipe model = new ModelPipe();

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float tick) {
        if (tile instanceof TileCoolantPipe) {
            TileCoolantPipe t = (TileCoolantPipe) tile;
            GL11.glPushMatrix();
            GL11.glTranslatef((float) x, (float) y, (float) z + 1.0f);
            GL11.glScalef(1.0f, 1.0f, 1.0f);

            model.render(t.getConnectedSides(), t.getAdapterSides(), t.getBlockMetadata());

            GL11.glPopMatrix();
        }

    }
}
