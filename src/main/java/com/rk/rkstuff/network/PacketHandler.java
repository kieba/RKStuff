package com.rk.rkstuff.network;


import com.rk.rkstuff.network.message.IGuiActionMessage;
import com.rk.rkstuff.network.message.MessageCustom;
import com.rk.rkstuff.network.message.MessageGuiAction;
import com.rk.rkstuff.network.message.MessageSideConfigChanged;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.tileentity.TileEntity;
import rk.com.core.io.IOStream;

public class PacketHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);

    public static void init() {
        int index = 0;
        INSTANCE.registerMessage(MessageCustom.class, MessageCustom.class, index++, Side.CLIENT);
        INSTANCE.registerMessage(MessageGuiAction.class, MessageGuiAction.class, index++, Side.SERVER);
        INSTANCE.registerMessage(MessageSideConfigChanged.class, MessageSideConfigChanged.class, index++, Side.CLIENT);
        INSTANCE.registerMessage(MessageSideConfigChanged.class, MessageSideConfigChanged.class, index++, Side.SERVER);
    }

    public static <T extends TileEntity & IGuiActionMessage> void sendGuiActionMessage(T tile, IOStream data) {
        INSTANCE.sendToServer(new MessageGuiAction(tile, data));
    }

}
