package com.rk.rkstuff.network.message;

import com.rk.rkstuff.helper.RKLog;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class MessageCustom implements IMessage, IMessageHandler<MessageCustom, IMessage> {

    private int x, y,z;
    private IOStream msg = new IOStream();

    public MessageCustom() { }

    public <T extends TileEntity & ICustomMessage> MessageCustom(T tile) {
        x = tile.xCoord;
        y = tile.yCoord;
        z = tile.zCoord;
        tile.writeData(msg);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        int length = buf.readInt();
        try {
            buf.readBytes(msg.getOutputStreamLast(), length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        int length = msg.available();
        buf.writeInt(length);
        try {
            buf.writeBytes(msg.getInputStreamFirst(), length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public IMessage onMessage(MessageCustom message, MessageContext ctx) {
        TileEntity tileEntity = FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x, message.y, message.z);
        if (tileEntity instanceof ICustomMessage) {
            try {
                ((ICustomMessage) tileEntity).readData(message.msg);
            } catch (IOException e) {
                RKLog.error(e);
            }
        }
        return null;
    }

}
