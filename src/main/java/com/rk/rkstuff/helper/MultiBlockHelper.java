package com.rk.rkstuff.helper;

import net.minecraft.nbt.NBTTagCompound;

public class MultiBlockHelper {



    public static class Bounds{
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
                maxY = x;
            }

            if(maxZ < z){
                maxZ = x;
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

        public int getHight(){
            return maxY - minY;
        }

        public int getWidthX(){
            return maxX - minX;
        }

        public int getWidthZ(){
            return maxZ - minZ;
        }


    }

}
