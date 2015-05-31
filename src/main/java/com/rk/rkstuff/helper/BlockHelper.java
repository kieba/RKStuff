package com.rk.rkstuff.helper;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockHelper {

    public static TileEntity getNeighbourTileEntity(World world, int x, int y, int z, ForgeDirection direction) {
        x += direction.offsetX;
        y += direction.offsetY;
        z += direction.offsetZ;
        return world.getTileEntity(x, y, z);
    }

    public static TileEntity getNeighbourTileEntity(TileEntity source, int directionOrdinal) {
        return source == null ? null : getNeighbourTileEntity(source.getWorldObj(), source.xCoord, source.yCoord, source.zCoord,
                ForgeDirection.VALID_DIRECTIONS[directionOrdinal]);
    }

    public static TileEntity getNeighbourTileEntity(TileEntity source, ForgeDirection direction) {
        return source == null ? null : getNeighbourTileEntity(source.getWorldObj(), source.xCoord, source.yCoord, source.zCoord,
                direction);
    }
}
