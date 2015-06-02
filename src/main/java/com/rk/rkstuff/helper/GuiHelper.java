package com.rk.rkstuff.helper;

public class GuiHelper {

    public static boolean isInArea(int x, int y, int xMin, int yMin, int xMax, int yMax) {
        if(x < xMin)return false;
        if(y < yMin)return false;
        if(x > xMax)return false;
        if(y > yMax)return false;
        return true;
    }
}
