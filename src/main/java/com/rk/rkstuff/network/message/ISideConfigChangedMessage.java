package com.rk.rkstuff.network.message;

import java.io.IOException;

public interface ISideConfigChangedMessage {

    public void receiveSideConfigChanged(int newFacing, byte[] newConfig) throws IOException;
}
