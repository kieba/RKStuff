package com.rk.rkstuff.client.gui.elements;

import cofh.lib.gui.GuiBase;
import cofh.lib.gui.element.ElementTextField;
import com.rk.rkstuff.coolant.CoolantStack;
import com.rk.rkstuff.util.RKConfig;

public class ElementTextFieldTemp extends ElementTextField {
    public ElementTextFieldTemp(GuiBase guiBase, int posX, int posY, int sizeX, int sizeY) {
        super(guiBase, posX, posY, sizeX, sizeY);
    }

    public ElementTextFieldTemp(GuiBase guiBase, int posX, int posY, int sizeX, int sizeY, short maxLength) {
        super(guiBase, posX, posY, sizeX, sizeY, maxLength);
    }

    @Override
    public boolean isAllowedCharacter(char c) {
        return Character.isDigit(c) || c == '.' || c == ',' || c == '-';
    }

    public float getKelvin() {
        if (RKConfig.useCelsius) {
            return CoolantStack.celsiusToKelvin(Float.parseFloat(getText()));
        } else {
            return CoolantStack.fahrenheitToCelsius(Float.parseFloat(getText()));
        }
    }
}
