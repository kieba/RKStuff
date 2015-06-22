package com.rk.rkstuff.coolant.tile;

import cofh.api.tileentity.IReconfigurableFacing;
import cofh.api.tileentity.IReconfigurableSides;
import cofh.api.tileentity.ISidedTexture;
import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.coolant.CoolantStack;
import com.rk.rkstuff.core.tile.TileRK;
import com.rk.rkstuff.helper.FluidHelper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileHeatExchanger extends TileRK implements ICoolantReceiver, IReconfigurableFacing, IReconfigurableSides, ISidedTexture {

    private static float SCALE = 0.05f;
    private static float LAVA_TEMPERATURE = 500.0f;
    private static float FIRE_TEMPERATURE = 200.0f;
    private static float WATER_TEMPERATURE = 20.0f;
    private static float SNOW_TEMPERATURE = -50.0f;
    private static float ICE_TEMPERATURE = -270.0f;

    private static int MAX_IO = 200; //mB
    private static int MAX_STORAGE = 1000;
    private Block[] blocks;
    private int[] config = new int[6]; //0 = disabled, 1 = input, 2 = output
    private CoolantStack coolantStack = new CoolantStack();

    @Override
    public void updateEntity() {
        super.updateEntity();

        for (int i = 0; i < 6; i++) {
            Block b = getBlock(i);

            float temperature = 0;
            if (isLava(b)) {
                temperature = LAVA_TEMPERATURE;
            } else if (isFire(b)) {
                temperature = FIRE_TEMPERATURE;
            } else if (isWater(b)) {
                temperature = WATER_TEMPERATURE;
            } else if (isSnow(b)) {
                temperature = SNOW_TEMPERATURE;
            } else if (isIce(b)) {
                temperature = ICE_TEMPERATURE;
            }

            if (temperature != 0) {
                float tempDiff = (temperature - coolantStack.getTemperature()) * SCALE;
                tempDiff /= coolantStack.getAmount();
                coolantStack.set(coolantStack.getAmount(), tempDiff);
            }
        }

        int max = Math.min(MAX_IO, coolantStack.getAmount());
        if (max > 0) {
            coolantStack.remove(FluidHelper.outputCoolantToNeighbours(neighbours, max, coolantStack.getTemperature()));
        }
    }

    @Override
    public void onNeighborChange(ForgeDirection dir) {
        super.onNeighborChange(dir);
        if (config[dir.ordinal()] != 2) neighbours[dir.ordinal()] = null;
    }

    @Override
    protected boolean cacheNeighbours() {
        return true;
    }

    @Override
    public void readData(IOStream data) throws IOException {
        for (int i = 0; i < 6; i++) {
            config[i] = data.readFirstInt();
        }
    }

    @Override
    public void writeData(IOStream data) {
        for (int i = 0; i < 6; i++) {
            data.writeLast(config[i]);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setIntArray("config", config);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        config = tag.getIntArray("config");
    }

    public void onNeighbourBlockChange() {
        if (blocks == null) blocks = new Block[6];
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            blocks[dir.ordinal()] = worldObj.getBlock(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
        }
    }

    private Block getBlock(int side) {
        if (blocks == null) {
            onNeighbourBlockChange();
        }
        return blocks[side];
    }

    @Override
    public int receiveCoolant(ForgeDirection from, int maxAmount, float temperature, boolean simulate) {
        if (config[from.getOpposite().ordinal()] != 1) return 0;
        maxAmount = Math.min(maxAmount, MAX_IO);
        maxAmount = Math.min(maxAmount, MAX_STORAGE - coolantStack.getAmount());
        if (!simulate) {
            coolantStack.add(maxAmount, temperature);
        }
        return maxAmount;
    }

    @Override
    public boolean canConnect(ForgeDirection from) {
        return config[from.getOpposite().ordinal()] != 0;
    }

    @Override
    public int getFacing() {
        return 0;
    }

    @Override
    public boolean allowYAxisFacing() {
        return false;
    }

    @Override
    public boolean rotateBlock() {
        return false;
    }

    @Override
    public boolean setFacing(int i) {
        return false;
    }

    @Override
    public boolean decrSide(int side) {
        if (worldObj.isRemote) return false;
        config[side]--;
        if (config[side] == -1) config[side] = 2;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        return true;
    }

    @Override
    public boolean incrSide(int side) {
        if (worldObj.isRemote) return false;
        config[side] = (config[side] + 1) % 3;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        return true;
    }

    @Override
    public boolean setSide(int side, int config) {
        if (worldObj.isRemote) return false;
        this.config[side] = config;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        return true;
    }

    @Override
    public boolean resetSides() {
        if (worldObj.isRemote) return false;
        for (int i = 0; i < 6; i++) {
            config[i] = 0;
        }
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        return true;
    }

    @Override
    public int getNumConfig(int side) {
        return 3;
    }

    @Override
    public IIcon getTexture(int side, int pass) {
        return RkStuff.blockHeatExchanger.blockIcons[config[side]];
    }

    private boolean isLava(Block b) {
        return b == Blocks.lava;
    }

    private boolean isFire(Block b) {
        return b == Blocks.fire;
    }

    private boolean isWater(Block b) {
        return b == Blocks.water;
    }

    private boolean isSnow(Block b) {
        return b == Blocks.snow;
    }

    private boolean isIce(Block b) {
        return b == Blocks.ice || b == Blocks.packed_ice;
    }
}
