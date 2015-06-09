package com.rk.rkstuff.helper;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Vec3;

public class ModelHelper {

    public static void setNormal(Tessellator t, Vec3 normal) {
        t.setNormal((float) normal.xCoord, (float) normal.yCoord, (float) normal.zCoord);
    }

    public static void addVertex(Tessellator t, Vec3 vertex, float x, float y, float z, double u, double v) {
        t.addVertexWithUV(x + vertex.xCoord, y + vertex.yCoord, z + vertex.zCoord, u, v);
    }

}
