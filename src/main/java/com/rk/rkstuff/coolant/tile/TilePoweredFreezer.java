package com.rk.rkstuff.coolant.tile;

import cofh.api.energy.IEnergyReceiver;
import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.coolant.CoolantStack;
import com.rk.rkstuff.core.modinteraction.IWailaBodyProvider;
import com.rk.rkstuff.core.tile.TileRKReconfigurable;
import com.rk.rkstuff.helper.FluidHelper;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import rk.com.core.io.IOStream;

import java.io.IOException;
import java.util.List;

public class TilePoweredFreezer extends TileRKReconfigurable implements ICoolantReceiver, IEnergyReceiver, IWailaBodyProvider {
    public static byte[] defaultConfig = {2, 1, 0, 0, 0, 0};

    private static float TEMPERATURE_ENERGY_PER_RF = 2.0f;

    private static int MAX_COOLANT_IO = 200;
    private static int MAX_COOLANT_STORAGE = 1000;

    private static int MAX_RF_IO = 200;
    private static int MAX_RK_STORAGE = 2000;

    private static byte SIDE_DISABLED = 0;
    private static byte SIDE_COOLANT_INPUT = 1;
    private static byte SIDE_COOLANT_OUTPUT = 2;

    private CoolantStack coolantStack = new CoolantStack();
    private int energyStorage = 0;

    public TilePoweredFreezer() {
        super(RkStuff.blockPoweredFreezer);
        setUpdateInterval(20);
    }

    @Override
    protected boolean hasGui() {
        return true;
    }

    @Override
    protected boolean cacheNeighbours() {
        return true;
    }

    @Override
    public void readData(IOStream data) throws IOException {
        coolantStack.readData(data);
        energyStorage = data.readFirstInt();
    }

    @Override
    public void writeData(IOStream data) {
        coolantStack.writeData(data);
        data.writeLast(energyStorage);
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

        if (coolantStack.getAmount() > 0) {
            int rfUsage = Math.min(MAX_RF_IO, energyStorage);
            energyStorage -= rfUsage;
            float negativeEnergy = rfUsage * TEMPERATURE_ENERGY_PER_RF;
            coolantStack.extractEnergy(negativeEnergy);
        }

        int max = Math.min(MAX_COOLANT_IO, coolantStack.getAmount());
        if (max > 0) {
            coolantStack.remove(FluidHelper.outputCoolantToNeighbours(neighbours, sideCache, SIDE_COOLANT_OUTPUT, max, coolantStack.getTemperature()));
        }
    }

    @Override
    public int receiveCoolant(ForgeDirection from, int maxAmount, float temperature, boolean simulate) {
        if (sideCache[from.ordinal()] != SIDE_COOLANT_INPUT) return 0;
        maxAmount = Math.min(maxAmount, MAX_COOLANT_IO);
        maxAmount = Math.min(maxAmount, MAX_COOLANT_STORAGE - coolantStack.getAmount());
        if (!simulate) {
            coolantStack.add(maxAmount, temperature);
        }
        return maxAmount;
    }

    @Override
    public boolean canConnect(ForgeDirection from) {
        return sideCache[from.ordinal()] == SIDE_COOLANT_INPUT ||
                sideCache[from.ordinal()] == SIDE_COOLANT_OUTPUT;
    }

    @Override
    public int receiveEnergy(ForgeDirection forgeDirection, int maxEnergy, boolean simulate) {
        maxEnergy = Math.min(maxEnergy, MAX_RF_IO);
        maxEnergy = Math.min(maxEnergy, MAX_RK_STORAGE - energyStorage);
        if (!simulate) {
            energyStorage += maxEnergy;
        }
        return maxEnergy;
    }

    @Override
    public int getEnergyStored(ForgeDirection forgeDirection) {
        return energyStorage;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection forgeDirection) {
        return MAX_RK_STORAGE;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection forgeDirection) {
        return true;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentBody, IWailaDataAccessor accessor, IWailaConfigHandler configHandler) {
        currentBody.clear();
        currentBody.add(String.format("Energy: %d/%d RF", energyStorage, MAX_RK_STORAGE));
        currentBody.add(String.format("Coolant: %d/%d mB", coolantStack.getAmount(), MAX_COOLANT_STORAGE));
        currentBody.add(String.format("Temperature: %.2f °C", coolantStack.getTemperature()));
        return currentBody;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        coolantStack.writeToNBT("coolant", data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        coolantStack.readFromNBT("coolant", data);
    }
}
