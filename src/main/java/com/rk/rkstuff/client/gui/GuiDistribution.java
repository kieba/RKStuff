package com.rk.rkstuff.client.gui;

import com.rk.rkstuff.distribution.tile.TileDistribution;
import com.rk.rkstuff.network.PacketHandler;
import com.rk.rkstuff.network.message.IGuiActionMessage;
import com.rk.rkstuff.util.Textures;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;
import rk.com.core.io.ByteArrayHelper;
import rk.com.core.io.IOStream;

public abstract class GuiDistribution<T extends TileDistribution & IGuiActionMessage> extends GuiContainer {

    private static final int TEXTURE_WIDTH = 300;
    private static final int TEXTURE_HEIGHT = 300;
    private static final int SIZE_X = 176;
    private static final int SIZE_Y = 166;

    private static final int PAGE_X = 40;
    private static final int PAGE_Y = 6;

    private static final int PAGE_WIDTH = 95;
    private static final int PAGE_HEIGHT = 65;

    private GuiButtonRK[] sideButtons = new GuiButtonRK[6];
    private GuiButtonRK[] priorityButtons = new GuiButtonRK[6];
    private GuiButtonRK[] outputButtons = new GuiButtonRK[13];
    private GuiButtonRK[] pageButtons = new GuiButtonRK[2];

    protected T tile;
    private int selectedOutputSide = 0;
    private int page = 0;

    public GuiDistribution(Container container, T tile) {
        super(container);
        this.tile = tile;
    }

