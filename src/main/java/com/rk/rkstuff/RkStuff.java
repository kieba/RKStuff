package com.rk.rkstuff;

import com.rk.rkstuff.block.*;
import com.rk.rkstuff.block.fluid.BlockCoolCoolantFluid;
import com.rk.rkstuff.block.fluid.BlockHotCoolantFluid;
import com.rk.rkstuff.handler.BucketHandler;
import com.rk.rkstuff.helper.FluidHelper;
import com.rk.rkstuff.helper.RKLog;
import com.rk.rkstuff.item.BucketBase;
import com.rk.rkstuff.network.PacketHandler;
import com.rk.rkstuff.proxy.IProxy;
import com.rk.rkstuff.tile.*;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
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

import java.util.Map;

@Mod(modid = Reference.MOD_ID, version = Reference.VERSION, name = Reference.MOD_NAME)
public class RkStuff {
    
    public static Fluid coolCoolant;
    public static Block coolCoolantBlock;
    public static ItemBucket coolCoolantBucket;
    public static Fluid hotCoolant = new Fluid("hotCoolant");
    public static Block hotCoolantBlock;
    public static ItemBucket hotCoolantBucket;

    public static Block blockSolarOutput = new BlockSolarOutput();
    public static Block blockSolarInput = new BlockSolarInput();
    public static Block blockSolarMaster = new BlockSolarMaster();
    public static Block blockSolar = new BlockSolar();

    public static Block blockBoilerBase = new BlockBoilerBase();
    public static Block blockBoilerBaseInput = new BlockBoilerBaseInput();
    public static Block blockBoilerBaseOutput = new BlockBoilerBaseOutput();
    public static Block blockBoilerBaseMaster = new BlockBoilerBaseMaster();
    public static Block blockBoilerTank = new BlockBoilerTank();


    @Mod.Instance(Reference.MOD_ID)
    public static RkStuff INSTANCE;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static IProxy PROXY;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        PacketHandler.init();

        //Blocks
        GameRegistry.registerBlock(blockSolarOutput, Reference.BLOCK_SOLAR_OUTPUT);
        GameRegistry.registerBlock(blockSolarInput, Reference.BLOCK_SOLAR_INPUT);
        GameRegistry.registerBlock(blockSolarMaster, Reference.BLOCK_SOLAR_MASTER);
        GameRegistry.registerBlock(blockSolar, Reference.BLOCK_SOLAR);

        GameRegistry.registerBlock(blockBoilerBase, Reference.BLOCK_BOILER_BASE);
        GameRegistry.registerBlock(blockBoilerBaseInput, Reference.BLOCK_BOILER_BASE_INPUT);
        GameRegistry.registerBlock(blockBoilerBaseOutput, Reference.BLOCK_BOILER_BASE_OUTPUT);
        GameRegistry.registerBlock(blockBoilerBaseMaster, Reference.BLOCK_BOILER_BASE_MASTER);
        GameRegistry.registerBlock(blockBoilerTank, Reference.BLOCK_BOILER_TANK);

        //TileEntities
        GameRegistry.registerTileEntity(TileSolarOutput.class, Reference.TILE_SOLAR_OUTPUT);
        GameRegistry.registerTileEntity(TileSolarInput.class, Reference.TILE_SOLAR_INPUT);
        GameRegistry.registerTileEntity(TileSolarMaster.class, Reference.TILE_SOLAR_MASTER);

        GameRegistry.registerTileEntity(TileBoilerBaseInput.class, Reference.TILE_BOILER_BASE_INPUT);
        GameRegistry.registerTileEntity(TileBoilerBaseOutput.class, Reference.TILE_BOILER_BASE_OUTPUT);
        GameRegistry.registerTileEntity(TileBoilerBaseMaster.class, Reference.TILE_BOILER_BASE_MASTER);

        //Fluids
        registerFluids();

    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        for(Map.Entry<String, Fluid> e : FluidRegistry.getRegisteredFluids().entrySet()) {
            if(e.getValue().getUnlocalizedName().equals("fluid.steam")) {
                FluidHelper.steamId = e.getValue().getID();
            }
            if(e.getValue().getUnlocalizedName().equals("fluid.tile.water")) {
                FluidHelper.waterId = e.getValue().getID();
            }
        }
    }



    private void registerFluids(){
        MinecraftForge.EVENT_BUS.register(BucketHandler.INSTANCE);

        coolCoolant = new Fluid(Reference.FLUID_COOL_COOLANT_NAME);
        FluidRegistry.registerFluid(coolCoolant);
        coolCoolantBlock = new BlockCoolCoolantFluid(coolCoolant);
        GameRegistry.registerBlock(coolCoolantBlock, coolCoolantBlock.getUnlocalizedName().substring(5));
        coolCoolant.setUnlocalizedName(coolCoolantBlock.getUnlocalizedName());
        coolCoolantBucket = new BucketBase(coolCoolantBlock, "Bucket" + Reference.FLUID_COOL_COOLANT_NAME);
        GameRegistry.registerItem(coolCoolantBucket, "Bucket" + Reference.FLUID_COOL_COOLANT_NAME);
        FluidContainerRegistry.registerFluidContainer(coolCoolant, new ItemStack(coolCoolantBucket), new ItemStack(Items.bucket));
        BucketHandler.INSTANCE.buckets.put(coolCoolantBlock, coolCoolantBucket);



        hotCoolant = new Fluid(Reference.FLUID_HOT_COOLANT_NAME);
        FluidRegistry.registerFluid(hotCoolant);
        hotCoolantBlock = new BlockHotCoolantFluid(hotCoolant);
        GameRegistry.registerBlock(hotCoolantBlock, hotCoolantBlock.getUnlocalizedName().substring(5));
        hotCoolant.setUnlocalizedName(hotCoolantBlock.getUnlocalizedName());
        hotCoolantBucket = new BucketBase(hotCoolantBlock, "Bucket" + Reference.FLUID_HOT_COOLANT_NAME);
        GameRegistry.registerItem(hotCoolantBucket, "Bucket" + Reference.FLUID_HOT_COOLANT_NAME);
        FluidContainerRegistry.registerFluidContainer(hotCoolant, new ItemStack(hotCoolantBucket), new ItemStack(Items.bucket));
        BucketHandler.INSTANCE.buckets.put(hotCoolantBlock, hotCoolantBucket);
    }
}
