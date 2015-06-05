package com.rk.rkstuff.proxy;

import com.rk.rkstuff.client.renderer.TileModelTestRenderer;
import com.rk.rkstuff.tile.TileModelTest;
import cpw.mods.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {

    public static TileModelTestRenderer modelTestRenderer;

    @Override
    public void preInit() {

    }

    @Override
    public void init() {
        modelTestRenderer = new TileModelTestRenderer();
        ClientRegistry.bindTileEntitySpecialRenderer(TileModelTest.class, modelTestRenderer);

        //MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(RkStuff.blockModelTest), new ItemModelTestRenderer());
    }

    @Override
    public void postInit() {

    }

}
