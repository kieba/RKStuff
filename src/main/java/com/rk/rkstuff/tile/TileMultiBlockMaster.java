package com.rk.rkstuff.tile;

import com.rk.rkstuff.helper.MultiBlockHelper;
import com.rk.rkstuff.helper.RKLog;
import com.rk.rkstuff.network.PacketHandler;
import com.rk.rkstuff.network.message.ICustomMessage;
import com.rk.rkstuff.network.message.MessageCustom;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import rk.com.core.io.IOStream;

import java.io.IOException;

public abstract class TileMultiBlockMaster extends TileRK implements ICustomMessage {

    private int interval = 40; //check structure every 40 ticks (2 seconds)
    private int tick;
    protected boolean isBuild;
    protected MultiBlockHelper.Bounds bounds;

    /**
     * Check that structure is properly formed (master only)
     */
    public abstract boolean checkMultiBlockForm();

    public void build() {
        if(!isBuild && checkMultiBlockForm()) {
            bounds = setupStructure();
            isBuild = true;
        }
    }

    public void reset() {
        if(isBuild) {
            resetStructure();
            bounds = null;
            isBuild = false;
        }
    }

    /**
     * Setup all the blocks in the structure
     */
    protected abstract MultiBlockHelper.Bounds setupStructure();

    /**
     * Reset all the parts of the structure
     */
    protected abstract void resetStructure();

    protected abstract void updateMaster();

    protected abstract void writeToNBTMaster(NBTTagCompound data);

    protected abstract void readFromNBTMaster(NBTTagCompound data);

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (!worldObj.isRemote) {
            if (isBuild) {
                updateMaster();
            } else {
                // Constantly check if structure is formed until it is.
                if (tick >= interval) {
                    build();
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
        reset();
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("isBuild", isBuild);

        if (isBuild) {
            bounds.writeToNBT(data);
            writeToNBTMaster(data);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        isBuild = data.getBoolean("isBuild");
        if (isBuild) {
            bounds = new MultiBlockHelper.Bounds(0,0,0);
            bounds.readFromNBT(data);
            readFromNBTMaster(data);
        }
    }

    @Override
    public void readData(IOStream data) throws IOException {
        isBuild = data.readFirstBoolean();
    }

    @Override
    public void writeData(IOStream data) {
        data.writeLast(isBuild);
    }

    @Override
    public Packet getDescriptionPacket() {
        return PacketHandler.INSTANCE.getPacketFrom(new MessageCustom(this));
    }

    public boolean isBuild() {
        return isBuild;
    }
}
