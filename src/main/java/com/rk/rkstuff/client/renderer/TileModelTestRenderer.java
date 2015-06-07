package com.rk.rkstuff.client.renderer;

import com.rk.rkstuff.client.model.ModelTest;
import com.rk.rkstuff.tile.TileModelTest;
import com.rk.rkstuff.util.Reference;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileModelTestRenderer extends TileEntitySpecialRenderer {

    private final ModelTest model = new ModelTest();
    private ResourceLocation loc = new ResourceLocation(Reference.MOD_ID + ":textures/models/BevelSmall.png");

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float tick) {
        if (tile instanceof TileModelTest) {
            TileModelTest tileModelTest = (TileModelTest) tile;
            this.bindTexture(loc);


            GL11.glPushMatrix();
            GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
            GL11.glScalef(1.0f, 1.0f, 1.0f);
            GL11.glRotatef(90, 0.0F, 1.0F, 0.0F);

            model.render();

            GL11.glPopMatrix();

        }


    }
}
