package com.rk.rkstuff.client.model;

import com.rk.rkstuff.helper.ModelHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public class ModelBevelLarge {

    private Vec3[][] vertices = new Vec3[8][];
    private Vec3[][] normals = new Vec3[8][];

    public ModelBevelLarge() {
        setupModel();
    }

    private void setupModel() {
        //setup vertices for original model
        Vec3[] originalVertices = new Vec3[]{
                Vec3.createVectorHelper(-0.5f, -0.5f, -0.5f),
                Vec3.createVectorHelper(-0.5f, -0.5f, 0.5f),
                Vec3.createVectorHelper(-0.5f, 0.5f, 0.5f),
                Vec3.createVectorHelper(-0.5f, 0.5f, 0.5f),

                Vec3.createVectorHelper(0.5f, -0.5f, 0.5f),
                Vec3.createVectorHelper(0.5f, -0.5f, -0.5f),
                Vec3.createVectorHelper(0.5f, 0.5f, 0.5f),
                Vec3.createVectorHelper(0.5f, 0.5f, 0.5f),

                Vec3.createVectorHelper(0.5f, -0.5f, -0.5f),
                Vec3.createVectorHelper(-0.5f, -0.5f, -0.5f),
                Vec3.createVectorHelper(-0.5f, 0.5f, 0.5f),
                Vec3.createVectorHelper(0.5f, 0.5f, 0.5f),

                Vec3.createVectorHelper(0.5f, 0.5f, 0.5f),
                Vec3.createVectorHelper(-0.5f, 0.5f, 0.5f),
                Vec3.createVectorHelper(-0.5f, -0.5f, 0.5f),
                Vec3.createVectorHelper(0.5f, -0.5f, 0.5f),

                Vec3.createVectorHelper(0.5f, -0.5f, 0.5f),
                Vec3.createVectorHelper(-0.5f, -0.5f, 0.5f),
                Vec3.createVectorHelper(-0.5f, -0.5f, -0.5f),
                Vec3.createVectorHelper(0.5f, -0.5f, -0.5f)
        };

        //create a model for each metadata
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = new Vec3[originalVertices.length];
            for (int j = 0; j < originalVertices.length; j++) {
                vertices[i][j] = Vec3.createVectorHelper(originalVertices[j].xCoord, originalVertices[j].yCoord, originalVertices[j].zCoord);
            }
        }

        //apply rotations to the models (default location of model: yn dir: zp - SOUTH)
        for (int i = 0; i < vertices.length; i++) {
            int side = (i & 0x03) + 2;
            boolean yp = ((i >> 2) & 0x01) == 0x01;
            for (int j = 0; j < vertices[i].length; j++) {
                if (yp) vertices[i][j].rotateAroundX(((float) Math.PI) / 2.0f);
                if (side == ForgeDirection.NORTH.ordinal()) {
                    vertices[i][j].rotateAroundY(((float) Math.PI));
                } else if (side == ForgeDirection.WEST.ordinal()) {
                    vertices[i][j].rotateAroundY(((float) Math.PI) / 2.0f);
                } else if (side == ForgeDirection.EAST.ordinal()) {
                    vertices[i][j].rotateAroundY(((float) Math.PI) / -2.0f);
                }
            }
        }

        //compute normals
        for (int i = 0; i < vertices.length; i++) {
            normals[i] = new Vec3[originalVertices.length / 4];
            for (int j = 0; j < originalVertices.length; j += 4) {
                Vec3 vec3 = vertices[i][j + 1].subtract(vertices[i][j]);
                Vec3 vec31 = vertices[i][j + 1].subtract(vertices[i][j + 2]);
                normals[i][j / 4] = vec31.crossProduct(vec3).normalize();
            }
        }

    }

    public void render(IIcon i, float x, float y, float z, int meta) {

        double uMin = i.getMinU();
        double uMax = i.getMaxU();
        double vMin = i.getMinV();
        double vMax = i.getMaxV();

        Tessellator t = Tessellator.instance;
        t.setColorRGBA(255, 255, 255, 255);

        //TODO: improve vertices buffer and uv-offsets

        int index = 0;
        int indexNormal = 0;
        ModelHelper.setNormal(t, normals[meta][indexNormal++]);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin + (uMax - uMin) * (62.0 / 128.0), vMax);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin, vMax);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin, vMax - (vMax - vMin) * (62.0 / 128.0));
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin, vMax - (vMax - vMin) * (62.0 / 128.0));

        ModelHelper.setNormal(t, normals[meta][indexNormal++]);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin, vMax);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin + (uMax - uMin) * (62.0 / 128.0), vMax);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin, vMax - (vMax - vMin) * (62.0 / 128.0));
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin, vMax - (vMax - vMin) * (62.0 / 128.0));

        ModelHelper.setNormal(t, normals[meta][indexNormal++]);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin + (uMax - uMin) * (91.0 / 128.0), vMin);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin + (uMax - uMin) * (91.0 / 128.0), vMin + (vMax - vMin) * (64.0 / 128.0));
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin, vMin + (vMax - vMin) * (64.0 / 128.0));
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin, vMin);

        ModelHelper.setNormal(t, normals[meta][indexNormal++]);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMax, vMax - (vMax - vMin) * (64.0f / 128.0f));
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMax - (uMax - uMin) * (64.0 / 128.0), vMax - (vMax - vMin) * (64.0 / 128.0));
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMax - (uMax - uMin) * (64.0 / 128.0), vMax);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMax, vMax);

        ModelHelper.setNormal(t, normals[meta][indexNormal++]);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMax, vMax - (vMax - vMin) * (64.0 / 128.0));
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMax - (uMax - uMin) * (64.0 / 128.0), vMax - (vMax - vMin) * (64.0 / 128.0));
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMax - (uMax - uMin) * (64.0 / 128.0), vMax);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMax, vMax);

    }
}
