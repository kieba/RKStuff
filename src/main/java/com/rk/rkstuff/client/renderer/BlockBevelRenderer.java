package com.rk.rkstuff.client.renderer;

import com.rk.rkstuff.client.model.ModelBevelLarge;
import com.rk.rkstuff.client.model.ModelBevelSmall;
import com.rk.rkstuff.client.model.ModelBevelSmallInverted;
import com.rk.rkstuff.core.block.IBlockBevelLarge;
import com.rk.rkstuff.core.block.IBlockBevelSmall;
import com.rk.rkstuff.core.block.IBlockBevelSmallInverted;
import com.rk.rkstuff.proxy.ClientProxy;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class BlockBevelRenderer implements ISimpleBlockRenderingHandler {

    private ModelBevelLarge modelBevelLarge = new ModelBevelLarge();
    private ModelBevelSmall modelBevelSmall = new ModelBevelSmall();
    private ModelBevelSmallInverted modelBevelSmallInverted = new ModelBevelSmallInverted();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        IIcon icon = block.getIcon(0, 0);
        Tessellator.instance.startDrawingQuads();
        if (block instanceof IBlockBevelSmall) {
            modelBevelSmall.render(icon, 0.5f, 0.5f, 0.5f, 0);
        } else if (block instanceof IBlockBevelSmallInverted) {
            modelBevelSmallInverted.render(icon, 0.5f, 0.5f, 0.5f, 0);
        } else if (block instanceof IBlockBevelLarge) {
            modelBevelLarge.render(icon, 0.5f, 0.5f, 0.5f, 0);
        }
        Tessellator.instance.draw();
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return ClientProxy.blockBevelRenderId;
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        int meta = world.getBlockMetadata(x, y, z);
        IIcon icon = block.getIcon(0, 0);
        if (block instanceof IBlockBevelSmall) {
            modelBevelSmall.render(icon, x + 0.5f, y + 0.5f, z + 0.5f, meta);
        } else if (block instanceof IBlockBevelSmallInverted) {
            modelBevelSmallInverted.render(icon, x + 0.5f, y + 0.5f, z + 0.5f, meta);
        } else if (block instanceof IBlockBevelLarge) {
            modelBevelLarge.render(icon, x + 0.5f, y + 0.5f, z + 0.5f, meta);
        }
        return true;
    }

}
