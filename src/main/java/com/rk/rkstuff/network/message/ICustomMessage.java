package com.rk.rkstuff.network.message;

import rk.com.core.io.IOStream;

import java.io.IOException;

public interface ICustomMessage {

    public void readData(IOStream data) throws IOException;
    public void writeData(IOStream data);

}
