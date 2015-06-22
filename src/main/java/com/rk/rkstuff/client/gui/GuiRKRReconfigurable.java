package com.rk.rkstuff.client.gui;

import cofh.core.gui.element.TabConfiguration;
import cofh.core.render.IconRegistry;
import cofh.lib.gui.GuiBase;
import com.rk.rkstuff.core.ContainerRK;
import com.rk.rkstuff.core.tile.TileRKReconfigurable;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public class GuiRKRReconfigurable<T extends TileRKReconfigurable, C extends ContainerRK<T>> extends GuiBase {
    protected T tile;

    public GuiRKRReconfigurable(C container, ResourceLocation resourceLocation, T tile) {
        super(container, resourceLocation);
        this.tile = tile;
        addTab(new TabConfiguration(this, tile));
    }

    public IIcon getIcon(String var1) {
        return IconRegistry.getIcon(var1);
    }
}
