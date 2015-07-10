package com.rk.rkstuff.client.gui;

import cofh.core.gui.element.TabConfiguration;
import com.rk.rkstuff.coolant.ContainerCoolantMixer;
import com.rk.rkstuff.coolant.tile.TileCoolantMixer;
import com.rk.rkstuff.util.Textures;
import net.minecraft.entity.player.EntityPlayer;

public class GuiCoolantMixer extends GuiRKRReconfigurable<TileCoolantMixer, ContainerCoolantMixer> {

    public GuiCoolantMixer(EntityPlayer player, TileCoolantMixer tile) {
        super(new ContainerCoolantMixer(player, tile), Textures.COOLANT_MIXER_GUI, tile);


    }

    @Override
    public void initGui() {
        super.initGui();
        TabConfiguration conf = (TabConfiguration) addTab(new TabConfiguration(this, tile));
        conf.setVisible(true);
    }

}
