package com.rk.rkstuff.network.message;

import com.rk.rkstuff.core.tile.TileRKReconfigurable;
import com.rk.rkstuff.util.RKLog;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;

import java.io.IOException;

public class MessageSideConfigChanged implements IMessage, IMessageHandler<MessageSideConfigChanged, IMessage> {
    private int x, y, z;
    private int facing;
    private byte[] config;

    public MessageSideConfigChanged() {
    }

    public <T extends TileRKReconfigurable & ISideConfigChangedMessage> MessageSideConfigChanged(T tile) {
        x = tile.xCoord;
        y = tile.yCoord;
        z = tile.zCoord;
        facing = tile.getFacing();
        config = tile.getConfig();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        facing = buf.readInt();
        config = buf.readBytes(6).array();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(facing);
        buf.writeBytes(config);
    }

    @Override
    public IMessage onMessage(MessageSideConfigChanged message, MessageContext ctx) {
        TileEntity tileEntity = FMLClientHandler.instance().getServer().getEntityWorld().getTileEntity(message.x, message.y, message.z);
        if (tileEntity instanceof ISideConfigChangedMessage) {
            try {
                ((ISideConfigChangedMessage) tileEntity).receiveSideConfigChanged(message.facing, message.config);
            } catch (IOException e) {
                RKLog.error(e);
            }
        }
        return null;
    }
}
