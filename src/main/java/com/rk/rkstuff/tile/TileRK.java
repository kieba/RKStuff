package com.rk.rkstuff.tile;

import com.rk.rkstuff.helper.Pos;
import net.minecraft.tileentity.TileEntity;

public abstract class TileRK extends TileEntity {

    public Pos getPosition(){
        return new Pos(xCoord, yCoord, zCoord);
    }
}
