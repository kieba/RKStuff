package com.rk.rkstuff.tile;

import com.rk.rkstuff.network.PacketHandler;
import com.rk.rkstuff.network.message.ICustomMessage;
import com.rk.rkstuff.network.message.MessageCustom;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import rk.com.core.io.IOStream;

import java.io.IOException;

public abstract class TileMultiBlock extends TileRK implements ICustomMessage {

    private int interval = 40; //check structure every 40 ticks (2 seconds)
    private int tick;
    private boolean hasMaster, isMaster;
    private int masterX, masterY, masterZ;

    /** Check that structure is properly formed (master only) */
    protected abstract boolean checkMultiBlockForm();

    /** Setup all the blocks in the structure*/
    protected abstract void setupStructure();

    /** Reset all the parts of the structure */
    public abstract void resetStructure();

    protected abstract void updateMaster();

    protected abstract void writeToNBTMaster(NBTTagCompound data);

    protected abstract void readFromNBTMaster(NBTTagCompound data);

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (!worldObj.isRemote) {
            if (hasMaster()) {
                if (isMaster()) {
                    updateMaster();
                }
            } else {
                // Constantly check if structure is formed until it is.
                if(tick >= interval) {
                    if (checkMultiBlockForm()) {
                        setupStructure();
                    }
                    tick = 0;
                } else {
                    tick++;
                }
            }
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if(isMaster) {
            resetStructure();
        } else {
            TileMultiBlock master = getMaster();
            if(master != null) {
                master.resetStructure();
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("masterX", masterX);
        data.setInteger("masterY", masterY);
        data.setInteger("masterZ", masterZ);
        data.setBoolean("hasMaster", hasMaster);
        data.setBoolean("isMaster", isMaster);
        if (hasMaster() && isMaster()) {
            writeToNBTMaster(data);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        masterX = data.getInteger("masterX");
        masterY = data.getInteger("masterY");
        masterZ = data.getInteger("masterZ");
        hasMaster = data.getBoolean("hasMaster");
        isMaster = data.getBoolean("isMaster");
        if (hasMaster() && isMaster()) {
            readFromNBTMaster(data);
        }
    }

    public boolean hasMaster() {
        return hasMaster;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public TileMultiBlock getMaster() {
        if(!hasMaster) return null;
        TileEntity tile = worldObj.getTileEntity(masterX, masterY, masterZ);
        if(tile == null || !(tile instanceof TileMultiBlock)) return null;
        return (TileMultiBlock)tile;
    }

    public void setMultiBlock(int x, int y, int z, boolean isMaster) {
        this.masterX = x;
        this.masterY = y;
        this.masterZ = z;
        this.isMaster = isMaster;
        this.hasMaster = true;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void resetMultiBlock() {
        masterX = 0;
        masterY = 0;
        masterZ = 0;
        hasMaster = false;
        isMaster = false;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void readData(IOStream data) throws IOException {
        hasMaster = data.readFirstBoolean();

    }

    @Override
    public void writeData(IOStream data) {
        data.writeLast(hasMaster);
    }

    @Override
    public Packet getDescriptionPacket() {
        return PacketHandler.INSTANCE.getPacketFrom(new MessageCustom(this));
    }

}