    @Override
    public void initGui() {
        super.initGui();

        ResourceLocation tex = Textures.DISTRIBUTION_GUI;
        int buttonId = 0;

        TextureBounds defaultBounds = null, bounds = null;
        TextureBounds defaultOverlay = null, overlay = null;

        int overlayOffsetX = 3; //4
        int overlayOffsetY = 2; //3
        
        int overlaySizeX = 5; //3
        int overlaySizeY = 7; //5

        /* SIDE BUTTONS */
        defaultOverlay = new TextureBounds(0, 0, overlaySizeX, overlaySizeY, 176, 0, 8, 12).setTotalSize(TEXTURE_WIDTH, TEXTURE_HEIGHT).enableHover(true);
        defaultBounds = new TextureBounds(0, 0, 11, 11, 194, 84, 11, 11).setTotalSize(TEXTURE_WIDTH, TEXTURE_HEIGHT).enableHover(true);

        //DOWN
        bounds = defaultBounds.copy().setPos(guiLeft + 114, guiTop + 53);
        overlay = defaultOverlay.copy().setPos(bounds.xPos + overlayOffsetX, bounds.yPos + overlayOffsetY);
        sideButtons[ForgeDirection.DOWN.ordinal()] = new GuiButtonRK(ForgeDirection.DOWN.ordinal(), tex, bounds, overlay);

        //UP
        bounds = defaultBounds.copy().setPos(guiLeft + 114, guiTop + 27);
        overlay = defaultOverlay.copy().setPos(bounds.xPos + overlayOffsetX, bounds.yPos + overlayOffsetY);
        sideButtons[ForgeDirection.UP.ordinal()] = new GuiButtonRK(ForgeDirection.UP.ordinal(), tex, bounds, overlay);

        //NORTH
        bounds = defaultBounds.copy().setPos(guiLeft + 101, guiTop + 27);
        overlay = defaultOverlay.copy().setPos(bounds.xPos + overlayOffsetX, bounds.yPos + overlayOffsetY);
        sideButtons[ForgeDirection.NORTH.ordinal()] = new GuiButtonRK(ForgeDirection.NORTH.ordinal(), tex, bounds, overlay);

        //SOUTH
        bounds = defaultBounds.copy().setPos(guiLeft + 101, guiTop + 53);
        overlay = defaultOverlay.copy().setPos(bounds.xPos + overlayOffsetX, bounds.yPos + overlayOffsetY);
        sideButtons[ForgeDirection.SOUTH.ordinal()] = new GuiButtonRK(ForgeDirection.SOUTH.ordinal(), tex, bounds, overlay);

        //WEST
        bounds = defaultBounds.copy().setPos(guiLeft + 88, guiTop + 40);
        overlay = defaultOverlay.copy().setPos(bounds.xPos + overlayOffsetX, bounds.yPos + overlayOffsetY);
        sideButtons[ForgeDirection.WEST.ordinal()] = new GuiButtonRK(ForgeDirection.WEST.ordinal(), tex, bounds, overlay);

        //EAST
        bounds = defaultBounds.copy().setPos(guiLeft + 114, guiTop + 40);
        overlay = defaultOverlay.copy().setPos(bounds.xPos + overlayOffsetX, bounds.yPos + overlayOffsetY);
        sideButtons[ForgeDirection.EAST.ordinal()] = new GuiButtonRK(ForgeDirection.EAST.ordinal(), tex, bounds, overlay);


        for (int i = 0; i < sideButtons.length; i++) {
            sideButtons[i].visible = false;
            this.buttonList.add(sideButtons[i]);
        }

        /* PRIORITY BUTTONS */
        defaultOverlay = new TextureBounds(0, 0, overlaySizeX, overlaySizeY, 176, 24, 8, 12).setTotalSize(TEXTURE_WIDTH, TEXTURE_HEIGHT).enableHover(true);
        defaultBounds = new TextureBounds(0, 0, 11, 11, 194, 84, 11, 11).setTotalSize(TEXTURE_WIDTH, TEXTURE_HEIGHT).enableHover(true);

        //DOWN
        bounds = defaultBounds.copy().setPos(guiLeft + 114, guiTop + 53);
        overlay = defaultOverlay.copy().setPos(bounds.xPos + overlayOffsetX, bounds.yPos + overlayOffsetY);
        priorityButtons[ForgeDirection.DOWN.ordinal()] = new GuiButtonRK(6 + ForgeDirection.DOWN.ordinal(), tex, bounds, overlay);

        //UP
        bounds = defaultBounds.copy().setPos(guiLeft + 114, guiTop + 27);
        overlay = defaultOverlay.copy().setPos(bounds.xPos + overlayOffsetX, bounds.yPos + overlayOffsetY);
        priorityButtons[ForgeDirection.UP.ordinal()] = new GuiButtonRK(6 + ForgeDirection.UP.ordinal(), tex, bounds, overlay);

        //NORTH
        bounds = defaultBounds.copy().setPos(guiLeft + 101, guiTop + 27);
        overlay = defaultOverlay.copy().setPos(bounds.xPos + overlayOffsetX, bounds.yPos + overlayOffsetY);
        priorityButtons[ForgeDirection.NORTH.ordinal()] = new GuiButtonRK(6 + ForgeDirection.NORTH.ordinal(), tex, bounds, overlay);

        //SOUTH
        bounds = defaultBounds.copy().setPos(guiLeft + 101, guiTop + 53);
        overlay = defaultOverlay.copy().setPos(bounds.xPos + overlayOffsetX, bounds.yPos + overlayOffsetY);
        priorityButtons[ForgeDirection.SOUTH.ordinal()] = new GuiButtonRK(6 + ForgeDirection.SOUTH.ordinal(), tex, bounds, overlay);

        //WEST
        bounds = defaultBounds.copy().setPos(guiLeft + 88, guiTop + 40);
        overlay = defaultOverlay.copy().setPos(bounds.xPos + overlayOffsetX, bounds.yPos + overlayOffsetY);
        priorityButtons[ForgeDirection.WEST.ordinal()] = new GuiButtonRK(6 + ForgeDirection.WEST.ordinal(), tex, bounds, overlay);

        //EAST
        bounds = defaultBounds.copy().setPos(guiLeft + 114, guiTop + 40);
        overlay = defaultOverlay.copy().setPos(bounds.xPos + overlayOffsetX, bounds.yPos + overlayOffsetY);
        priorityButtons[ForgeDirection.EAST.ordinal()] = new GuiButtonRK(6 + ForgeDirection.EAST.ordinal(), tex, bounds, overlay);

        for (int i = 0; i < priorityButtons.length; i++) {
            priorityButtons[i].visible = false;
            this.buttonList.add(priorityButtons[i]);
        }

        /* OUTPUT BUTTONS */
        defaultOverlay = new TextureBounds(0, 0, overlaySizeX, overlaySizeY, 176, 48, 8, 12).setTotalSize(TEXTURE_WIDTH, TEXTURE_HEIGHT).enableHover(true);
        defaultBounds = new TextureBounds(0, 0, 11, 11, 194, 84, 11, 11).setTotalSize(TEXTURE_WIDTH, TEXTURE_HEIGHT).enableHover(true).enableSelect(true);

        //DOWN
        bounds = defaultBounds.copy().setPos(guiLeft + 75, guiTop + 46);
        overlay = defaultOverlay.copy().setPos(bounds.xPos + overlayOffsetX, bounds.yPos + overlayOffsetY).setIndex(5);
        outputButtons[ForgeDirection.DOWN.ordinal()] = new GuiButtonRK(12 + ForgeDirection.DOWN.ordinal(), tex, bounds, overlay);

        //UP
        bounds = defaultBounds.copy().setPos(guiLeft + 75, guiTop + 20);
        overlay = defaultOverlay.copy().setPos(bounds.xPos + overlayOffsetX, bounds.yPos + overlayOffsetY).setIndex(4);
        outputButtons[ForgeDirection.UP.ordinal()] = new GuiButtonRK(12 + ForgeDirection.UP.ordinal(), tex, bounds, overlay);

        //NORTH
        bounds = defaultBounds.copy().setPos(guiLeft + 62, guiTop + 20);
        overlay = defaultOverlay.copy().setPos(bounds.xPos + overlayOffsetX, bounds.yPos + overlayOffsetY).setIndex(0);
        outputButtons[ForgeDirection.NORTH.ordinal()] = new GuiButtonRK(12 + ForgeDirection.NORTH.ordinal(), tex, bounds, overlay);

        //SOUTH
        bounds = defaultBounds.copy().setPos(guiLeft + 62, guiTop + 46);
        overlay = defaultOverlay.copy().setPos(bounds.xPos + overlayOffsetX, bounds.yPos + overlayOffsetY).setIndex(2);
        outputButtons[ForgeDirection.SOUTH.ordinal()] = new GuiButtonRK(12 + ForgeDirection.SOUTH.ordinal(), tex, bounds, overlay);

        //WEST
        bounds = defaultBounds.copy().setPos(guiLeft + 49, guiTop + 33);
        overlay = defaultOverlay.copy().setPos(bounds.xPos + overlayOffsetX, bounds.yPos + overlayOffsetY).setIndex(3);
        outputButtons[ForgeDirection.WEST.ordinal()] = new GuiButtonRK(12 + ForgeDirection.WEST.ordinal(), tex, bounds, overlay);

        //EAST
        bounds = defaultBounds.copy().setPos(guiLeft + 75, guiTop + 33);
        overlay = defaultOverlay.copy().setPos(bounds.xPos + overlayOffsetX, bounds.yPos + overlayOffsetY).setIndex(1);
        outputButtons[ForgeDirection.EAST.ordinal()] = new GuiButtonRK(12 + ForgeDirection.EAST.ordinal(), tex, bounds, overlay);

        buttonId = 18;

        //ABSOLUTE - PERCENT
        bounds = defaultBounds.copy().setPos(guiLeft + 82, guiTop + 60).enableSelect(false);
        overlay = defaultOverlay.copy().setPos(bounds.xPos + 1, bounds.yPos + 3).setSize(9, 5).setTexPos(176, 72).setTexSize(12, 6);
        outputButtons[6 + 0] = new GuiButtonRK(buttonId++, tex, bounds, overlay);

        //---
        bounds = new TextureBounds(guiLeft + 40, guiTop + 61, 17, 9, 176, 118, 17, 9).enableHover(true).setTotalSize(TEXTURE_WIDTH, TEXTURE_HEIGHT);
        outputButtons[6 + 1] = new GuiButtonRK(buttonId++, tex, bounds);

        //--
        bounds = new TextureBounds(guiLeft + 58, guiTop + 61, 13, 9, 193, 118, 13, 9).enableHover(true).setTotalSize(TEXTURE_WIDTH, TEXTURE_HEIGHT);
        outputButtons[6 + 2] = new GuiButtonRK(buttonId++, tex, bounds);

        //-
        bounds = new TextureBounds(guiLeft + 72, guiTop + 61, 9, 9, 206, 118, 9, 9).enableHover(true).setTotalSize(TEXTURE_WIDTH, TEXTURE_HEIGHT);
        outputButtons[6 + 3] = new GuiButtonRK(buttonId++, tex, bounds);

        //+
        bounds = new TextureBounds(guiLeft + 94, guiTop + 61, 9, 9, 215, 118, 9, 9).enableHover(true).setTotalSize(TEXTURE_WIDTH, TEXTURE_HEIGHT);
        outputButtons[6 + 4] = new GuiButtonRK(buttonId++, tex, bounds);

        //++
        bounds = new TextureBounds(guiLeft + 104, guiTop + 61, 13, 9, 224, 118, 13, 9).enableHover(true).setTotalSize(TEXTURE_WIDTH, TEXTURE_HEIGHT);
        outputButtons[6 + 5] = new GuiButtonRK(buttonId++, tex, bounds);

        //+++
        bounds = new TextureBounds(guiLeft + 118, guiTop + 61, 17, 9, 237, 118, 17, 9).enableHover(true).setTotalSize(TEXTURE_WIDTH, TEXTURE_HEIGHT);
        outputButtons[6 + 6] = new GuiButtonRK(buttonId++, tex, bounds);

        for (int i = 0; i < outputButtons.length; i++) {
            outputButtons[i].visible = false;
            this.buttonList.add(outputButtons[i]);
        }

        /* PAGE BUTTONS */
        defaultBounds = new TextureBounds(guiLeft + 26, guiTop + 37, 9, 17, 185, 84, 9, 17).setTotalSize(TEXTURE_WIDTH, TEXTURE_HEIGHT).enableHover(true);
        pageButtons[0] = new GuiButtonRK(buttonId++, tex, defaultBounds);
        pageButtons[0].visible = true;

        defaultBounds = new TextureBounds(guiLeft + 140, guiTop + 37, 9, 17, 176, 84, 9, 17).setTotalSize(TEXTURE_WIDTH, TEXTURE_HEIGHT).enableHover(true);
        pageButtons[1] = new GuiButtonRK(buttonId++, tex, defaultBounds);
        pageButtons[1].visible = true;

        this.buttonList.add(pageButtons[0]);
        this.buttonList.add(pageButtons[1]);

        setPage(0);
    }

