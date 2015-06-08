package com.rk.rkstuff;

import com.rk.rkstuff.block.*;
import com.rk.rkstuff.block.fluid.BlockCoolCoolantFluid;
import com.rk.rkstuff.block.fluid.BlockHotCoolantFluid;
import com.rk.rkstuff.block.fusion.*;
import com.rk.rkstuff.client.gui.GuiHandler;
import com.rk.rkstuff.handler.BucketHandler;
import com.rk.rkstuff.helper.FluidHelper;
import com.rk.rkstuff.item.BucketBase;
import com.rk.rkstuff.item.ItemLinker;
import com.rk.rkstuff.item.ItemRK;
import com.rk.rkstuff.network.PacketHandler;
import com.rk.rkstuff.proxy.IProxy;
import com.rk.rkstuff.tile.*;
import com.rk.rkstuff.tile.fusion.TileFusionCaseFluidIO;
import com.rk.rkstuff.tile.fusion.TileFusionControlEnergyIO;
import com.rk.rkstuff.tile.fusion.TileFusionControlItemIO;
import com.rk.rkstuff.tile.fusion.TileFusionControlMaster;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import dan200.computercraft.api.ComputerCraftAPI;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.Map;

@Mod(modid = Reference.MOD_ID, version = Reference.VERSION, name = Reference.MOD_NAME)
public class RkStuff {
    
    public static Fluid coolCoolant;
    public static Block coolCoolantBlock;
    public static ItemBucket coolCoolantBucket;
    public static Fluid hotCoolant;
    public static Block hotCoolantBlock;
    public static ItemBucket hotCoolantBucket;

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

    public static BlockDistributionEnergy blockEnergyDistribution = new BlockDistributionEnergy();
    public static BlockDistributionFluid blockFluidDistribution = new BlockDistributionFluid();

    public static Block blockTeleporter = new BlockTeleporter();

    public static Block blockTankAdapter = new BlockTankAdapter();

    public static Block blockModelTest = new BlockModelTest();

    public static Block blockFusionCase = new BlockFusionCase();
    public static Block blockFusionCase1 = new BlockFusionCase1();
    public static Block blockFusionCase2 = new BlockFusionCase2();
    public static Block blockFusionCase3 = new BlockFusionCase3();
    public static Block blockFusionCaseFluidIO = new BlockFusionCaseFluidIO();
    public static Block blockFusionControlCase = new BlockFusionControlCase();
    public static Block blockFusionControlCore = new BlockFusionControlCore();
    public static Block blockFusionControlEnergyIO = new BlockFusionControlEnergyIO();
    public static Block blockFusionControlItemIO = new BlockFusionControlItemIO();
    public static Block blockFusionControlMaster = new BlockFusionControlMaster();
    public static Block blockFusionCore = new BlockFusionCore();

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

        GameRegistry.registerBlock(blockEnergyDistribution, Reference.BLOCK_DISTRIBUTION_ENERGY);
        GameRegistry.registerBlock(blockFluidDistribution, Reference.BLOCK_DISTRIBUTION_FLUID);

        GameRegistry.registerBlock(blockTeleporter, Reference.BLOCK_TELEPORTER);
        GameRegistry.registerBlock(blockModelTest, Reference.BLOCK_MODEL_TEST);
        GameRegistry.registerBlock(blockTankAdapter, Reference.BLOCK_TANK_ADAPTER);

        GameRegistry.registerBlock(blockFusionCase, Reference.BLOCK_FUSION_CASE);
        GameRegistry.registerBlock(blockFusionCase1, Reference.BLOCK_FUSION_CASE_BEVEL_LARGE);
        GameRegistry.registerBlock(blockFusionCase2, Reference.BLOCK_FUSION_CASE_BEVEL_SMALL);
        GameRegistry.registerBlock(blockFusionCase3, Reference.BLOCK_FUSION_CASE_BEVEL_SMALL_INVERTED);
        GameRegistry.registerBlock(blockFusionCaseFluidIO, Reference.BLOCK_FUSION_CASE_FLUID_IO);
        GameRegistry.registerBlock(blockFusionControlCase, Reference.BLOCK_FUSION_CONTROL_CASE);
        GameRegistry.registerBlock(blockFusionControlCore, Reference.BLOCK_FUSION_CONTROL_CORE);
        GameRegistry.registerBlock(blockFusionControlEnergyIO, Reference.BLOCK_FUSION_CONTROL_ENERGY_IO);
        GameRegistry.registerBlock(blockFusionControlItemIO, Reference.BLOCK_FUSION_CONTROL_ITEM_IO);
        GameRegistry.registerBlock(blockFusionControlMaster, Reference.BLOCK_FUSION_CONTROL_MASTER);
        GameRegistry.registerBlock(blockFusionCore, Reference.BLOCK_FUSION_CORE);

