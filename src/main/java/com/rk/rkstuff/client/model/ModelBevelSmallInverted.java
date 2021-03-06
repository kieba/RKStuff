package com.rk.rkstuff.client.model;

import com.rk.rkstuff.helper.ModelHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;

public class ModelBevelSmallInverted {

    private Vec3[][] vertices = new Vec3[8][];
    private Vec3[][] normals = new Vec3[8][];

    public ModelBevelSmallInverted() {
        setupModel();
    }

    private void setupModel() {
        //setup vertices for original model
        Vec3[] originalVertices = new Vec3[]{
                Vec3.createVectorHelper(-0.5f, -0.5f, -0.5f),
                Vec3.createVectorHelper(-0.5f, -0.5f, 0.5f),
                Vec3.createVectorHelper(-0.5f, 0.5f, 0.5f),
                Vec3.createVectorHelper(-0.5f, 0.5f, 0.5f),

                Vec3.createVectorHelper(0.5f, -0.5f, -0.5f),
                Vec3.createVectorHelper(-0.5f, -0.5f, -0.5f),
                Vec3.createVectorHelper(0.5f, 0.5f, -0.5f),
                Vec3.createVectorHelper(0.5f, 0.5f, -0.5f),

                Vec3.createVectorHelper(0.5f, 0.5f, 0.5f),
                Vec3.createVectorHelper(0.5f, 0.5f, -0.5f),
                Vec3.createVectorHelper(-0.5f, 0.5f, 0.5f),
                Vec3.createVectorHelper(-0.5f, 0.5f, 0.5f),

                Vec3.createVectorHelper(0.5f, 0.5f, -0.5f),
                Vec3.createVectorHelper(-0.5f, -0.5f, -0.5f),
                Vec3.createVectorHelper(-0.5f, 0.5f, 0.5f),
                Vec3.createVectorHelper(-0.5f, 0.5f, 0.5f),

                Vec3.createVectorHelper(-0.5f, -0.5f, 0.5f),
                Vec3.createVectorHelper(0.5f, -0.5f, 0.5f),
                Vec3.createVectorHelper(0.5f, 0.5f, 0.5f),
                Vec3.createVectorHelper(-0.5f, 0.5f, 0.5f),

                Vec3.createVectorHelper(0.5f, -0.5f, -0.5f),
                Vec3.createVectorHelper(0.5f, 0.5f, -0.5f),
                Vec3.createVectorHelper(0.5f, 0.5f, 0.5f),
                Vec3.createVectorHelper(0.5f, -0.5f, 0.5f),

                Vec3.createVectorHelper(-0.5f, -0.5f, -0.5f),
                Vec3.createVectorHelper(0.5f, -0.5f, -0.5f),
                Vec3.createVectorHelper(0.5f, -0.5f, 0.5f),
                Vec3.createVectorHelper(-0.5f, -0.5f, 0.5f)
        };

        //create a model for each metadata
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = new Vec3[originalVertices.length];
            for (int j = 0; j < originalVertices.length; j++) {
                vertices[i][j] = Vec3.createVectorHelper(originalVertices[j].xCoord, originalVertices[j].yCoord, originalVertices[j].zCoord);
            }
        }

        //apply rotations to the models (default location of model: xp, yn, zp)
        for (int i = 0; i < vertices.length; i++) {
            boolean xp = (i & 0x01) == 0x01;
            boolean yp = ((i >> 1) & 0x01) == 0x01;
            boolean zp = ((i >> 2) & 0x01) == 0x01;

            for (int j = 0; j < vertices[i].length; j++) {
                if (yp) vertices[i][j].rotateAroundX(((float) Math.PI) / 2.0f);
                if (!xp && zp) vertices[i][j].rotateAroundY(((float) Math.PI) / -2.0f);
                if (xp && !zp) vertices[i][j].rotateAroundY(((float) Math.PI) / 2.0f);
                if (!xp && !zp) vertices[i][j].rotateAroundY((float) Math.PI);
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
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin + (uMax - uMin) * (191.0 / 256.0), vMax);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMax, vMax);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMax, vMin + (vMax - vMin) * (191.0 / 256.0));
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin, vMin + (vMax - vMin) * (191.0 / 256.0));

        ModelHelper.setNormal(t, normals[meta][indexNormal++]);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMax, vMax);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin + (uMax - uMin) * (191.0 / 256.0), vMax);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMax, vMin + (vMax - vMin) * (191.0 / 256.0));
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin, vMin + (vMax - vMin) * (191.0 / 256.0));

        ModelHelper.setNormal(t, normals[meta][indexNormal++]);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin, vMin);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin + (uMax - uMin) * (64.0 / 256.0), vMin);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin, vMin + (vMax - vMin) * (64.0 / 256.0));
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin, vMin + (vMax - vMin) * (64.0 / 256.0));

        ModelHelper.setNormal(t, normals[meta][indexNormal++]);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin + (uMax - uMin) * (164.0 / 256.0), vMin);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin + (uMax - uMin) * (210.0 / 256.0), vMin + (vMax - vMin) * (91.0 / 256.0));
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMax, vMin);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMax, vMin);

        ModelHelper.setNormal(t, normals[meta][indexNormal++]);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin, vMax);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin + (uMax - uMin) * (64.0 / 256.0), vMax);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin + (uMax - uMin) * (64.0 / 256.0), vMax - (vMax - vMin) * (64.0 / 256.0));
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin, vMax - (vMax - vMin) * (64.0 / 256.0));

        ModelHelper.setNormal(t, normals[meta][indexNormal++]);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin, vMax);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin + (uMax - uMin) * (64.0 / 256.0), vMax);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin + (uMax - uMin) * (64.0 / 256.0), vMax - (vMax - vMin) * (64.0 / 256.0));
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin, vMax - (vMax - vMin) * (64.0 / 256.0));

        ModelHelper.setNormal(t, normals[meta][indexNormal++]);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin, vMax);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin + (uMax - uMin) * (64.0 / 256.0), vMax);
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin + (uMax - uMin) * (64.0 / 256.0), vMax - (vMax - vMin) * (64.0 / 256.0));
        ModelHelper.addVertex(t, vertices[meta][index++], x, y, z, uMin, vMax - (vMax - vMin) * (64.0 / 256.0));
    }

}
