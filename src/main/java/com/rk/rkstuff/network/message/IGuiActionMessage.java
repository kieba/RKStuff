package com.rk.rkstuff.network.message;

import rk.com.core.io.IOStream;

import java.io.IOException;

public interface IGuiActionMessage {

    public void receiveGuiAction(IOStream data) throws IOException;

}
