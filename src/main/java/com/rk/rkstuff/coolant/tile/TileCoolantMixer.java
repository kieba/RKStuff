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

public class TileCoolantMixer extends TileRKReconfigurable implements ICoolantReceiver, IWailaBodyProvider {
    public static byte[] defaultConfig = {0, 0, 0, 0, 0, 0};

    private static final byte SIDE_DISABLED = 0;
    private static final byte SIDE_COOLANT_INPUT1 = 1;
    private static final byte SIDE_COOLANT_INPUT2 = 2;
    private static final byte SIDE_COOLANT_OUTPUT = 3;

    private static int MAX_COOLANT_RESOURCE_STORAGE = 2000;
    private static int MAX_COOLANT_PRODUCT_STORAGE = 500;
    private static int MAX_PRODUCTION_IO = 200;

    private CoolantStack coolantStackRes1 = new CoolantStack();
    private CoolantStack coolantStackRes2 = new CoolantStack();
    private CoolantStack coolantStackProd = new CoolantStack();

    private float targetTemperature = 24.5f;

    public TileCoolantMixer() {
        super(RkStuff.blockCoolantMixer);
        setUpdateInterval(20);
    }

    @Override
    protected byte[] getDefaultSideConfig() {
        return defaultConfig;
    }

    @Override
    public int getNumConfig(int i) {
        return 4;
    }

    @Override
    public boolean hasFacing() {
        return true;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (worldObj.isRemote) return;

        if (MAX_COOLANT_PRODUCT_STORAGE - coolantStackProd.getAmount() > MAX_PRODUCTION_IO) {
            int productionVolume = Math.min(MAX_PRODUCTION_IO, MAX_COOLANT_PRODUCT_STORAGE - coolantStackProd.getAmount());
            productionVolume = Math.min(productionVolume, coolantStackRes1.getAmount() + coolantStackRes2.getAmount());

            float targetHeat = targetTemperature * productionVolume;
            int coolant2Usage = (int) ((targetHeat - coolantStackRes1.getTemperature() * productionVolume) / (-coolantStackRes1.getTemperature() + coolantStackRes2.getTemperature()));
            int coolant1Usage = productionVolume - coolant2Usage;

            coolant1Usage = Math.max(coolant1Usage, 0);
            coolant2Usage = Math.max(coolant2Usage, 0);

            coolant1Usage = Math.min(coolant1Usage, MAX_PRODUCTION_IO);
            coolant2Usage = Math.min(coolant2Usage, MAX_PRODUCTION_IO);

            if (coolant1Usage < 0 || coolant2Usage < 0) {
                productionVolume = Math.min(MAX_PRODUCTION_IO, MAX_COOLANT_PRODUCT_STORAGE - coolantStackProd.getAmount());
                coolant1Usage = productionVolume / 2;
                coolant2Usage = productionVolume / 2;
            }

            coolantStackProd.add(coolantStackRes1.remove(coolant1Usage));
            coolantStackProd.add(coolantStackRes2.remove(coolant2Usage));
        }

        coolantStackProd.remove(FluidHelper.outputCoolantToNeighbours(neighbours, config, SIDE_COOLANT_OUTPUT, coolantStackProd.getAmount(), coolantStackProd.getTemperature()));
    }

    @Override
    protected boolean cacheNeighbours() {
        return true;
    }

    @Override
    public void writeData(IOStream data) {
        super.writeData(data);

        coolantStackProd.writeData(data);
        coolantStackRes1.writeData(data);
        coolantStackRes2.writeData(data);
    }

    @Override
    public void readData(IOStream data) throws IOException {
        super.readData(data);

        coolantStackProd.readData(data);
        coolantStackRes1.readData(data);
        coolantStackRes2.readData(data);
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        coolantStackProd.writeToNBT("coolantProd", data);
        coolantStackRes1.writeToNBT("coolantRes1", data);
        coolantStackRes2.writeToNBT("coolantRes2", data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        coolantStackProd.readFromNBT("coolantProd", data);
        coolantStackRes1.readFromNBT("coolantRes1", data);
        coolantStackRes2.readFromNBT("coolantRes2", data);
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
                config[from.ordinal()] == SIDE_COOLANT_OUTPUT;
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentBody, IWailaDataAccessor accessor, IWailaConfigHandler configHandler) {
        currentBody.add(String.format("Coolant Prod: %d/%d mB", coolantStackProd.getAmount(), MAX_COOLANT_PRODUCT_STORAGE));
        currentBody.add(String.format("Temperature Prod: %s", coolantStackProd.getFormattedString()));
        currentBody.add(String.format("Coolant Res1: %d/%d mB", coolantStackRes1.getAmount(), MAX_COOLANT_RESOURCE_STORAGE));
        currentBody.add(String.format("Temperature Prod: %s", coolantStackRes1.getFormattedString()));
        currentBody.add(String.format("Coolant Res2: %d/%d mB", coolantStackRes2.getAmount(), MAX_COOLANT_RESOURCE_STORAGE));
        currentBody.add(String.format("Temperature Prod: %s", coolantStackRes2.getFormattedString()));

        return currentBody;
    }
}
