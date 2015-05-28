package com.rk.rkstuff.network;


import com.rk.rkstuff.network.message.MessageCustom;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);

    public static void init() {
        int index = 0;
        INSTANCE.registerMessage(MessageCustom.class, MessageCustom.class, index++, Side.CLIENT);
    }

}
