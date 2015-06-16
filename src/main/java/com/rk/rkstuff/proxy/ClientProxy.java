package com.rk.rkstuff.proxy;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.client.renderer.BlockBevelRenderer;
import com.rk.rkstuff.client.renderer.ItemCoolantPipeRenderer;
import com.rk.rkstuff.client.renderer.TileCoolantPipeRenderer;
import com.rk.rkstuff.client.renderer.TileTankAdapterSpecialRenderer;
import com.rk.rkstuff.coolant.tile.TileCoolantPipe;
import com.rk.rkstuff.tank.tile.TileTankAdapter;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy {

    public static int blockBevelRenderId = -1;
    public static TileCoolantPipeRenderer coolantPipeRenderer;
    public static TileTankAdapterSpecialRenderer tankAdapterSpecialRenderer;

    @Override
    public void preInit() {

    }

    @Override
    public void init() {
        coolantPipeRenderer = new TileCoolantPipeRenderer();
        ClientRegistry.bindTileEntitySpecialRenderer(TileCoolantPipe.class, coolantPipeRenderer);

        tankAdapterSpecialRenderer = new TileTankAdapterSpecialRenderer();
        ClientRegistry.bindTileEntitySpecialRenderer(TileTankAdapter.class, tankAdapterSpecialRenderer);

        blockBevelRenderId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new BlockBevelRenderer());

        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(RkStuff.blockCoolantPipe), new ItemCoolantPipeRenderer());
    }

    @Override
    public void postInit() {

    }

}
