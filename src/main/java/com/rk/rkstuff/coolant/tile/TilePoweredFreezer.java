package com.rk.rkstuff.coolant.tile;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.coolant.CoolantStack;
import com.rk.rkstuff.core.modinteraction.IWailaBodyProvider;
import com.rk.rkstuff.core.tile.TileRKReconfigurable;
import com.rk.rkstuff.helper.FluidHelper;
import com.rk.rkstuff.network.message.IGuiActionMessage;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import rk.com.core.io.IOStream;

import java.io.IOException;
import java.util.List;

public class TilePoweredFreezer extends TileRKReconfigurable implements ICoolantReceiver, IEnergyReceiver, IWailaBodyProvider, IGuiActionMessage {
    public static byte[] defaultConfig = {2, 1, 0, 0, 0, 0};

    private static float TEMPERATURE_ENERGY_PER_RF = 2.0f;

    private static int MAX_COOLANT_IO = 200;
    private static int MAX_COOLANT_STORAGE = 1000;

    private static int MAX_RF_IO = 300;
    private static int MAX_RF_USAGE = 200;
    private static int MAX_RK_STORAGE = 2000;

    private static byte SIDE_DISABLED = 0;
    private static byte SIDE_COOLANT_INPUT = 1;
    private static byte SIDE_COOLANT_OUTPUT = 2;

    private CoolantStack coolantStack = new CoolantStack();
    private EnergyStorage energyStorage = new EnergyStorage(MAX_RK_STORAGE, MAX_RF_IO);
    private float targetTemp = 20.0f;

    public TilePoweredFreezer() {
        super(RkStuff.blockPoweredFreezer);
        setUpdateInterval(10);
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    protected boolean cacheNeighbours() {
        return true;
    }

    @Override
    public void readData(IOStream data) throws IOException {
        super.readData(data);
        coolantStack.readData(data);
        energyStorage.setEnergyStored(data.readFirstInt());
        targetTemp = data.readFirstFloat();
    }

    @Override
    public void writeData(IOStream data) {
        super.writeData(data);
        coolantStack.writeData(data);
        data.writeLast(energyStorage.getEnergyStored());
        data.writeLast(targetTemp);
    }

    @Override
    protected byte[] getDefaultSideConfig() {
        return defaultConfig;
    }

    @Override
    public int getNumConfig(int i) {
        return 3;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (worldObj.isRemote) return;

        if (coolantStack.getAmount() > 0 && coolantStack.getTemperature() > targetTemp) {
            int rfUsage = Math.min(MAX_RF_USAGE, energyStorage.getEnergyStored());
            energyStorage.setEnergyStored(energyStorage.getEnergyStored() - rfUsage);
            float negativeEnergy = rfUsage * TEMPERATURE_ENERGY_PER_RF;
            coolantStack.extractEnergy(negativeEnergy);
        }

        int max = Math.min(MAX_COOLANT_IO, coolantStack.getAmount());
        if (max > 0) {
            coolantStack.remove(FluidHelper.outputCoolantToNeighbours(neighbours, config, SIDE_COOLANT_OUTPUT, max, coolantStack.getTemperature()));
        }
    }

    @Override
    public int receiveCoolant(ForgeDirection from, int maxAmount, float temperature, boolean simulate) {
        if (config[from.ordinal()] != SIDE_COOLANT_INPUT) return 0;
        maxAmount = Math.min(maxAmount, MAX_COOLANT_IO + 50);
        maxAmount = Math.min(maxAmount, MAX_COOLANT_STORAGE - coolantStack.getAmount());
        if (!simulate) {
            coolantStack.add(maxAmount, temperature);
        }
        return maxAmount;
    }

    @Override
    public boolean canConnect(ForgeDirection from) {
        return config[from.ordinal()] == SIDE_COOLANT_INPUT ||
                config[from.ordinal()] == SIDE_COOLANT_OUTPUT;
    }

    @Override
    public int receiveEnergy(ForgeDirection forgeDirection, int maxEnergy, boolean simulate) {
        return energyStorage.receiveEnergy(maxEnergy, simulate);
    }

    @Override
    public int getEnergyStored(ForgeDirection forgeDirection) {
        return energyStorage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection forgeDirection) {
        return MAX_RK_STORAGE;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection forgeDirection) {
        return true;
    }

    public int getMaxCoolantStorage() {
        return MAX_COOLANT_STORAGE;
    }

    public CoolantStack getCoolantStack() {
        return coolantStack;
    }

    public EnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public float getTargetTemp() {
        return targetTemp;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentBody, IWailaDataAccessor accessor, IWailaConfigHandler configHandler) {
        currentBody.clear();
        currentBody.add(String.format("Energy: %d/%d RF", energyStorage.getEnergyStored(), MAX_RK_STORAGE));
        currentBody.add(String.format("Coolant: %d/%d mB", coolantStack.getAmount(), MAX_COOLANT_STORAGE));
        currentBody.add(String.format("Temperature: %s", coolantStack.getFormattedString()));
        return currentBody;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        coolantStack.writeToNBT("coolant", data);
        data.setInteger("energy", energyStorage.getEnergyStored());
        data.setFloat("targetTemp", targetTemp);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        coolantStack.readFromNBT("coolant", data);
        energyStorage.setEnergyStored(data.getInteger("energy"));
        targetTemp = data.getFloat("targetTemp");
    }


    @Override
    public void receiveGuiAction(IOStream data) throws IOException {
        int id = data.readFirstInt();
        if (id == 0) {
            targetTemp = data.readFirstFloat();
        }
    }
}
