package com.rk.rkstuff;

import com.rk.rkstuff.block.BlockMultiBlockTest;
import com.rk.rkstuff.proxy.IProxy;
import com.rk.rkstuff.tile.TileMultiBlockTest;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = Reference.MOD_ID, version = Reference.VERSION, name = Reference.MOD_NAME)
public class RkStuff {

    @Mod.Instance(Reference.MOD_ID)
    public static RkStuff INSTANCE;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static IProxy PROXY;

    public static Block multiBlockTest = new BlockMultiBlockTest();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        //Blocks
        GameRegistry.registerBlock(multiBlockTest, Reference.BLOCK_MULTI_BLOCK_TEST_NAME);

        //TileEntities
        GameRegistry.registerTileEntity(TileMultiBlockTest.class, Reference.BLOCK_MULTI_BLOCK_TEST_NAME);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }

}