        //TileEntities
        GameRegistry.registerTileEntity(TileSolarOutput.class, Reference.TILE_SOLAR_OUTPUT);
        GameRegistry.registerTileEntity(TileSolarInput.class, Reference.TILE_SOLAR_INPUT);
        GameRegistry.registerTileEntity(TileSolarMaster.class, Reference.TILE_SOLAR_MASTER);

        GameRegistry.registerTileEntity(TileBoilerBaseInput.class, Reference.TILE_BOILER_BASE_INPUT);
        GameRegistry.registerTileEntity(TileBoilerBaseOutput.class, Reference.TILE_BOILER_BASE_OUTPUT);
        GameRegistry.registerTileEntity(TileBoilerBaseMaster.class, Reference.TILE_BOILER_BASE_MASTER);

        GameRegistry.registerTileEntity(TileDistributionEnergy.class, Reference.TILE_DISTRIBUTION_ENERGY);
        GameRegistry.registerTileEntity(TileDistributionFluid.class, Reference.TILE_DISTRIBUTION_FLUID);

        GameRegistry.registerTileEntity(TileTeleporter.class, Reference.TILE_TELEPORTER);
        GameRegistry.registerTileEntity(TileTankAdapter.class, Reference.TILE_TANK_ADAPTER);
        GameRegistry.registerTileEntity(TileModelTest.class, Reference.TILE_MODEL_TEST);

        GameRegistry.registerTileEntity(TileFusionCaseFluidIO.class, Reference.TILE_FUSION_CASE_FLUID_IO);
        GameRegistry.registerTileEntity(TileFusionControlEnergyIO.class, Reference.TILE_FUSION_CONTROL_ENERGY_IO);
        GameRegistry.registerTileEntity(TileFusionControlItemIO.class, Reference.TILE_FUSION_CONTROL_ITEM_IO);
        GameRegistry.registerTileEntity(TileFusionControlMaster.class, Reference.TILE_FUSION_CONTROL_MASTER);

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
        ComputerCraftAPI.registerPeripheralProvider(RkStuff.blockEnergyDistribution);
        ComputerCraftAPI.registerPeripheralProvider(RkStuff.blockFluidDistribution);

        //Recipes
        registerRecipes();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

        guiHandler = new GuiHandler();
        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, guiHandler);

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



    private void registerFluids(){
        MinecraftForge.EVENT_BUS.register(BucketHandler.INSTANCE);

        coolCoolant = new Fluid(Reference.FLUID_COOL_COOLANT);
        FluidRegistry.registerFluid(coolCoolant);
        coolCoolantBlock = new BlockCoolCoolantFluid(coolCoolant);
        GameRegistry.registerBlock(coolCoolantBlock, coolCoolantBlock.getUnlocalizedName().substring(5));
        coolCoolant.setUnlocalizedName(coolCoolantBlock.getUnlocalizedName());
        coolCoolantBucket = new BucketBase(coolCoolantBlock, "Bucket" + Reference.FLUID_COOL_COOLANT);
        GameRegistry.registerItem(coolCoolantBucket, "Bucket" + Reference.FLUID_COOL_COOLANT);
        FluidContainerRegistry.registerFluidContainer(coolCoolant, new ItemStack(coolCoolantBucket), new ItemStack(Items.bucket));
        BucketHandler.INSTANCE.buckets.put(coolCoolantBlock, coolCoolantBucket);


        hotCoolant = new Fluid(Reference.FLUID_HOT_COOLANT);
        hotCoolant.setTemperature(2000);
        FluidRegistry.registerFluid(hotCoolant);
        hotCoolantBlock = new BlockHotCoolantFluid(hotCoolant);
        GameRegistry.registerBlock(hotCoolantBlock, hotCoolantBlock.getUnlocalizedName().substring(5));
        hotCoolant.setUnlocalizedName(hotCoolantBlock.getUnlocalizedName());
        hotCoolantBucket = new BucketBase(hotCoolantBlock, "Bucket" + Reference.FLUID_HOT_COOLANT);
        GameRegistry.registerItem(hotCoolantBucket, "Bucket" + Reference.FLUID_HOT_COOLANT);
        FluidContainerRegistry.registerFluidContainer(hotCoolant, new ItemStack(hotCoolantBucket), new ItemStack(Items.bucket));
        BucketHandler.INSTANCE.buckets.put(hotCoolantBlock, hotCoolantBucket);
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
        cofh.api.modhelpers.ThermalExpansionHelper.addCrucibleRecipe(1600, new ItemStack(itemGlycerine), new FluidStack(coolCoolant, 1000));
    }
}
