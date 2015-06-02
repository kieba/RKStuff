package com.rk.rkstuff.client.gui;

import com.rk.rkstuff.container.ContainerBoiler;
import com.rk.rkstuff.container.ContainerEnergyDistribution;
import com.rk.rkstuff.container.ContainerSolar;
import com.rk.rkstuff.tile.TileBoilerBaseMaster;
import com.rk.rkstuff.tile.TileEnergyDistribution;
import com.rk.rkstuff.tile.TileSolarMaster;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(ID == Reference.GUI_ID_SOLAR) {
            return new ContainerSolar(player, (TileSolarMaster) world.getTileEntity(x, y, z));
        } else if(ID == Reference.GUI_ID_BOILER) {
            return new ContainerBoiler(player, (TileBoilerBaseMaster) world.getTileEntity(x, y, z));
        } else if(ID == Reference.GUI_ID_ENERGY_DISTRIBUTION) {
            return new ContainerEnergyDistribution(player, (TileEnergyDistribution) world.getTileEntity(x, y, z));
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(ID == Reference.GUI_ID_SOLAR) {
            return new GuiSolar(player, (TileSolarMaster) world.getTileEntity(x, y, z));
        } else if(ID == Reference.GUI_ID_BOILER) {
            return new GuiBoiler(player, (TileBoilerBaseMaster) world.getTileEntity(x, y, z));
        } else if(ID == Reference.GUI_ID_ENERGY_DISTRIBUTION) {
            return new GuiDistribution(player, (TileEnergyDistribution) world.getTileEntity(x, y, z));
        }
        return null;
    }
}
