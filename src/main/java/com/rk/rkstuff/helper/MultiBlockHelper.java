package com.rk.rkstuff.helper;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import scala.collection.*;

import java.lang.Iterable;
import java.util.*;
import java.util.Iterator;

public class MultiBlockHelper {



    public static class Bounds implements Iterable<Bounds.BlockIterator.BoundsPos>{
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

        public int getHeight(){
            return Math.abs(maxY - minY);
        }

        public int getWidthX(){
            return Math.abs(maxX - minX);
        }

        public int getWidthZ(){
            return Math.abs(maxZ - minZ);
        }

        public boolean isInBounds(Pos pos){
            return pos.x >= minX && pos.x <= maxX
                    && pos.y >= minY && pos.y <= maxY
                    && pos.z >= minZ && pos.z <= maxZ;
        }


        @Override
        public Iterator<BlockIterator.BoundsPos> iterator() {
            return new BlockIterator();
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
                if(xOffset > getWidthX()){
                    xOffset = 0;
                    yOffset++;
                }
                if(yOffset > getHeight()){
                    yOffset = 0;
                    zOffset++;
                }
                return tmp;
            }

            public class BoundsPos extends Pos{
                private BoundsPos(int x, int y, int z) {
                    super(x, y, z);
                }

                public boolean hasBlock(ForgeDirection direction){
                    return isInBounds(new Pos(direction.offsetX + x, direction.offsetY + y, direction.offsetZ + z));
                }

            }
        }
    }

}
