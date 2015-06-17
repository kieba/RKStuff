package com.rk.rkstuff.client.gui;

import com.rk.rkstuff.boiler.ContainerBoiler;
import com.rk.rkstuff.boiler.tile.TileBoilerBaseMaster;
import com.rk.rkstuff.distribution.ContainerDistributionEnergy;
import com.rk.rkstuff.distribution.ContainerDistributionFluid;
import com.rk.rkstuff.distribution.tile.TileDistributionEnergy;
import com.rk.rkstuff.distribution.tile.TileDistributionFluid;
import com.rk.rkstuff.solar.ContainerSolar;
import com.rk.rkstuff.solar.tile.TileSolarMaster;
import com.rk.rkstuff.tank.ContainerTankInteraction;
import com.rk.rkstuff.tank.tile.TileTankInteraction;
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
        } else if(ID == Reference.GUI_ID_DISTRIBUTION_ENERGY) {
            return new ContainerDistributionEnergy(player, (TileDistributionEnergy) world.getTileEntity(x, y, z));
        } else if(ID == Reference.GUI_ID_DISTRIBUTION_FLUID) {
            return new ContainerDistributionFluid(player, (TileDistributionFluid) world.getTileEntity(x, y, z));
        } else if (ID == Reference.GUI_ID_TANK_INTERACTION) {
            return new ContainerTankInteraction(player, (TileTankInteraction) world.getTileEntity(x, y, z));
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(ID == Reference.GUI_ID_SOLAR) {
            return new GuiSolar(player, (TileSolarMaster) world.getTileEntity(x, y, z));
        } else if(ID == Reference.GUI_ID_BOILER) {
            return new GuiBoiler(player, (TileBoilerBaseMaster) world.getTileEntity(x, y, z));
        } else if(ID == Reference.GUI_ID_DISTRIBUTION_ENERGY) {
            return new GuiDistributionEnergy(player, (TileDistributionEnergy) world.getTileEntity(x, y, z));
        } else if(ID == Reference.GUI_ID_DISTRIBUTION_FLUID) {
            return new GuiDistributionFluid(player, (TileDistributionFluid) world.getTileEntity(x, y, z));
        } else if (ID == Reference.GUI_ID_TANK_INTERACTION) {
            return new GuiTankInteraction(player, (TileTankInteraction) world.getTileEntity(x, y, z));
        }
        return null;
    }
}
