package com.rk.rkstuff.tile;

import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileModelTest extends TileRK {

    public float rotation = 0.0f;

    @Override
    protected boolean hasGui() {
        return false;
    }

    @Override
    public void readData(IOStream data) throws IOException {

    }

    @Override
    public void writeData(IOStream data) {

    }

    public void addRotaion() {
        rotation += 10.0f;
        rotation %= 360.0f;
    }

    public void subRotaion() {
        rotation -= 10.0f;
        if (rotation < 0.0f) rotation = 360.0f;
    }
}
