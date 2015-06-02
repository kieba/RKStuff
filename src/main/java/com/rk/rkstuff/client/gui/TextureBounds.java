package com.rk.rkstuff.client.gui;

public class TextureBounds {

    public int totalSizeX = 256, totalSizeY = 256;
    public int xPos, yPos, width, height, texPosX, texPosY, texWidth, texHeight;
    public boolean enableSelect, enableHover;
    public int index;

    public TextureBounds(int xPos, int yPos, int width, int height, int texPosX, int texPosY) {
        this(xPos, yPos, width, height, texPosX, texPosY, 0, 0);
    }

    public TextureBounds(int xPos, int yPos, int width, int height, int texPosX, int texPosY, int texWidth, int texHeight) {
        this.setPos(xPos, yPos);
        this.setSize(width, height);
        this.setTexPos(texPosX, texPosY);
        this.setTexSize(texWidth, texHeight);
    }

    public TextureBounds setPos(int x, int y) {
        this.xPos = x;
        this.yPos = y;
        return this;
    }

    public TextureBounds setSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public TextureBounds setTexPos(int x, int y) {
        this.texPosX = x;
        this.texPosY = y;
        return this;
    }

    public TextureBounds setTexSize(int width, int height) {
        this.texWidth = width;
        this.texHeight = height;
        return this;
    }

    public TextureBounds setTotalSize(int x, int y) {
        this.totalSizeX = x;
        this.totalSizeY = y;
        return this;
    }

    public TextureBounds enableSelect(boolean enableSelect) {
        this.enableSelect = enableSelect;
        return this;
    }

    public TextureBounds enableHover(boolean enableHover) {
        this.enableHover = enableHover;
        return this;
    }

    public TextureBounds setIndex(int index) {
        this.index = index;
        return this;
    }

    public TextureBounds copy() {
        return new TextureBounds(xPos, yPos, width, height, texPosX, texPosY, texWidth, texHeight).setTotalSize(totalSizeX, totalSizeY).enableHover(enableHover).enableSelect(enableSelect).setIndex(index);
    }
}

