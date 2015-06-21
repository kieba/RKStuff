package com.rk.rkstuff;

import com.rk.rkstuff.boiler.block.*;
import com.rk.rkstuff.boiler.tile.TileBoilerBaseInput;
import com.rk.rkstuff.boiler.tile.TileBoilerBaseMaster;
import com.rk.rkstuff.boiler.tile.TileBoilerBaseOutput;
import com.rk.rkstuff.client.gui.GuiHandler;
import com.rk.rkstuff.client.model.ModelPipe;
import com.rk.rkstuff.coolant.block.*;
import com.rk.rkstuff.coolant.tile.TileCoolantExtractor;
import com.rk.rkstuff.coolant.tile.TileCoolantInjector;
import com.rk.rkstuff.coolant.tile.TileCoolantPipe;
import com.rk.rkstuff.core.block.BlockRK;
import com.rk.rkstuff.distribution.block.BlockDistributionCoolant;
import com.rk.rkstuff.distribution.block.BlockDistributionEnergy;
import com.rk.rkstuff.distribution.block.BlockDistributionFluid;
import com.rk.rkstuff.distribution.tile.TileDistributionCoolant;
import com.rk.rkstuff.distribution.tile.TileDistributionEnergy;
import com.rk.rkstuff.distribution.tile.TileDistributionFluid;
import com.rk.rkstuff.fusion.block.*;
import com.rk.rkstuff.fusion.tile.TileFusionCaseFluidIO;
import com.rk.rkstuff.fusion.tile.TileFusionControlEnergyIO;
import com.rk.rkstuff.fusion.tile.TileFusionControlItemIO;
import com.rk.rkstuff.fusion.tile.TileFusionControlMaster;
import com.rk.rkstuff.handler.BucketHandler;
import com.rk.rkstuff.helper.FluidHelper;
import com.rk.rkstuff.item.BucketBase;
import com.rk.rkstuff.item.ItemLinker;
import com.rk.rkstuff.item.ItemRK;
import com.rk.rkstuff.network.PacketHandler;
import com.rk.rkstuff.proxy.IProxy;
import com.rk.rkstuff.solar.block.BlockSolar;
import com.rk.rkstuff.solar.block.BlockSolarInput;
import com.rk.rkstuff.solar.block.BlockSolarMaster;
import com.rk.rkstuff.solar.block.BlockSolarOutput;
import com.rk.rkstuff.solar.tile.TileSolarInput;
import com.rk.rkstuff.solar.tile.TileSolarMaster;
import com.rk.rkstuff.solar.tile.TileSolarOutput;
import com.rk.rkstuff.tank.block.*;
import com.rk.rkstuff.tank.tile.TileTankAdapter;
import com.rk.rkstuff.tank.tile.TileTankInteraction;
import com.rk.rkstuff.tank.tile.TileTankValve;
import com.rk.rkstuff.teleporter.block.BlockTeleporter;
import com.rk.rkstuff.teleporter.tile.TileTeleporter;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computercraft.api.ComputerCraftAPI;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.Map;

@Mod(modid = Reference.MOD_ID, version = Reference.VERSION, name = Reference.MOD_NAME)
public class RkStuff {

    public static Fluid fluidCoolant;
    public static Block fluidCoolantBlock;
    public static ItemBucket fluidCoolantBucket;
    public static Fluid fluidUsedCoolant;
    public static Block fluidUsedCoolantBlock;
    public static ItemBucket fluidUsedCoolantBucket;

    public static Block blockMachineBlock = new BlockRK(Material.iron, Reference.BLOCK_MACHINE_BLOCK);
    public static Block blockPortlandite = new BlockRK(Material.iron, Reference.BLOCK_PORTLANDITE);

    public static Block blockSolarOutput = new BlockSolarOutput();
    public static Block blockSolarInput = new BlockSolarInput();
    public static BlockSolarMaster blockSolarMaster = new BlockSolarMaster();
    public static Block blockSolar = new BlockSolar();

    public static Block blockBoilerBase = new BlockBoilerBase();
    public static Block blockBoilerBaseInput = new BlockBoilerBaseInput();
    public static Block blockBoilerBaseOutput = new BlockBoilerBaseOutput();
    public static BlockBoilerBaseMaster blockBoilerBaseMaster = new BlockBoilerBaseMaster();
    public static Block blockBoilerTank = new BlockBoilerTank();

