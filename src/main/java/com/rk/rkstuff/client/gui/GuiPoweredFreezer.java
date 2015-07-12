package com.rk.rkstuff.client.gui;

import cofh.lib.gui.element.ElementButtonManaged;
import cofh.lib.gui.element.ElementEnergyStored;
import com.rk.rkstuff.client.gui.elements.ElementMultiTank;
import com.rk.rkstuff.client.gui.elements.ElementTextFieldTemp;
import com.rk.rkstuff.coolant.ContainerPoweredFreezer;
import com.rk.rkstuff.coolant.CoolantStack;
import com.rk.rkstuff.coolant.tile.TilePoweredFreezer;
import com.rk.rkstuff.network.PacketHandler;
import com.rk.rkstuff.util.RKConfig;
import com.rk.rkstuff.util.RKLog;
import com.rk.rkstuff.util.Textures;
import net.minecraft.entity.player.EntityPlayer;
import rk.com.core.io.IOStream;

public class GuiPoweredFreezer extends GuiRKRReconfigurable<TilePoweredFreezer, ContainerPoweredFreezer> {
    private ElementMultiTank elementMultiTank;
    private ElementTextFieldTemp elementTextFieldTemp;

    public GuiPoweredFreezer(EntityPlayer player, TilePoweredFreezer tile) {
        super(new ContainerPoweredFreezer(player, tile), Textures.POWERED_FREEZER_GUI, tile);
        name = "Powered Freezer";
    }

    @Override
    public void initGui() {
        super.initGui();
        elementMultiTank = new ElementMultiTank(this, 8, 10, tile.getMaxCoolantStorage());
        elementMultiTank.setInformationStack(tile.getCoolantStack());
        addElement(elementMultiTank);

        addElement(new ElementEnergyStored(this, 154, 9, tile.getEnergyStorage()));

        elementTextFieldTemp = new ElementTextFieldTemp(this, 50, 35, 45, 10);
        addElement(elementTextFieldTemp);
        elementTextFieldTemp.setText("" + (RKConfig.useCelsius ? CoolantStack.toCelsius(tile.getTargetTemp()) :
                CoolantStack.toFahrenheit(tile.getTargetTemp())));
        addElement(new ElementButtonManaged(this, 105, 34, 30, 12, "SET") {
            @Override
            public void onClick() {
                try {
                    IOStream data = new IOStream();
                    data.writeLast(0);
                    data.writeLast(elementTextFieldTemp.getKelvin());
                    PacketHandler.sendGuiActionMessage(tile, data);
                } catch (Exception e) {
                    RKLog.error(e);
                }
            }
        });
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int i, int i1) {
        super.drawGuiContainerForegroundLayer(i, i1);
        fontRendererObj.drawString(String.format("Target Temp: %s", CoolantStack.toFormattedString(tile.getTargetTemp())), 30, 20, 4210752);
    }
}
