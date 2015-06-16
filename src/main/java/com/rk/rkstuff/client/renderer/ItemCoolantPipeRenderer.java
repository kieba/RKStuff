package com.rk.rkstuff.client.renderer;

import com.rk.rkstuff.client.model.ModelPipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class ItemCoolantPipeRenderer implements IItemRenderer {

    private final ModelPipe model = new ModelPipe();
    private boolean[] TRUE = new boolean[]{
            true, true, true, true, true, true
    };

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, 0.0f, 1.0f);
        GL11.glScalef(1.0f, 1.0f, 1.0f);

        model.render(TRUE, TRUE, 0);

        GL11.glPopMatrix();
    }
}
