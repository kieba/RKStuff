package com.rk.rkstuff;

import com.rk.rkstuff.block.fluid.BlockCoolCoolantFluid;
import com.rk.rkstuff.block.fluid.BlockHotCoolantFluid;
import com.rk.rkstuff.handler.BucketHandler;
import com.rk.rkstuff.item.BucketBase;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import com.rk.rkstuff.block.BlockMultiBlockTest;
import com.rk.rkstuff.proxy.IProxy;
import com.rk.rkstuff.tile.TileMultiBlockTest;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

@Mod(modid = Reference.MOD_ID, version = Reference.VERSION, name = Reference.MOD_NAME)
public class RkStuff {

    public static final String MODID = "RKStuff";
    public static final String VERSION = "1.0";

    public static Fluid coolCoolant;
    public static Block coolCoolantBlock;
    public static ItemBucket coolCoolantBucket;
    public static Fluid hotCoolant = new Fluid("hotCoolant");
    public static Block hotCoolantBlock;
    public static ItemBucket hotCoolantBucket;

    @Mod.Instance(Reference.MOD_ID)
    public static RkStuff INSTANCE;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static IProxy PROXY;

    public static Block multiBlockTest = new BlockMultiBlockTest();

    @EventHandler
    public void preInit(FMLPreInitializationEvent evt){
        //Blocks
        GameRegistry.registerBlock(multiBlockTest, Reference.BLOCK_MULTI_BLOCK_TEST_NAME);

        //TileEntities
        GameRegistry.registerTileEntity(TileMultiBlockTest.class, Reference.BLOCK_MULTI_BLOCK_TEST_NAME);

        //Fluids
        registerFluids();

    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {


    }



    private void registerFluids(){
        MinecraftForge.EVENT_BUS.register(BucketHandler.INSTANCE);

        coolCoolant = new Fluid("coolCoolant");

        FluidRegistry.registerFluid(coolCoolant);
        coolCoolantBlock = new BlockCoolCoolantFluid(coolCoolant);
        GameRegistry.registerBlock(coolCoolantBlock, MODID + "_" + coolCoolantBlock.getUnlocalizedName().substring(5));
        coolCoolant.setUnlocalizedName(coolCoolantBlock.getUnlocalizedName());
        coolCoolantBucket = new BucketBase(coolCoolantBlock, "BucketCoolCoolant");
        GameRegistry.registerItem(coolCoolantBucket, "coolCoolantBucket");
        FluidContainerRegistry.registerFluidContainer(coolCoolant, new ItemStack(coolCoolantBucket), new ItemStack(Items.bucket));
        BucketHandler.INSTANCE.buckets.put(coolCoolantBlock, coolCoolantBucket);



        hotCoolant = new Fluid("hotCoolant");
        FluidRegistry.registerFluid(hotCoolant);
        hotCoolantBlock = new BlockHotCoolantFluid(hotCoolant).setBlockName("hotCoolantBlock");
        GameRegistry.registerBlock(hotCoolantBlock, MODID + "_" + hotCoolantBlock.getUnlocalizedName().substring(5));
        hotCoolant.setUnlocalizedName(hotCoolantBlock.getUnlocalizedName());
        hotCoolantBucket = new BucketBase(hotCoolantBlock, "BucketHotCoolant");
        GameRegistry.registerItem(hotCoolantBucket, "hotCoolantBucket");
        FluidContainerRegistry.registerFluidContainer(hotCoolant, new ItemStack(hotCoolantBucket), new ItemStack(Items.bucket));
        BucketHandler.INSTANCE.buckets.put(hotCoolantBlock, hotCoolantBucket);
    }
}
