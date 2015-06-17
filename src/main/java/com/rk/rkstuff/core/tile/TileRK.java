package com.rk.rkstuff.core.tile;

import com.rk.rkstuff.network.PacketHandler;
import com.rk.rkstuff.network.message.ICustomMessage;
import com.rk.rkstuff.network.message.MessageCustom;
import com.rk.rkstuff.util.Pos;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;

public abstract class TileRK extends TileEntity implements ICustomMessage {

    public static int GUI_UPDATE_RATE = 10;

    private ArrayList<EntityPlayerMP> playerInGui = new ArrayList<EntityPlayerMP>(0);
    private int tick = 0;

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (!worldObj.isRemote && hasGui() && !playerInGui.isEmpty()) {
            if(tick > GUI_UPDATE_RATE) {
                updateGuiInformation();
            } else {
                tick++;
            }
        }
    }

    public void updateGuiInformation() {
        for (EntityPlayerMP p : playerInGui) {
            PacketHandler.INSTANCE.sendTo(new MessageCustom(this), p);
        }
        tick = 0;
    }

    public void registerPlayerGui(EntityPlayerMP player) {
        playerInGui.add(player);
        PacketHandler.INSTANCE.sendTo(new MessageCustom(this), player);
    }

    public void unregisterPlayerGui(EntityPlayerMP player) {
        playerInGui.remove(player);
    }

    protected abstract boolean hasGui();

    public Pos getPosition(){
        return new Pos(xCoord, yCoord, zCoord);
    }

    @Override
    public Packet getDescriptionPacket() {
        return PacketHandler.INSTANCE.getPacketFrom(new MessageCustom(this));
    }

    @Override
    public void onChunkUnload() {
        if (!tileEntityInvalid) {
            invalidate();
        }
    }
}
