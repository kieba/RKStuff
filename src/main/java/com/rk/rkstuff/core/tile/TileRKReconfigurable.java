package com.rk.rkstuff.core.tile;

import cofh.api.tileentity.IReconfigurableFacing;
import cofh.api.tileentity.IReconfigurableSides;
import cofh.api.tileentity.ISidedTexture;
import com.rk.rkstuff.core.block.BlockRKReconfigurable;
import net.minecraft.util.IIcon;
import rk.com.core.io.IOStream;

import java.io.IOException;

public abstract class TileRKReconfigurable extends TileRK implements ISidedTexture, IReconfigurableFacing, IReconfigurableSides {
    protected int facing = 0;
    protected byte[] sideCache = {0, 0, 0, 0, 0, 0};
    protected BlockRKReconfigurable block;

    public TileRKReconfigurable(BlockRKReconfigurable block) {
        this.block = block;
    }

    @Override
    public void readData(IOStream data) throws IOException {
        facing = data.readFirstInt();
        sideCache = data.readFirstExact(6);
    }

    @Override
    public void writeData(IOStream data) {
        data.writeLast(facing);
        data.writeLast(sideCache);
    }

    @Override
    public int getFacing() {
        return facing;
    }

    @Override
    public boolean allowYAxisFacing() {
        return false;
    }

    public boolean hasFacing() {
        return false;
    }

    @Override
    public boolean rotateBlock() {
        if (allowYAxisFacing()) {
            byte[] arrayOfByte = new byte[6];
            int i = 0;
            switch (this.facing) {
                case 0:
                    for (i = 0; i < 6; i++) {
                        arrayOfByte[i] = this.sideCache[cofh.lib.util.helpers.BlockHelper.INVERT_AROUND_X[i]];
                    }
                    break;
                case 1:
                    for (i = 0; i < 6; i++) {
                        arrayOfByte[i] = this.sideCache[cofh.lib.util.helpers.BlockHelper.ROTATE_CLOCK_X[i]];
                    }
                    break;
                case 2:
                    for (i = 0; i < 6; i++) {
                        arrayOfByte[i] = this.sideCache[cofh.lib.util.helpers.BlockHelper.INVERT_AROUND_Y[i]];
                    }
                    break;
                case 3:
                    for (i = 0; i < 6; i++) {
                        arrayOfByte[i] = this.sideCache[cofh.lib.util.helpers.BlockHelper.ROTATE_CLOCK_Y[i]];
                    }
                    break;
                case 4:
                    for (i = 0; i < 6; i++) {
                        arrayOfByte[i] = this.sideCache[cofh.lib.util.helpers.BlockHelper.INVERT_AROUND_Z[i]];
                    }
                    break;
                case 5:
                    for (i = 0; i < 6; i++) {
                        arrayOfByte[i] = this.sideCache[cofh.lib.util.helpers.BlockHelper.ROTATE_CLOCK_Z[i]];
                    }
            }
            this.sideCache = arrayOfByte.clone();
            this.facing = this.facing + 1;
            this.facing = this.facing % 6;
            markBlockForUpdate();
            return true;
        }
        byte[] arrayOfByte = new byte[6];
        for (int i = 0; i < 6; i++) {
            arrayOfByte[i] = this.sideCache[cofh.lib.util.helpers.BlockHelper.ROTATE_CLOCK_Y[i]];
        }
        this.sideCache = arrayOfByte.clone();
        this.facing = cofh.lib.util.helpers.BlockHelper.SIDE_LEFT[this.facing];
        markBlockForUpdate();
        return true;
    }

    @Override
    public boolean setFacing(int newFacing) {
        if ((newFacing < 0) || (newFacing > 5)) {
            return false;
        }
        if ((!allowYAxisFacing()) && (newFacing < 2)) {
            return false;
        }
        facing = (byte) newFacing;
        markBlockForUpdate();
        return true;
    }

    @Override
    public boolean decrSide(int side) {
        if (hasFacing() && side == facing) {
            return false;
        }
        sideCache[side]--;
        sideCache[side] = (byte) (sideCache[side] % getNumConfig(side));
        return true;
    }

    @Override
    public boolean incrSide(int side) {
        if (hasFacing() && side == facing) {
            return false;
        }
        sideCache[side]++;
        sideCache[side] = (byte) (sideCache[side] % getNumConfig(side));
        return true;
    }

    @Override
    public boolean setSide(int side, int config) {
        if (hasFacing() && side == facing) {
            return false;
        }
        sideCache[side] = (byte) config;
        return true;
    }

    protected abstract byte[] getDefaultSideConfig();

    @Override
    public boolean resetSides() {
        sideCache = getDefaultSideConfig();
        return true;
    }

    @Override
    public abstract int getNumConfig(int i);

    @Override
    public IIcon getTexture(int side, int pass) {
        return block.getIconForGui(side, sideCache[side]);
    }
}
