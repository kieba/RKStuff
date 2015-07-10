package com.rk.rkstuff.core.tile;

import com.rk.rkstuff.network.PacketHandler;
import com.rk.rkstuff.network.message.ICustomMessage;
import com.rk.rkstuff.network.message.MessageCustom;
import com.rk.rkstuff.util.Pos;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;

public abstract class TileRK extends TileEntity implements ICustomMessage {

    public static int GUI_UPDATE_RATE = 10;

    private ArrayList<EntityPlayerMP> playerInGui = new ArrayList<EntityPlayerMP>(0);
    protected TileEntity[] neighbours = new TileEntity[6];
    private boolean hadFirstUpdate = false;
    private int updateInterval = 0;

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (worldObj.isRemote) return;
        if (hasGui() && !playerInGui.isEmpty()) {
            if (worldObj.getWorldTime() % GUI_UPDATE_RATE == 0) {
                updateGuiInformation();
            }
        }
        if (updateInterval != 0 && worldObj.getWorldTime() % updateInterval == 0) {
            markBlockForUpdate();
        }
        if (!hadFirstUpdate) {
            onFirstUpdate();
            hadFirstUpdate = true;
        }
    }

    protected void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }

    public void updateGuiInformation() {
        for (EntityPlayerMP p : playerInGui) {
            PacketHandler.INSTANCE.sendTo(new MessageCustom(this), p);
        }
    }

    public void registerPlayerGui(EntityPlayerMP player) {
        playerInGui.add(player);
        PacketHandler.INSTANCE.sendTo(new MessageCustom(this), player);
    }

    public void unregisterPlayerGui(EntityPlayerMP player) {
        playerInGui.remove(player);
    }

    public boolean hasGui() {
        return false;
    }

    protected boolean cacheNeighbours() {
        return false;
    }


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

    protected TileEntity getNeighbour(int side) {
        return neighbours[side];
    }

    public void onNeighborChange(ForgeDirection dir) {
        if (worldObj.isRemote || !cacheNeighbours()) return;
        int side = dir.ordinal();
        int x = xCoord + dir.offsetX;
        int y = yCoord + dir.offsetY;
        int z = zCoord + dir.offsetZ;
        neighbours[side] = worldObj.getTileEntity(x, y, z);
    }

    protected void onFirstUpdate() {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            onNeighborChange(dir);
        }
    }

    protected void markBlockForUpdate() {
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void updateEntityByMaster() {

    }
}
