package com.rk.rkstuff.client.model;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.util.Reference;
import com.rk.rkstuff.util.Textures;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.util.ForgeDirection;

@SideOnly(Side.CLIENT)
public class ModelPipe {

    private static float FLUID_TEX_SIZE = 32.0f;

    private static float hDiameter = 1.325F;

    private static Vec3 centerTopVec = Vec3.createVectorHelper(0.0, 1.0, 0.0);
    private static Vec3 centerBottomVec = Vec3.createVectorHelper(0.0, -1.0, 0.0);
    private static Vec3 centerLeftVec = Vec3.createVectorHelper(0.0, 0.0, 1.0);
    private static Vec3 centerRightVec = Vec3.createVectorHelper(0.0, 0.0, -1.0);
    private static Vec3[] centerVectorBase = new Vec3[]{centerTopVec, centerBottomVec, centerLeftVec, centerRightVec};

    //blockSide - quads
    private static TexturedQuad[][] liquidQuads = new TexturedQuad[6][];

    private static final String TEXTURE_PATH = Reference.MOD_ID + ":textures/blocks/pipe/";
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[]{
            new ResourceLocation(TEXTURE_PATH + "pipe0.png"),
            new ResourceLocation(TEXTURE_PATH + "pipe1.png"),
            new ResourceLocation(TEXTURE_PATH + "pipe2.png"),
            new ResourceLocation(TEXTURE_PATH + "pipe3.png"),
            new ResourceLocation(TEXTURE_PATH + "pipe4.png"),
            new ResourceLocation(TEXTURE_PATH + "pipe5.png")
    };

    private static final ResourceLocation TEXTURE_DEFAULT = new ResourceLocation(TEXTURE_PATH + "pipeCenter.png");
    private static final String modelFile = "Pipe.obj";
    private static final String[] modelSide = {"zMinus", "zPlus", "yPlus", "yMinus", "xMinus", "xPlus"};
    private static final String modelCenter = "CenterCube";

    private IModelCustom modelPipe;


    public ModelPipe() {
        modelPipe = AdvancedModelLoader.loadModel(new ResourceLocation(Reference.MOD_ID + ":models/" + modelFile));
    }

    public void render(boolean[] isConnected, boolean[] hasAdapter, int meta) {
        int connected = 0;

        Textures.loadTexture(TEXTURES[meta]);
        for (int i = 0; i < 6; i++) {
            if (!isConnected[i]) {
                continue;
            } else {
                connected |= (0x01 << i);
            }
            modelPipe.renderPart(modelSide[i]);
        }

        Textures.loadTexture(TEXTURE_DEFAULT);
        for (int i = 0; i < 6; i++) {
            if (!hasAdapter[i]) continue;
            modelPipe.renderPart(modelSide[i] + "AdapterDefault");
        }

        if (connected != 0x03 && connected != 0x0C && connected != 0x30) {
            modelPipe.renderPart(modelCenter);
        }

        Textures.loadTexture(TextureMap.locationBlocksTexture);
        for (int i = 0; i < 6; i++) {
            if (!isConnected[i]) continue;
            renderLiquid(ForgeDirection.values()[i]);
        }
    }

    public static void init() {
        for (ForgeDirection dir : ForgeDirection.values()) {
            if (dir == ForgeDirection.UNKNOWN) continue;
            liquidQuads[dir.ordinal()] = computeLiquidQuads(dir);
        }
    }

    private static int[] getTextureBounds(IIcon i) {
        //TODO: change the 16, to support different texturepacks like 32x32 etc...
        int textureFileHeight = (int) Math.floor(1.0F / (i.getMaxV() - i.getMinV()) * FLUID_TEX_SIZE);
        int textureFileWidth = (int) Math.floor(1.0F / (i.getMaxU() - i.getMinU()) * FLUID_TEX_SIZE);

        float left = i.getMinU() * textureFileWidth;//(i.getMaxU() - i.getMinU());
        float right = i.getMaxU() * textureFileWidth;//(i.getMaxU() - i.getMinU());
        float top = i.getMinV() * textureFileHeight;//(i.getMaxV() - i.getMinV());
        float bottom = i.getMaxV() * textureFileHeight;//(i.getMaxV() - i.getMinV());

        float t = FLUID_TEX_SIZE * (7.0f / 8.0f);
        int uOffset = Math.round((right - left) * t);
        int vOffset = Math.round((bottom - top) * t);
        return new int[]{Math.round(top), Math.round(bottom), Math.round(left), Math.round(right), uOffset, vOffset};
    }

    private void renderLiquid(ForgeDirection side) {
        for (TexturedQuad quad : liquidQuads[side.ordinal()]) {
            quad.draw(Tessellator.instance, 1.0F / 16.0f);
        }
    }