    public static BlockDistributionEnergy blockDistributionEnergy = new BlockDistributionEnergy();
    public static BlockDistributionFluid blockDistributionFluid = new BlockDistributionFluid();
    public static BlockDistributionCoolant blockDistributionCoolant = new BlockDistributionCoolant();

    public static Block blockTeleporter = new BlockTeleporter();

    public static Block blockTankAdapter = new BlockTankAdapter();
    public static Block blockTank = new BlockTank();
    public static Block blockTankBevelLarge = new BlockTankBevelLarge();
    public static Block blockTankBevelSmall = new BlockTankBevelSmall();
    public static Block blockTankInteraction = new BlockTankInteraction();
    public static Block blockTankValve = new BlockTankValve();

    public static Block blockFusionCase = new BlockFusionCase();
    public static Block blockFusionCaseBevelSmall = new BlockFusionCaseBevelSmall();
    public static Block blockFusionCaseBevelSmallInverted = new BlockFusionCaseBevelSmallInverted();
    public static Block blockFusionCaseBevelLarge = new BlockFusionCaseBevelLarge();
    public static Block blockFusionCaseFluidIO = new BlockFusionCaseFluidIO();
    public static Block blockFusionControlCase = new BlockFusionControlCase();
    public static Block blockFusionControlCore = new BlockFusionControlCore();
    public static Block blockFusionControlEnergyIO = new BlockFusionControlEnergyIO();
    public static Block blockFusionControlItemIO = new BlockFusionControlItemIO();
    public static Block blockFusionControlMaster = new BlockFusionControlMaster();
    public static Block blockFusionCore = new BlockFusionCore();

    public static Block blockCoolantPipe = new BlockCoolantPipe();
    public static Block blockCoolantInjector = new BlockCoolantInjector();
    public static Block blockCoolantExtractor = new BlockCoolantExtractor();

    public static Item itemValve = new ItemRK(Reference.ITEM_VALVE);
    public static Item itemSolarPanel = new ItemRK(Reference.ITEM_SOLAR_PANEL);
    public static Item itemControlUnit = new ItemRK(Reference.ITEM_CONTROL_UNIT);
    public static Item itemWire = new ItemRK(Reference.ITEM_WIRE);
    public static Item itemSolarTile = new ItemRK(Reference.ITEM_SOLAR_TILE);
    public static Item itemWoodAsh = new ItemRK(Reference.ITEM_WOOD_ASH);
    public static Item itemPotash = new ItemRK(Reference.ITEM_POTASH);
    public static Item itemPotassiumHydroxide = new ItemRK(Reference.ITEM_POTASSIUM_HYDROXIDE);
    public static Item itemGlycerine = new ItemRK(Reference.ITEM_GLYCERINE);

    public static ItemLinker itemLinker = new ItemLinker();

    @Mod.Instance(Reference.MOD_ID)
    public static RkStuff INSTANCE;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static IProxy PROXY;

