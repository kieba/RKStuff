package com.rk.rkstuff.client.gui;

import cofh.core.gui.element.TabConfiguration;
import com.rk.rkstuff.coolant.ContainerPoweredFreezer;
import com.rk.rkstuff.coolant.tile.TilePoweredFreezer;
import com.rk.rkstuff.util.Textures;
import net.minecraft.entity.player.EntityPlayer;

public class GuiPoweredFreezer extends GuiRKRReconfigurable<TilePoweredFreezer, ContainerPoweredFreezer> {

    public GuiPoweredFreezer(EntityPlayer player, TilePoweredFreezer tile) {
        super(new ContainerPoweredFreezer(player, tile), Textures.POWERED_FREEZER_GUI, tile);


    }

    @Override
    public void initGui() {
        super.initGui();
        TabConfiguration conf = (TabConfiguration) addTab(new TabConfiguration(this, tile));
        conf.setVisible(true);
    }


}