    private static TexturedQuad[] computeLiquidQuads(ForgeDirection side) {
        float rotateZAngle = 0.0F;
        float rotateYAngle = 0.0F;
        switch (side) {
            case WEST:
                break;
            case EAST:
                rotateYAngle = (float) Math.PI;
                break;
            case SOUTH:
                rotateYAngle = (float) Math.PI / 2.0F;
                break;
            case NORTH:
                rotateYAngle = (float) (Math.PI / -2.0F);
                break;
            case DOWN:
                rotateZAngle = (float) Math.PI / -2.0F;
                break;
            case UP:
                rotateZAngle = (float) Math.PI / 2.0F;
                break;
            default:
                break;
        }

        Vec3[] adapterPoints = new Vec3[8];
        for (int i = 0; i < adapterPoints.length; i++) {
            Vec3 vec = centerVectorBase[i % 4];
            adapterPoints[i] = Vec3.createVectorHelper(vec.xCoord, vec.yCoord, vec.zCoord);
            adapterPoints[i].xCoord *= hDiameter;
            adapterPoints[i].yCoord *= hDiameter;
            adapterPoints[i].zCoord *= hDiameter;
            adapterPoints[i].rotateAroundZ(rotateZAngle);
            adapterPoints[i].rotateAroundY(rotateYAngle);
            adapterPoints[i] = adapterPoints[i].addVector(8.0F, 8.0F, -8.0F);
            adapterPoints[i].xCoord += (8.0F - ((i >= 4) ? 8.0 : 0.0F)) * side.offsetX;
            adapterPoints[i].yCoord += (8.0F - ((i >= 4) ? 8.0 : 0.0F)) * side.offsetY;
            adapterPoints[i].zCoord += (8.0F - ((i >= 4) ? 8.0 : 0.0F)) * side.offsetZ;
        }

        PositionTextureVertex[] vertexPoints = new PositionTextureVertex[8];
        for (int i = 0; i < vertexPoints.length; i++) {
            vertexPoints[i] = new PositionTextureVertex(adapterPoints[i], 0.0F, 0.0F);
        }

        IIcon i = RkStuff.fluidCoolant.getStillIcon();
        int[] texBounds = getTextureBounds(i);

        float scaleU = (float) Math.floor(1.0F / (i.getMaxU() - i.getMinU()) * FLUID_TEX_SIZE);
        float scaleV = (float) Math.floor(1.0F / (i.getMaxV() - i.getMinV()) * FLUID_TEX_SIZE);

        //topLeft, topRight, bottomRight, bottomLeft {centerTopVec, centerBottomVec, centerLeftVec, centerRightVec};
        PositionTextureVertex[] side1 = new PositionTextureVertex[]{vertexPoints[3], vertexPoints[0], vertexPoints[4], vertexPoints[7]};
        PositionTextureVertex[] side2 = new PositionTextureVertex[]{vertexPoints[0], vertexPoints[2], vertexPoints[6], vertexPoints[4]};
        PositionTextureVertex[] side3 = new PositionTextureVertex[]{vertexPoints[2], vertexPoints[1], vertexPoints[5], vertexPoints[6]};
        PositionTextureVertex[] side4 = new PositionTextureVertex[]{vertexPoints[1], vertexPoints[3], vertexPoints[7], vertexPoints[5]};

        //{Math.round(top), Math.round(bottom), Math.round(left), Math.round(right), uOffset, vOffset};
        int intSide = side.ordinal();
        int index1 = (intSide % 2) == 0 ? 2 : 3;
        int index2 = (intSide % 2) == 0 ? 1 : 0;
        int index3 = (intSide % 2) == 0 ? 3 : 2;
        int index4 = (intSide % 2) == 0 ? 0 : 1;
        //new TexturedQuad(side1, left, bottom, right, top)
        TexturedQuad quad1 = new TexturedQuad(side1, texBounds[index1], texBounds[index2], texBounds[index3], texBounds[index4], scaleU, scaleV);
        TexturedQuad quad2 = new TexturedQuad(side2, texBounds[index1], texBounds[index2], texBounds[index3], texBounds[index4], scaleU, scaleV);
        TexturedQuad quad3 = new TexturedQuad(side3, texBounds[index1], texBounds[index2], texBounds[index3], texBounds[index4], scaleU, scaleV);
        TexturedQuad quad4 = new TexturedQuad(side4, texBounds[index1], texBounds[index2], texBounds[index3], texBounds[index4], scaleU, scaleV);

        return new TexturedQuad[]{quad1, quad2, quad3, quad4};

    }

}
