package com.rk.rkstuff.client.gui.tab;

import cofh.core.gui.element.TabInfo;
import cofh.lib.gui.GuiBase;

public class TabInfoDynamic extends TabInfo {

    public TabInfoDynamic(GuiBase guiBase, String s) {
        super(guiBase, s);
    }

    public void setText(String text) {
        this.maxHeight = 92;
        this.myText = this.getFontRenderer().listFormattedStringToWidth(text, this.maxWidth - 16);
        this.numLines = Math.min(this.myText.size(), (this.maxHeight - 24) / this.getFontRenderer().FONT_HEIGHT);
        this.maxFirstLine = this.myText.size() - this.numLines;
    }
}
