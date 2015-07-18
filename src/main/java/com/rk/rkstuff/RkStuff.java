package com.rk.rkstuff;

import com.rk.rkstuff.accelerator.LHCRecipeRegistry;
import com.rk.rkstuff.accelerator.block.*;
import com.rk.rkstuff.accelerator.tile.*;
import com.rk.rkstuff.boiler.block.*;
import com.rk.rkstuff.boiler.tile.TileBoilerBaseInput;
import com.rk.rkstuff.boiler.tile.TileBoilerBaseMaster;
import com.rk.rkstuff.boiler.tile.TileBoilerBaseOutput;
import com.rk.rkstuff.client.gui.GuiHandler;
import com.rk.rkstuff.client.model.ModelPipe;
import com.rk.rkstuff.coolant.block.*;
import com.rk.rkstuff.coolant.tile.*;
import com.rk.rkstuff.core.block.BlockRK;
import com.rk.rkstuff.core.block.BlockRKReconfigurable;
import com.rk.rkstuff.distribution.block.BlockDistributionCoolant;
import com.rk.rkstuff.distribution.block.BlockDistributionEnergy;
import com.rk.rkstuff.distribution.block.BlockDistributionFluid;
import com.rk.rkstuff.distribution.tile.TileDistributionCoolant;
import com.rk.rkstuff.distribution.tile.TileDistributionEnergy;
import com.rk.rkstuff.distribution.tile.TileDistributionFluid;
import com.rk.rkstuff.handler.BucketHandler;
import com.rk.rkstuff.helper.FluidHelper;
import com.rk.rkstuff.item.BucketBase;
import com.rk.rkstuff.item.ItemBlockAcceleratorControlCore;
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
import com.rk.rkstuff.util.RKConfig;
import com.rk.rkstuff.util.RKLog;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.common.Loader;
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
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.lang.reflect.Method;
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

    public static BlockRKReconfigurable blockPoweredFreezer = new BlockPoweredFreezer();

    public static BlockRKReconfigurable blockCoolantMixer = new BlockCoolantMixer();

    public static Block blockAcceleratorCase = new BlockAcceleratorCase();
    public static Block blockAcceleratorCaseBevelSmall = new BlockAcceleratorCaseBevelSmall();
    public static Block blockAcceleratorCaseBevelSmallInverted = new BlockAcceleratorCaseBevelSmallInverted();
    public static Block blockAcceleratorCaseBevelLarge = new BlockAcceleratorCaseBevelLarge();
    public static Block blockAcceleratorCaseFluidIO = new BlockAcceleratorCaseFluidIO();
    public static Block blockAcceleratorControlCase = new BlockAcceleratorControlCase();
    public static Block blockAcceleratorControlCore = new BlockAcceleratorControlCore();
    public static Block blockAcceleratorControlEnergyIO = new BlockAcceleratorControlEnergyIO();
    public static Block blockAcceleratorControlItemIO = new BlockAcceleratorControlItemIO();
    public static Block blockAcceleratorCore = new BlockAcceleratorCore();
    public static Block blockLHCMaster = new BlockLHCMaster();
    public static Block blockFusionReactorMaster = new BlockFusionReactorMaster();

    public static Block blockCoolantPipe = new BlockCoolantPipe();
    public static Block blockCoolantInjector = new BlockCoolantInjector();
    public static Block blockCoolantExtractor = new BlockCoolantExtractor();
    public static BlockHeatExchanger blockHeatExchanger = new BlockHeatExchanger();
    public static BlockRKReconfigurable blockHeatPump = new BlockHeatPump();

    public static Item itemValve = new ItemRK(Reference.ITEM_VALVE);
    public static Item itemSolarPanel = new ItemRK(Reference.ITEM_SOLAR_PANEL);
    public static Item itemControlUnit = new ItemRK(Reference.ITEM_CONTROL_UNIT);
    public static Item itemWire = new ItemRK(Reference.ITEM_WIRE);
    public static Item itemSolarTile = new ItemRK(Reference.ITEM_SOLAR_TILE);
    public static Item itemWoodAsh = new ItemRK(Reference.ITEM_WOOD_ASH);
    public static Item itemPotash = new ItemRK(Reference.ITEM_POTASH);
    public static Item itemPotassiumHydroxide = new ItemRK(Reference.ITEM_POTASSIUM_HYDROXIDE);
    public static Item itemGlycerine = new ItemRK(Reference.ITEM_GLYCERINE);
    public static Item itemMixer = new ItemRK(Reference.ITEM_MIXER);
    public static Item itemMixerBlade = new ItemRK(Reference.ITEM_MIXER_BLADE);

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
        RKConfig.init(new Configuration(event.getSuggestedConfigurationFile()));


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

        GameRegistry.registerBlock(blockPoweredFreezer, Reference.BLOCK_POWERED_FREEZER);
        GameRegistry.registerTileEntity(TilePoweredFreezer.class, Reference.TILE_POWERED_FREEZER);

        GameRegistry.registerBlock(blockCoolantMixer, Reference.BLOCK_COOLANT_MIXER);
        GameRegistry.registerTileEntity(TileCoolantMixer.class, Reference.TILE_COOLANT_MIXER);

        GameRegistry.registerBlock(blockAcceleratorCase, Reference.BLOCK_ACCELERATOR_CASE);
        GameRegistry.registerBlock(blockAcceleratorCaseBevelSmall, Reference.BLOCK_ACCELERATOR_CASE_BEVEL_LARGE);
        GameRegistry.registerBlock(blockAcceleratorCaseBevelSmallInverted, Reference.BLOCK_ACCELERATOR_CASE_BEVEL_SMALL);
        GameRegistry.registerBlock(blockAcceleratorCaseBevelLarge, Reference.BLOCK_ACCELERATOR_CASE_BEVEL_SMALL_INVERTED);
        GameRegistry.registerBlock(blockAcceleratorCaseFluidIO, Reference.BLOCK_ACCELERATOR_CASE_FLUID_IO);
        GameRegistry.registerBlock(blockAcceleratorControlCase, Reference.BLOCK_ACCELERATOR_CONTROL_CASE);
        GameRegistry.registerBlock(blockAcceleratorControlCore, ItemBlockAcceleratorControlCore.class, Reference.BLOCK_ACCELERATOR_CONTROL_CORE);
        GameRegistry.registerBlock(blockAcceleratorControlEnergyIO, Reference.BLOCK_ACCELERATOR_CONTROL_ENERGY_IO);
        GameRegistry.registerBlock(blockAcceleratorControlItemIO, Reference.BLOCK_ACCELERATOR_CONTROL_ITEM_IO);
        GameRegistry.registerBlock(blockLHCMaster, Reference.BLOCK_LHC_MASTER);
        GameRegistry.registerBlock(blockFusionReactorMaster, Reference.BLOCK_FUSION_REACTOR_MASTER);
        GameRegistry.registerBlock(blockAcceleratorCore, Reference.BLOCK_ACCELERATOR_CORE);

        GameRegistry.registerBlock(blockCoolantPipe, Reference.BLOCK_COOLANT_PIPE);
        GameRegistry.registerBlock(blockCoolantInjector, Reference.BLOCK_COOLANT_INJECTOR);
        GameRegistry.registerBlock(blockCoolantExtractor, Reference.BLOCK_COOLANT_EXTRACTOR);
        GameRegistry.registerBlock(blockHeatExchanger, Reference.BLOCK_HEAT_EXCHANGER);
        GameRegistry.registerBlock(blockHeatPump, Reference.BLOCK_HEAT_PUMP);


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

        GameRegistry.registerTileEntity(TileAcceleratorCaseFluidIO.class, Reference.TILE_ACCELERATOR_CASE_FLUID_IO);
        GameRegistry.registerTileEntity(TileAcceleratorControlEnergyIO.class, Reference.TILE_ACCELERATOR_CONTROL_ENERGY_IO);
        GameRegistry.registerTileEntity(TileAcceleratorControlItemIO.class, Reference.TILE_ACCELERATOR_CONTROL_ITEM_IO);
        GameRegistry.registerTileEntity(TileLHCMaster.class, Reference.TILE_LHC_MASTER);
        GameRegistry.registerTileEntity(TileFusionReactorMaster.class, Reference.TILE_FUSION_REACTOR_MASTER);

        GameRegistry.registerTileEntity(TileCoolantPipe.class, Reference.TILE_COOLANT_PIPE);
        GameRegistry.registerTileEntity(TileCoolantInjector.class, Reference.TILE_COOLANT_INJECTOR);
        GameRegistry.registerTileEntity(TileCoolantExtractor.class, Reference.TILE_COOLANT_EXTRACTOR);
        GameRegistry.registerTileEntity(TileHeatExchanger.class, Reference.TILE_HEAT_EXCHANGER);
        GameRegistry.registerTileEntity(TileHeatPump.class, Reference.TILE_HEAT_PUMP);

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
        GameRegistry.registerItem(itemMixer, Reference.ITEM_MIXER);
        GameRegistry.registerItem(itemMixerBlade, Reference.ITEM_MIXER_BLADE);


        //ComputerCraft Provider
        ComputerCraftAPI.registerPeripheralProvider(RkStuff.blockSolarMaster);
        ComputerCraftAPI.registerPeripheralProvider(RkStuff.blockBoilerBaseMaster);
        ComputerCraftAPI.registerPeripheralProvider(RkStuff.blockDistributionEnergy);
        ComputerCraftAPI.registerPeripheralProvider(RkStuff.blockDistributionFluid);
        ComputerCraftAPI.registerPeripheralProvider(RkStuff.blockDistributionCoolant);

        //Recipes
        registerRecipes();

        try {
            if (Loader.isModLoaded("BuildCraft|Transport")) {
                Class clazz = Class.forName("buildcraft.transport.ItemFacade");
                Method blacklistFacade = clazz.getDeclaredMethod("blacklistFacade", String.class);
                blacklistFacade.invoke(clazz, Block.blockRegistry.getNameForObject(RkStuff.blockTankBevelSmall));
                blacklistFacade.invoke(clazz, Block.blockRegistry.getNameForObject(RkStuff.blockTankBevelLarge));
                blacklistFacade.invoke(clazz, Block.blockRegistry.getNameForObject(RkStuff.blockAcceleratorCaseBevelSmall));
                blacklistFacade.invoke(clazz, Block.blockRegistry.getNameForObject(RkStuff.blockAcceleratorCaseBevelSmallInverted));
                blacklistFacade.invoke(clazz, Block.blockRegistry.getNameForObject(RkStuff.blockAcceleratorCaseBevelLarge));
            }
        } catch (Exception e) {
            RKLog.error(e);
        }
        RKConfig.save();
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

    @EventHandler
    public void imcCallback(FMLInterModComms.IMCEvent event) {
        for (final FMLInterModComms.IMCMessage imcMessage : event.getMessages()) {
            if (imcMessage.key.equalsIgnoreCase("addLHCRecipe") && imcMessage.isNBTMessage()) {
                LHCRecipeRegistry.addRecipe(imcMessage.getNBTValue());
            }
        }
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
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemValve), " i ", "iii", 'i', "ingotIron"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockMachineBlock), "iii", "iri", "iii", 'i', "ingotIron", 'r', "dustRedstone"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemControlUnit), "wrw", "rgr", "wrw", 'w', itemWire, 'r', "dustRedstone", 'g', "ingotGold"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemWire, 2), true, new Object[]{"   ", "ccc", "   ", 'c', "ingotCopper"}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemSolarPanel), true, new Object[]{"sts", "tct", "sts", 's', itemSolarTile, 't', "ingotTin", 'c', "ingotCopper"}));
        GameRegistry.addShapelessRecipe(new ItemStack(itemPotash), new ItemStack(itemWoodAsh), new ItemStack(Items.water_bucket.setContainerItem(Items.bucket)));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockSolar), true, new Object[]{"sss", "ibi", "iii", 's', itemSolarPanel, 'b', blockMachineBlock, 'i', "ingotIron"}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockSolarInput), true, new Object[]{"sss", "vbr", "iii", 's', itemSolarPanel, 'v', itemValve, 'r', "dustRedstone", 'b', blockMachineBlock, 'i', "ingotIron"}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockSolarOutput), true, new Object[]{"sss", "rbv", "iii", 's', itemSolarPanel, 'v', itemValve, 'r', "dustRedstone", 'b', blockMachineBlock, 'i', "ingotIron"}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockSolarMaster), true, new Object[]{"sss", "cbc", "iii", 's', itemSolarPanel, 'c', itemControlUnit, 'b', blockMachineBlock, 'i', "ingotIron"}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockDistributionEnergy), true, new Object[]{" i ", "cbc", "iii", 'c', itemControlUnit, 'b', blockMachineBlock, 'i', "dustRedstone"}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockDistributionCoolant), true, new Object[]{" x ", "cbc", "iii", 'x', blockCoolantPipe, 'c', itemControlUnit, 'b', blockMachineBlock, 'i', "dustRedstone"}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockDistributionFluid), true, new Object[]{" x ", "cbc", "iii", 'x', Items.bucket, 'c', itemControlUnit, 'b', blockMachineBlock, 'i', "dustRedstone"}));


        cofh.api.modhelpers.ThermalExpansionHelper.addSmelterRecipe(1600, new ItemStack(Items.redstone), new ItemStack(Items.dye, 1, 4), new ItemStack(itemSolarTile, 2));
        for (ItemStack item : OreDictionary.getOres("plankWood")) {
            GameRegistry.addSmelting(item, new ItemStack(itemWoodAsh), 0.1f);
        }

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockCoolantPipe, 16), true, new Object[]{"iii", "ccc", "iii", 'i', "ingotIron", 'c', fluidCoolantBucket}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockBoilerTank, 4), true, new Object[]{"iii", "i i", "iii", 'i', "ingotTin"}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockBoilerBase), true, new Object[]{"   ", "ixi", "iii", 'i', "ingotIron", 'x', blockBoilerTank}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockBoilerBaseMaster), true, new Object[]{"   ", " x ", " i ", 'i', blockBoilerBase, 'x', itemControlUnit}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockBoilerBaseInput), true, new Object[]{"   ", "ix ", "   ", 'i', blockBoilerBase, 'x', itemValve}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockBoilerBaseOutput), true, new Object[]{"   ", " xi", "   ", 'i', blockBoilerBase, 'x', itemValve}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockTeleporter), true, new Object[]{" e ", "exe", " e ", 'e', Items.ender_pearl, 'x', blockMachineBlock}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemLinker), true, new Object[]{"   ", " p ", " s ", 'p', Items.ender_pearl, 's', "stickWood"}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockTank, 4), true, new Object[]{"gig", "i i", "gig", 'g', Blocks.glass, 'i', "ingotIron"}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockTankAdapter), true, new Object[]{"   ", " i ", " x ", 'i', itemControlUnit, 'x', blockTank}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockTankValve), true, new Object[]{"   ", "xix", "   ", 'i', blockTank, 'x', itemValve}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockTankInteraction), true, new Object[]{"   ", " i ", " x ", 'i', itemControlUnit, 'x', blockTank}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemMixerBlade), true, new Object[]{"   ", "itt", "   ", 'i', "ingotIron", 't', "ingotTin"}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemMixer), true, new Object[]{" x ", "x x", " x ", 'x', itemMixerBlade}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockCoolantMixer), true, new Object[]{" c ", "umu", " i ", 'c', fluidCoolantBucket, 'm', blockMachineBlock, 'i', itemMixer, 'u', itemControlUnit}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockCoolantInjector), true, new Object[]{" h ", " p ", "   ", 'h', Blocks.hopper, 'p', blockCoolantPipe}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockCoolantExtractor), true, new Object[]{"   ", " p ", " h ", 'h', Blocks.hopper, 'p', blockCoolantPipe}));

        cofh.api.modhelpers.ThermalExpansionHelper.addSmelterRecipe(1600, new ItemStack(Blocks.sand, 1, 1), new ItemStack(Items.clay_ball), new ItemStack(blockPortlandite, 2));
        cofh.api.modhelpers.ThermalExpansionHelper.addInsolatorRecipe(1600, new ItemStack(itemPotash), new ItemStack(blockPortlandite), new ItemStack(itemPotassiumHydroxide));
        cofh.api.modhelpers.ThermalExpansionHelper.addSmelterRecipe(1600, new ItemStack(itemPotassiumHydroxide), new ItemStack(Items.leather), new ItemStack(itemGlycerine));
        cofh.api.modhelpers.ThermalExpansionHelper.addCrucibleRecipe(1600, new ItemStack(itemGlycerine), new FluidStack(fluidCoolant, 1000));

        /* =============== ACCELERATOR RECIPES =============== */
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockAcceleratorCore, 2), true, new Object[]{"ici", "geg", "ici", 'i', "ingotGold", 'c', itemControlUnit, 'g', Blocks.glass, 'e', Items.ender_pearl}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockAcceleratorCase, 4), true, new Object[]{"igi", "gcg", "igi", 'i', "ingotIron", 'c', fluidCoolantBucket, 'g', Blocks.glass}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockAcceleratorCaseFluidIO, 1), true, new Object[]{"ivi", "vcv", "ivi", 'i', "ingotIron", 'c', fluidCoolantBucket, 'v', itemValve}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockAcceleratorControlCase, 2, 0), true, new Object[]{"iri", "rxr", "iri", 'i', "ingotIron", 'r', Items.redstone, 'x', Blocks.glass}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockAcceleratorControlEnergyIO, 2, 0), true, new Object[]{"ivi", "vxv", "ivi", 'i', "ingotIron", 'v', itemValve, 'x', Blocks.redstone_block}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockAcceleratorControlItemIO, 2, 0), true, new Object[]{"ivi", "vxv", "ivi", 'i', "ingotIron", 'v', itemValve, 'x', Blocks.chest}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockAcceleratorControlCore, 2, 0), true, new Object[]{"ici", "cxc", "ici", 'i', "ingotIron", 'c', itemControlUnit, 'x', Blocks.glass}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockAcceleratorControlCore, 2, 1), true, new Object[]{"ici", "cxc", "ici", 'i', "ingotIron", 'c', itemControlUnit, 'x', Blocks.redstone_block}));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockAcceleratorControlCore, 2, 2), true, new Object[]{"ici", "cxc", "ici", 'i', "ingotIron", 'c', itemControlUnit, 'x', Blocks.lapis_block}));

        /* =============== LHC RECIPES =============== */
        // ~21k RF
        if (OreDictionary.doesOreNameExist("ingotTin") && OreDictionary.doesOreNameExist("oreTin")) {
            ItemStack ingot = OreDictionary.getOres("ingotTin").get(0);
            ingot.stackSize = 4;
            for (ItemStack ore : OreDictionary.getOres("oreTin")) {
                LHCRecipeRegistry.addRecipe(ingot.copy(), 40.0f, 0.025f, ore);
            }
        }

        // ~21k RF
        if (OreDictionary.doesOreNameExist("ingotCopper") && OreDictionary.doesOreNameExist("oreCopper")) {
            ItemStack ingot = OreDictionary.getOres("ingotCopper").get(0);
            ingot.stackSize = 4;
            for (ItemStack ore : OreDictionary.getOres("oreCopper")) {
                LHCRecipeRegistry.addRecipe(ingot.copy(), 40.0f, 0.025f, ore);
            }
        }

        // ~21k RF
        if (OreDictionary.doesOreNameExist("ingotIron") && OreDictionary.doesOreNameExist("oreIron")) {
            ItemStack ingot = OreDictionary.getOres("ingotIron").get(0);
            ingot.stackSize = 4;
            for (ItemStack ore : OreDictionary.getOres("oreIron")) {
                LHCRecipeRegistry.addRecipe(ingot.copy(), 40.0f, 0.025f, ore);
            }
        }

        // ~42k RF
        LHCRecipeRegistry.addRecipe(new ItemStack(Items.redstone, 4), 40.0f, 0.05f, new ItemStack(Items.redstone, 16));

        //~150k RF
        if (OreDictionary.doesOreNameExist("ingotGold") && OreDictionary.doesOreNameExist("oreGold")) {
            ItemStack ingot = OreDictionary.getOres("ingotGold").get(0);
            ingot.stackSize = 4;
            for (ItemStack ore : OreDictionary.getOres("oreGold")) {
                LHCRecipeRegistry.addRecipe(ingot.copy(), 108.0f, 0.025f, ore);
            }
        }

        //~150k RF
        LHCRecipeRegistry.addRecipe(new ItemStack(Items.glowstone_dust, 12), 108.0f, 0.025f, new ItemStack(Items.glowstone_dust, 4));

        //~150k RF
        LHCRecipeRegistry.addRecipe(new ItemStack(Items.dye, 12, 4), 108.0f, 0.025f, new ItemStack(Items.dye, 4, 4));

        //~400k RF
        LHCRecipeRegistry.addRecipe(new ItemStack(Items.diamond, 2), 180.0f, 0.025f, new ItemStack(Items.diamond, 1));

        //~800k RF
        LHCRecipeRegistry.addRecipe(new ItemStack(Items.emerald, 2), 180.0f, 0.05f, new ItemStack(Items.emerald, 1));

        //most expensive recipe (atm.). Requires max LHC and about 8 Million RF @7.6k RF per Tick for ~1000 Ticks.
        LHCRecipeRegistry.addRecipe(new ItemStack(Items.nether_star, 2), 300.0f, 0.175f, new ItemStack(Items.nether_star));
    }
}