    private void setPage(int page) {
        if(page > 2) page = 0;
        if(page < 0) page = 2;

        this.page = page;
        for (int i = 0; i < sideButtons.length; i++) {
            sideButtons[i].visible = page == 0;
        }
        for (int i = 0; i < priorityButtons.length; i++) {
            priorityButtons[i].visible = page == 1;
        }
        for (int i = 0; i < outputButtons.length; i++) {
            outputButtons[i].visible = page == 2;
            outputButtons[i].setSelected(i == selectedOutputSide);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        int id = button.id;
        GuiButtonRK btn = (GuiButtonRK) button;
        if(id >= 0 && id < 6) {
            //SIDE BUTTONS
            PacketHandler.sendGuiActionMessage(tile, new IOStream(ByteArrayHelper.toByteArray(id)));
        } else if(id >= 6 && id < 12) {
            //PRIORITY BUTTONS
            PacketHandler.sendGuiActionMessage(tile, new IOStream(ByteArrayHelper.toByteArray(id)));
        } else if(id >= 12 && id < 18) {
            //OUTPUT BUTTONS (sides)
            for (int i = 0; i < 6; i++) {
                outputButtons[i].setSelected(false);
            }
            btn.setSelected(true);
            selectedOutputSide = id - 12;
        } else if(id >= 18 && id < 25) {
            //OUTPUT BUTTONS (other)
            IOStream data = new IOStream();
            data.writeLast(id);
            data.writeLast(selectedOutputSide);
            PacketHandler.sendGuiActionMessage(tile, data);
        } else if(id >= 25 && id < 27) {
            //PAGE BUTTONS
            if(id == 26) { //left
                setPage(page + 1);
            }  else { //right
                setPage(page - 1);
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float tick, int mouseX, int mouseY) {
        this.mc.getTextureManager().bindTexture(Textures.DISTRIBUTION_GUI);
        int x = (this.width - SIZE_X) / 2;
        int y = (this.height - SIZE_Y) / 2;
        func_146110_a(x, y, 0, 0, SIZE_X, SIZE_Y, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        //draw the page
        func_146110_a(x + PAGE_X, y + PAGE_Y, page * (PAGE_WIDTH + 1), SIZE_Y, PAGE_WIDTH, PAGE_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        //update buttons
        int[] sides = tile.getSides();
        for (int i = 0; i < 6; i++) {
            sideButtons[i].setOverlayIndex(sides[i]);
        }

        int[] priorities = tile.getPriorities();
        for (int i = 0; i < 6; i++) {
            priorityButtons[i].setOverlayIndex(priorities[i]);
        }

        outputButtons[6].setOverlayIndex(tile.isOutputLimitRelative() ? 0 : 1);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        float scale = 0.75f;
        float invScale = (1.0f/scale);
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, 1.0f);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if(page == 2) {
            int x = Math.round(91 * invScale);
            int y = 21;
            if(tile.isOutputLimitRelative()) {
                float[] output = tile.getMaxOutputRel();
                for (int i = 0; i < 6; i++) {
                    fontRendererObj.drawString(formatRel(i, output[i]),  x, Math.round((y + i * 6) * invScale), 4210752);
                }
            } else {
                int[] output = tile.getMaxOutputAbs();
                for (int i = 0; i < 6; i++) {
                    fontRendererObj.drawString(formatAbs(i, output[i]), x, Math.round((y + i * 6) * invScale), 4210752);
                }
            }
        }
        GL11.glPopMatrix();

        String str = getAvgOutputString(tile);
        int strWidth = fontRendererObj.getStringWidth(str);
        fontRendererObj.drawString(str, 87 - (strWidth / 2), 73, 4210752);
    }

    private String formatRel(int side, float value) {
        ForgeDirection  dir = ForgeDirection.VALID_DIRECTIONS[side];
        value *= 100;
        char c = dir.name().toUpperCase().charAt(0);
        return String.format("%c: %.1f%%", c, value);
    }

    private String formatAbs(int side, int value) {
        ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[side];
        char c = dir.name().toUpperCase().charAt(0);
        if(value == TileDistribution.ABS_OUTPUT_INFINITE) {
            return String.format("%c: Infinite", c);
        }
        return String.format("%c: %,d", c, value);
    }

    protected abstract String getAvgOutputString(T tile);
}