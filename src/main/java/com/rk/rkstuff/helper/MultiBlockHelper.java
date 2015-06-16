package com.rk.rkstuff.helper;

import com.rk.rkstuff.network.message.ICustomMessage;
import com.rk.rkstuff.util.Pos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import rk.com.core.io.IOStream;

import java.io.IOException;
import java.util.Iterator;

public class MultiBlockHelper {


    public static class Bounds implements Iterable<Bounds.BlockIterator.BoundsPos>, ICustomMessage {
        private int minX;
        private int minY;
        private int minZ;
        private int maxX;
        private int maxY;
        private int maxZ;

        public Bounds(int x, int y, int z){
            minX = x;
            minY = y;
            minZ = z;
            maxX = x;
            maxY = y;
            maxZ = z;
        }

        public Bounds(Pos position){
            this(position.x, position.y, position.z);
        }

        public void add(int x, int y, int z){
            if(minX > x){
                minX = x;
            }

            if(minY > y){
                minY = y;
            }

            if(minZ > z){
                minZ = z;
            }

            if(maxX < x){
                maxX = x;
            }

            if(maxY < y){
                maxY = y;
            }

            if(maxZ < z){
                maxZ = z;
            }
        }

        @Override
        public String toString() {
            return "Bounds{" +
                    "maxX=" + maxX +
                    ", minX=" + minX +
                    ", minY=" + minY +
                    ", minZ=" + minZ +
                    ", maxY=" + maxY +
                    ", maxZ=" + maxZ +
                    '}';
        }

        public void writeToNBT(NBTTagCompound data) {
            data.setInteger("minX", getMinX());
            data.setInteger("minY", getMinY());
            data.setInteger("minZ", getMinZ());
            data.setInteger("maxX", getMaxX());
            data.setInteger("maxY", getMaxY());
            data.setInteger("maxZ", getMaxZ());
        }

        public void readFromNBT(NBTTagCompound data) {
            minX = data.getInteger("minX");
            minY = data.getInteger("minY");
            minZ = data.getInteger("minZ");
            maxX = data.getInteger("maxX");
            maxY = data.getInteger("maxY");
            maxZ = data.getInteger("maxZ");
        }


        public int getMaxX() {
            return maxX;
        }

        public int getMaxY() {
            return maxY;
        }

        public int getMaxZ() {
            return maxZ;
        }

        public int getMinX() {
            return minX;
        }

        public int getMinY() {
            return minY;
        }

        public int getMinZ() {
            return minZ;
        }

        public void setMaxX(int maxX) {
            this.maxX = maxX;
        }

        public void setMaxY(int maxY) {
            this.maxY = maxY;
        }

        public void setMaxZ(int maxZ) {
            this.maxZ = maxZ;
        }

        public void setMinX(int minX) {
            this.minX = minX;
        }

        public void setMinY(int minY) {
            this.minY = minY;
        }

        public void setMinZ(int minZ) {
            this.minZ = minZ;
        }

        public int getHeight(){
            return Math.abs(maxY - minY) + 1;
        }

        public int getWidthX(){
            return Math.abs(maxX - minX) + 1;
        }

        public int getWidthZ(){
            return Math.abs(maxZ - minZ) + 1;
        }

        public int size(){
            return getHeight() * getWidthX() * getWidthZ();
        }

        public boolean isInBounds(Pos pos){
            return pos.x >= minX && pos.x <= maxX
                    && pos.y >= minY && pos.y <= maxY
                    && pos.z >= minZ && pos.z <= maxZ;
        }

        public Bounds clone() {
            Bounds newBounds = new Bounds(minX, minY, minZ);
            newBounds.add(maxX, maxY, maxZ);
            return newBounds;
        }


        @Override
        public Iterator<BlockIterator.BoundsPos> iterator() {
            return new BlockIterator();
        }

        @Override
        public void readData(IOStream data) throws IOException {
            minX = data.readFirstInt();
            minY = data.readFirstInt();
            minZ = data.readFirstInt();
            maxX = data.readFirstInt();
            maxY = data.readFirstInt();
            maxZ = data.readFirstInt();
        }

        @Override
        public void writeData(IOStream data) {
            data.writeLast(minX);
            data.writeLast(minY);
            data.writeLast(minZ);
            data.writeLast(maxX);
            data.writeLast(maxY);
            data.writeLast(maxZ);
        }

        public class BlockIterator implements Iterator<BlockIterator.BoundsPos> {
            private int xOffset, yOffset, zOffset;

            private BlockIterator() {
            }

            @Override
            public boolean hasNext() {
                return isInBounds(new Pos(minX + xOffset, minY + yOffset, minZ + zOffset));
            }

            @Override
            public BoundsPos next() {
                BoundsPos tmp = new BoundsPos(minX + xOffset, minY + yOffset, minZ + zOffset);

                xOffset++;
                if (xOffset >= getWidthX()) {
                    xOffset = 0;
                    yOffset++;
                }
                if (yOffset >= getHeight()) {
                    yOffset = 0;
                    zOffset++;
                }
                return tmp;
            }

            public class BoundsPos extends Pos{
                private BoundsPos(int x, int y, int z) {
                    super(x, y, z);
                }

                public boolean hasNeighbourBlock(ForgeDirection direction) {
                    return isInBounds(new Pos(direction.offsetX + x, direction.offsetY + y, direction.offsetZ + z));
                }

                public boolean isBorder() {
                    return x == minX || x == maxX || y == minY || y == maxY || z == minZ || z == maxZ;
                }

                public boolean isEdge() {
                    return (x == minX || x == maxX) &&
                            (y == minY || y == maxY) &&
                            (z == minZ || z == maxZ);
                }
            }
        }
    }

}
