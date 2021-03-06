package com.rk.rkstuff.core.tile;

import cofh.api.tileentity.IReconfigurableFacing;
import cofh.api.tileentity.IReconfigurableSides;
import cofh.api.tileentity.ISidedTexture;
import com.rk.rkstuff.core.block.BlockRKReconfigurable;
import com.rk.rkstuff.network.PacketHandler;
import com.rk.rkstuff.network.message.ISideConfigChangedMessage;
import com.rk.rkstuff.network.message.MessageSideConfigChanged;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import rk.com.core.io.IOStream;

import java.io.IOException;

public abstract class TileRKReconfigurable extends TileRK implements ISidedTexture, IReconfigurableFacing, IReconfigurableSides, ISideConfigChangedMessage {
    protected int facing = 3;
    protected byte[] config = {0, 0, 0, 0, 0, 0};
    protected BlockRKReconfigurable block;

    public TileRKReconfigurable(BlockRKReconfigurable block) {
        this.block = block;
        config = getDefaultSideConfig().clone();
    }

    @Override
    public void readData(IOStream data) throws IOException {
        facing = data.readFirstInt();
        config = data.readFirstExact(6);
        markBlockForUpdate();
    }

    @Override
    public void writeData(IOStream data) {
        data.writeLast(facing);
        data.writeLast(config);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        facing = data.getInteger("reconf_facing");
        config = data.getByteArray("reconf_config");
        if (config == null || config.length < 6) {
            config = getDefaultSideConfig().clone();
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setInteger("reconf_facing", facing);
        data.setByteArray("reconf_config", config);
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

    public byte[] getConfig() {
        return config;
    }

    @Override
    public boolean rotateBlock() {
        if (allowYAxisFacing()) {
            byte[] arrayOfByte = new byte[6];
            int i = 0;
            switch (this.facing) {
                case 0:
                    for (i = 0; i < 6; i++) {
                        arrayOfByte[i] = this.config[cofh.lib.util.helpers.BlockHelper.INVERT_AROUND_X[i]];
                    }
                    break;
                case 1:
                    for (i = 0; i < 6; i++) {
                        arrayOfByte[i] = this.config[cofh.lib.util.helpers.BlockHelper.ROTATE_CLOCK_X[i]];
                    }
                    break;
                case 2:
                    for (i = 0; i < 6; i++) {
                        arrayOfByte[i] = this.config[cofh.lib.util.helpers.BlockHelper.INVERT_AROUND_Y[i]];
                    }
                    break;
                case 3:
                    for (i = 0; i < 6; i++) {
                        arrayOfByte[i] = this.config[cofh.lib.util.helpers.BlockHelper.ROTATE_CLOCK_Y[i]];
                    }
                    break;
                case 4:
                    for (i = 0; i < 6; i++) {
                        arrayOfByte[i] = this.config[cofh.lib.util.helpers.BlockHelper.INVERT_AROUND_Z[i]];
                    }
                    break;
                case 5:
                    for (i = 0; i < 6; i++) {
                        arrayOfByte[i] = this.config[cofh.lib.util.helpers.BlockHelper.ROTATE_CLOCK_Z[i]];
                    }
            }
            this.config = arrayOfByte.clone();
            this.facing = this.facing + 1;
            this.facing = this.facing % 6;
            markBlockForUpdate();
            return true;
        }
        byte[] arrayOfByte = new byte[6];
        for (int i = 0; i < 6; i++) {
            arrayOfByte[i] = this.config[cofh.lib.util.helpers.BlockHelper.ROTATE_CLOCK_Y[i]];
        }
        this.config = arrayOfByte.clone();
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
        if (hasFacing()) {
            config[facing] = (byte) getNumConfig(0);
        }
        markBlockForUpdate();
        return true;
    }

    //on Clientside
    @Override
    public boolean decrSide(int side) {
        if (hasFacing() && side == facing) {
            return false;
        }
        config[side]--;
        config[side] = (byte) (config[side] % getNumConfig(side));
        updateFacingConfigToAll();
        return true;
    }

    //on Clientside
    @Override
    public boolean incrSide(int side) {
        if (hasFacing() && side == facing) {
            return false;
        }
        config[side]++;
        config[side] = (byte) (config[side] % getNumConfig(side));
        updateFacingConfigToAll();
        return true;
    }

    @Override
    public boolean setSide(int side, int config) {
        if (hasFacing() && side == facing) {
            return false;
        }
        this.config[side] = (byte) config;
        updateFacingConfigToAll();
        return true;
    }

    protected abstract byte[] getDefaultSideConfig();

    @Override
    public boolean resetSides() {
        config = getDefaultSideConfig();
        return true;
    }

    @Override
    public abstract int getNumConfig(int i);

    @Override
    public IIcon getTexture(int side, int pass) {
        return block.getIconForGui(side, config[side]);
    }

    @Override
    public void receiveSideConfigChanged(int newFacing, byte[] newConfig) throws IOException {
        this.facing = newFacing;
        this.config = newConfig;
        markDirty();
        markBlockForUpdate();
    }

    private void updateFacingConfigToAll() {
        if (worldObj.isRemote) {
            PacketHandler.INSTANCE.sendToServer(new MessageSideConfigChanged(this));
        } else {
            markBlockForUpdate();
        }

    }

}
