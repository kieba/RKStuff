package com.rk.rkstuff.proxy;

import com.rk.rkstuff.client.renderer.BlockBevelRenderer;
import com.rk.rkstuff.client.renderer.TileModelTestRenderer;
import com.rk.rkstuff.client.renderer.TileTankAdapterSpecialRenderer;
import com.rk.rkstuff.tile.TileModelTest;
import com.rk.rkstuff.tile.TileTankAdapter;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

    public static int blockFusionCaseRenderId = -1;
    public static TileModelTestRenderer modelTestRenderer;
    public static TileTankAdapterSpecialRenderer tankAdapterSpecialRenderer;

    @Override
    public void preInit() {

    }

    @Override
    public void init() {
        modelTestRenderer = new TileModelTestRenderer();
        ClientRegistry.bindTileEntitySpecialRenderer(TileModelTest.class, modelTestRenderer);

        //MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(RkStuff.blockModelTest), new ItemModelTestRenderer());
        tankAdapterSpecialRenderer = new TileTankAdapterSpecialRenderer();
        ClientRegistry.bindTileEntitySpecialRenderer(TileTankAdapter.class, tankAdapterSpecialRenderer);

        blockFusionCaseRenderId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new BlockBevelRenderer());
    }

    @Override
    public void postInit() {

    }

}
