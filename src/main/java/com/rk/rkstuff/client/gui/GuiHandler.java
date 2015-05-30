package com.rk.rkstuff.client.gui;

import com.rk.rkstuff.container.ContainerSolar;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(ID == Reference.GUI_ID_SOLAR) {
            return new ContainerSolar(player.inventory);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(ID == Reference.GUI_ID_SOLAR) {
            return new GuiSolar(player.inventory);
        }
        return null;
    }
}