    private static GuiHandler guiHandler;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);

        PacketHandler.init();

        //Blocks
        GameRegistry.registerBlock(blockMachineBlock, Reference.BLOCK_MACHINE_BLOCK);
        GameRegistry.registerBlock(blockPortlandite, Reference.BLOCK_PORTLANDITE);

        GameRegistry.registerBlock(blockSolarOutput, Reference.BLOCK_SOLAR_OUTPUT);
        GameRegistry.registerBlock(blockSolarInput, Reference.BLOCK_SOLAR_INPUT);
        GameRegistry.registerBlock(blockSolarMaster, Reference.BLOCK_SOLAR_MASTER);
        GameRegistry.registerBlock(blockSolar, Reference.BLOCK_SOLAR);

        GameRegistry.registerBlock(blockBoilerBase, Reference.BLOCK_BOILER_BASE);
        GameRegistry.registerBlock(blockBoilerBaseInput, Reference.BLOCK_BOILER_BASE_INPUT);
        GameRegistry.registerBlock(blockBoilerBaseOutput, Reference.BLOCK_BOILER_BASE_OUTPUT);
        GameRegistry.registerBlock(blockBoilerBaseMaster, Reference.BLOCK_BOILER_BASE_MASTER);
        GameRegistry.registerBlock(blockBoilerTank, Reference.BLOCK_BOILER_TANK);

        GameRegistry.registerBlock(blockDistributionEnergy, Reference.BLOCK_DISTRIBUTION_ENERGY);
        GameRegistry.registerBlock(blockDistributionFluid, Reference.BLOCK_DISTRIBUTION_FLUID);
        GameRegistry.registerBlock(blockDistributionCoolant, Reference.BLOCK_DISTRIBUTION_COOLANT);

        GameRegistry.registerBlock(blockTeleporter, Reference.BLOCK_TELEPORTER);
        GameRegistry.registerBlock(blockTankAdapter, Reference.BLOCK_TANK_ADAPTER);
        GameRegistry.registerBlock(blockTank, Reference.BLOCK_TANK);
        GameRegistry.registerBlock(blockTankBevelSmall, Reference.BLOCK_TANK_BEVEL_SMALL);
        GameRegistry.registerBlock(blockTankBevelLarge, Reference.BLOCK_TANK_BEVEL_LARGE);
        GameRegistry.registerBlock(blockTankInteraction, Reference.BLOCK_TANK_INTERACTION);
        GameRegistry.registerBlock(blockTankValve, Reference.BLOCK_TANK_VALVE);

        GameRegistry.registerBlock(blockFusionCase, Reference.BLOCK_FUSION_CASE);
        GameRegistry.registerBlock(blockFusionCaseBevelSmall, Reference.BLOCK_FUSION_CASE_BEVEL_LARGE);
        GameRegistry.registerBlock(blockFusionCaseBevelSmallInverted, Reference.BLOCK_FUSION_CASE_BEVEL_SMALL);
        GameRegistry.registerBlock(blockFusionCaseBevelLarge, Reference.BLOCK_FUSION_CASE_BEVEL_SMALL_INVERTED);
        GameRegistry.registerBlock(blockFusionCaseFluidIO, Reference.BLOCK_FUSION_CASE_FLUID_IO);
        GameRegistry.registerBlock(blockFusionControlCase, Reference.BLOCK_FUSION_CONTROL_CASE);
        GameRegistry.registerBlock(blockFusionControlCore, Reference.BLOCK_FUSION_CONTROL_CORE);
        GameRegistry.registerBlock(blockFusionControlEnergyIO, Reference.BLOCK_FUSION_CONTROL_ENERGY_IO);
        GameRegistry.registerBlock(blockFusionControlItemIO, Reference.BLOCK_FUSION_CONTROL_ITEM_IO);
        GameRegistry.registerBlock(blockFusionControlMaster, Reference.BLOCK_FUSION_CONTROL_MASTER);
        GameRegistry.registerBlock(blockFusionCore, Reference.BLOCK_FUSION_CORE);

        GameRegistry.registerBlock(blockCoolantPipe, Reference.BLOCK_COOLANT_PIPE);
        GameRegistry.registerBlock(blockCoolantInjector, Reference.BLOCK_COOLANT_INJECTOR);
        GameRegistry.registerBlock(blockCoolantExtractor, Reference.BLOCK_COOLANT_EXTRACTOR);

        //TileEntities
        GameRegistry.registerTileEntity(TileSolarOutput.class, Reference.TILE_SOLAR_OUTPUT);
        GameRegistry.registerTileEntity(TileSolarInput.class, Reference.TILE_SOLAR_INPUT);
        GameRegistry.registerTileEntity(TileSolarMaster.class, Reference.TILE_SOLAR_MASTER);

        GameRegistry.registerTileEntity(TileBoilerBaseInput.class, Reference.TILE_BOILER_BASE_INPUT);
        GameRegistry.registerTileEntity(TileBoilerBaseOutput.class, Reference.TILE_BOILER_BASE_OUTPUT);
        GameRegistry.registerTileEntity(TileBoilerBaseMaster.class, Reference.TILE_BOILER_BASE_MASTER);

        GameRegistry.registerTileEntity(TileDistributionEnergy.class, Reference.TILE_DISTRIBUTION_ENERGY);
        GameRegistry.registerTileEntity(TileDistributionFluid.class, Reference.TILE_DISTRIBUTION_FLUID);
        GameRegistry.registerTileEntity(TileDistributionCoolant.class, Reference.TILE_DISTRIBUTION_COOLANT);

        GameRegistry.registerTileEntity(TileTeleporter.class, Reference.TILE_TELEPORTER);
        GameRegistry.registerTileEntity(TileTankAdapter.class, Reference.TILE_TANK_ADAPTER);
        GameRegistry.registerTileEntity(TileTankInteraction.class, Reference.TILE_TANK_INTERACTION);
        GameRegistry.registerTileEntity(TileTankValve.class, Reference.TILE_TANK_VALVE);

        GameRegistry.registerTileEntity(TileFusionCaseFluidIO.class, Reference.TILE_FUSION_CASE_FLUID_IO);
        GameRegistry.registerTileEntity(TileFusionControlEnergyIO.class, Reference.TILE_FUSION_CONTROL_ENERGY_IO);
        GameRegistry.registerTileEntity(TileFusionControlItemIO.class, Reference.TILE_FUSION_CONTROL_ITEM_IO);
        GameRegistry.registerTileEntity(TileFusionControlMaster.class, Reference.TILE_FUSION_CONTROL_MASTER);

        GameRegistry.registerTileEntity(TileCoolantPipe.class, Reference.TILE_COOLANT_PIPE);
        GameRegistry.registerTileEntity(TileCoolantInjector.class, Reference.TILE_COOLANT_INJECTOR);
        GameRegistry.registerTileEntity(TileCoolantExtractor.class, Reference.TILE_COOLANT_EXTRACTOR);

        //Fluids
        registerFluids();

        //Items
        GameRegistry.registerItem(itemControlUnit, Reference.ITEM_CONTROL_UNIT);
        GameRegistry.registerItem(itemSolarPanel, Reference.ITEM_SOLAR_PANEL);
        GameRegistry.registerItem(itemValve, Reference.ITEM_VALVE);
        GameRegistry.registerItem(itemWire, Reference.ITEM_WIRE);
        GameRegistry.registerItem(itemSolarTile, Reference.ITEM_SOLAR_TILE);
        GameRegistry.registerItem(itemLinker, Reference.ITEM_LINKER);
        GameRegistry.registerItem(itemWoodAsh, Reference.ITEM_WOOD_ASH);
        GameRegistry.registerItem(itemPotash, Reference.ITEM_POTASH);
        GameRegistry.registerItem(itemPotassiumHydroxide, Reference.ITEM_POTASSIUM_HYDROXIDE);
        GameRegistry.registerItem(itemGlycerine, Reference.ITEM_GLYCERINE);


        //ComputerCraft Provider
        ComputerCraftAPI.registerPeripheralProvider(RkStuff.blockSolarMaster);
        ComputerCraftAPI.registerPeripheralProvider(RkStuff.blockBoilerBaseMaster);
        ComputerCraftAPI.registerPeripheralProvider(RkStuff.blockDistributionEnergy);
        ComputerCraftAPI.registerPeripheralProvider(RkStuff.blockDistributionFluid);
        ComputerCraftAPI.registerPeripheralProvider(RkStuff.blockDistributionCoolant);

        //Recipes
        registerRecipes();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        guiHandler = new GuiHandler();
        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, guiHandler);

        FMLInterModComms.sendMessage("Waila", "register", "com.rk.rkstuff.core.modinteraction.WailaTileHandler.callbackRegister");
        FMLInterModComms.sendMessage("Waila", "register", "com.rk.rkstuff.core.modinteraction.WailaBlockHandler.callbackRegister");

        PROXY.init();

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        for(Map.Entry<String, Fluid> e : FluidRegistry.getRegisteredFluids().entrySet()) {
            if(e.getValue().getUnlocalizedName().equals("fluid.steam")) {
                FluidHelper.steam = e.getValue();
            }
            if(e.getValue().getUnlocalizedName().equals("fluid.tile.water")) {
                FluidHelper.water = e.getValue();
            }
        }
    }


    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void textureHook(TextureStitchEvent.Post event) {
        if (event.map.getTextureType() == 0) ModelPipe.init();
    }



    private void registerFluids(){
        MinecraftForge.EVENT_BUS.register(BucketHandler.INSTANCE);

        fluidCoolant = new Fluid(Reference.FLUID_COOLANT);
        FluidRegistry.registerFluid(fluidCoolant);
        fluidCoolantBlock = new BlockCoolantFluid(fluidCoolant);
        GameRegistry.registerBlock(fluidCoolantBlock, fluidCoolantBlock.getUnlocalizedName().substring(5));
        fluidCoolant.setUnlocalizedName(fluidCoolantBlock.getUnlocalizedName());
        fluidCoolantBucket = new BucketBase(fluidCoolantBlock, "Bucket" + Reference.FLUID_COOLANT);
        GameRegistry.registerItem(fluidCoolantBucket, "Bucket" + Reference.FLUID_COOLANT);
        FluidContainerRegistry.registerFluidContainer(fluidCoolant, new ItemStack(fluidCoolantBucket), new ItemStack(Items.bucket));
        BucketHandler.INSTANCE.buckets.put(fluidCoolantBlock, fluidCoolantBucket);


        fluidUsedCoolant = new Fluid(Reference.FLUID_USED_COOLANT);
        fluidUsedCoolant.setTemperature(2000);
        FluidRegistry.registerFluid(fluidUsedCoolant);
        fluidUsedCoolantBlock = new BlockUsedCoolantFluid(fluidUsedCoolant);
        GameRegistry.registerBlock(fluidUsedCoolantBlock, fluidUsedCoolantBlock.getUnlocalizedName().substring(5));
        fluidUsedCoolant.setUnlocalizedName(fluidUsedCoolantBlock.getUnlocalizedName());
        fluidUsedCoolantBucket = new BucketBase(fluidUsedCoolantBlock, "Bucket" + Reference.FLUID_USED_COOLANT);
        GameRegistry.registerItem(fluidUsedCoolantBucket, "Bucket" + Reference.FLUID_USED_COOLANT);
        FluidContainerRegistry.registerFluidContainer(fluidUsedCoolant, new ItemStack(fluidUsedCoolantBucket), new ItemStack(Items.bucket));
        BucketHandler.INSTANCE.buckets.put(fluidUsedCoolantBlock, fluidUsedCoolantBucket);
    }

    private void registerRecipes() {
        GameRegistry.addShapedRecipe(new ItemStack(itemValve), " i ", "iii", 'i', Items.iron_ingot);
        GameRegistry.addShapedRecipe(new ItemStack(blockMachineBlock), "iii", "iri", "iii", 'i', Items.iron_ingot, 'r', Items.redstone);
        GameRegistry.addShapedRecipe(new ItemStack(itemControlUnit), "wrw", "rgr", "wrw", 'w', itemWire, 'r', Items.redstone, 'g', Items.gold_ingot);
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemWire, 2), true, new Object[]{"   ", "ccc", "   ", 'c', "ingotCopper"}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemSolarPanel), true, new Object[]{"sts", "tct", "sts", 's', itemSolarTile, 't', "ingotTin", 'c', "ingotCopper"}));
        GameRegistry.addShapelessRecipe(new ItemStack(itemPotash), new ItemStack(itemWoodAsh), new ItemStack(Items.water_bucket.setContainerItem(Items.bucket)));

        //Blockrecipes
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockSolar), true, new Object[]{"sss", "ibi", "iii", 's', itemSolarPanel, 'b', blockMachineBlock, 'i', Items.iron_ingot}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockSolarInput), true, new Object[]{"sss", "vbr", "iii", 's', itemSolarPanel, 'v', itemValve, 'r', Items.redstone, 'b', blockMachineBlock, 'i', Items.iron_ingot}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockSolarOutput), true, new Object[]{"sss", "rbv", "iii", 's', itemSolarPanel, 'v', itemValve, 'r', Items.redstone, 'b', blockMachineBlock, 'i', Items.iron_ingot}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockSolarMaster), true, new Object[]{"sss", "cbc", "iii", 's', itemSolarPanel, 'c', itemControlUnit, 'b', blockMachineBlock, 'i', Items.iron_ingot}));

        //MachineRecipes
        cofh.api.modhelpers.ThermalExpansionHelper.addSmelterRecipe(1600, new ItemStack(Items.redstone), new ItemStack(Items.dye, 1, 4), new ItemStack(itemSolarTile, 2));
        GameRegistry.addSmelting(new ItemStack(Blocks.planks), new ItemStack(itemWoodAsh), 0.1f);
        cofh.api.modhelpers.ThermalExpansionHelper.addSmelterRecipe(1600, new ItemStack(Blocks.sand, 1, 1), new ItemStack(Items.clay_ball), new ItemStack(blockPortlandite, 2));
        cofh.api.modhelpers.ThermalExpansionHelper.addInsolatorRecipe(1600, new ItemStack(itemPotash), new ItemStack(blockPortlandite), new ItemStack(itemPotassiumHydroxide));
        cofh.api.modhelpers.ThermalExpansionHelper.addSmelterRecipe(1600, new ItemStack(itemPotassiumHydroxide), new ItemStack(Items.leather), new ItemStack(itemGlycerine));
        cofh.api.modhelpers.ThermalExpansionHelper.addCrucibleRecipe(1600, new ItemStack(itemGlycerine), new FluidStack(fluidCoolant, 1000));
    }
}
