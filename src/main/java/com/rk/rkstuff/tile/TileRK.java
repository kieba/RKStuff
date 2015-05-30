package com.rk.rkstuff.tile;

import com.rk.rkstuff.helper.Pos;
import com.rk.rkstuff.network.PacketHandler;
import com.rk.rkstuff.network.message.ICustomMessage;
import com.rk.rkstuff.network.message.MessageCustom;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;

public abstract class TileRK extends TileEntity implements ICustomMessage {

    public static int GUI_UPDATE_RATE = 10;

    private ArrayList<EntityPlayerMP> playerInGui = new ArrayList<>(1);
    private int tick = 0;

    @Override
    public void updateEntity() {
        super.updateEntity();
        if(hasGui() && !playerInGui.isEmpty()) {
            if(tick > GUI_UPDATE_RATE) {
                updateGuiInformation();
                tick = 0;
            } else  {
                tick++;
            }
        }
    }

    public void updateGuiInformation() {
        for (EntityPlayerMP p : playerInGui) {
            PacketHandler.INSTANCE.sendTo(new MessageCustom(this), p);
        }
    }

    public void registerPlayerGui(EntityPlayerMP player) {
        playerInGui.add(player);
    }

    public void unregisterPlayerGui(EntityPlayerMP player) {
        playerInGui.remove(player);
    }

    protected abstract boolean hasGui();

    public Pos getPosition(){
        return new Pos(xCoord, yCoord, zCoord);
    }
}
