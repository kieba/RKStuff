package com.rk.rkstuff.coolant.tile;

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

public class TileHeatPump extends TileRKReconfigurable implements ICoolantReceiver, IWailaBodyProvider {
    private static byte[] defaultConfig = {0, 0, 0, 0, 0, 0};

    private static byte SIDE_DISABLED = 0;
    private static byte SIDE_COOLANT_INPUT1 = 1;
    private static byte SIDE_COOLANT_INPUT2 = 2;
    private static byte SIDE_COOLANT_OUTPUT1 = 3;
    private static byte SIDE_COOLANT_OUTPUT2 = 4;

    private static int MAX_COOLANT_RESOURCE_STORAGE = 1000;
    private static int MAX_COOLANT_IO = 200;

    private CoolantStack coolantStackRes1 = new CoolantStack();
    private CoolantStack coolantStackRes2 = new CoolantStack();

    private int energyStorage = 0;

    public TileHeatPump() {
        super(RkStuff.blockHeatPump);
        setUpdateInterval(20);
    }

    @Override
    protected byte[] getDefaultSideConfig() {
        return defaultConfig;
    }

    @Override
    public int getNumConfig(int i) {
        return 5;
    }

    @Override
    public boolean hasFacing() {
        return true;
    }

    @Override
    public int receiveCoolant(ForgeDirection from, int maxAmount, float temperature, boolean simulate) {
        if (!(config[from.ordinal()] == SIDE_COOLANT_INPUT1 || config[from.ordinal()] == SIDE_COOLANT_INPUT2))
            return 0;
        CoolantStack stack;
        if (config[from.ordinal()] == SIDE_COOLANT_INPUT1) {
            stack = coolantStackRes1;
        } else {
            stack = coolantStackRes2;
        }
        maxAmount = Math.min(maxAmount, MAX_COOLANT_RESOURCE_STORAGE);
        maxAmount = Math.min(maxAmount, MAX_COOLANT_RESOURCE_STORAGE - stack.getAmount());
        if (!simulate) {
            stack.add(maxAmount, temperature);
        }
        return maxAmount;
    }

    @Override
    public boolean canConnect(ForgeDirection from) {
        return config[from.ordinal()] == SIDE_COOLANT_INPUT1 ||
                config[from.ordinal()] == SIDE_COOLANT_INPUT2 ||
                config[from.ordinal()] == SIDE_COOLANT_OUTPUT1 ||
                config[from.ordinal()] == SIDE_COOLANT_OUTPUT2;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        coolantStackRes1.writeToNBT("coolantRes1", data);
        coolantStackRes2.writeToNBT("coolantRes2", data);
        data.setInteger("rf", energyStorage);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        coolantStackRes1.readFromNBT("coolantRes1", data);
        coolantStackRes2.readFromNBT("coolantRes2", data);
        energyStorage = data.getInteger("rf");
    }

    @Override
    public void writeData(IOStream data) {
        super.writeData(data);

        coolantStackRes1.writeData(data);
        coolantStackRes2.writeData(data);
        data.writeLast(energyStorage);
    }

    @Override
    public void readData(IOStream data) throws IOException {
        super.readData(data);

        coolantStackRes1.readData(data);
        coolantStackRes2.readData(data);
        energyStorage = data.readFirstInt();
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (worldObj.isRemote) return;

        float extractEff = (coolantStackRes1.getTemperature()) / CoolantStack.MAX_TEMPERATURE;
        float injectEff = (float) Math.pow(1 - ((coolantStackRes2.getTemperature()) / CoolantStack.MAX_TEMPERATURE), 2);
        float extractEnergy = Math.abs(extractEff * coolantStackRes1.getEnergy()) * 0.001f;
        float injectEnergy = extractEnergy * injectEff;

        coolantStackRes1.extractEnergy(extractEnergy);
        coolantStackRes2.addEnergy(injectEnergy);

        coolantStackRes1.remove(FluidHelper.outputCoolantToNeighbours(neighbours, config, SIDE_COOLANT_OUTPUT1, MAX_COOLANT_IO, coolantStackRes1.getTemperature()));
        coolantStackRes2.remove(FluidHelper.outputCoolantToNeighbours(neighbours, config, SIDE_COOLANT_OUTPUT2, MAX_COOLANT_IO, coolantStackRes2.getTemperature()));
    }


    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentBody, IWailaDataAccessor accessor, IWailaConfigHandler configHandler) {
        currentBody.add(String.format("Coolant Res1: %d/%d mB", coolantStackRes1.getAmount(), MAX_COOLANT_RESOURCE_STORAGE));
        currentBody.add(String.format("Temperature Prod: %s", coolantStackRes1.getFormattedString()));
        currentBody.add(String.format("Coolant Res2: %d/%d mB", coolantStackRes2.getAmount(), MAX_COOLANT_RESOURCE_STORAGE));
        currentBody.add(String.format("Temperature Prod: %s", coolantStackRes2.getFormattedString()));

        return currentBody;
    }
}
