package com.rk.rkstuff.client.gui;

import cofh.lib.gui.element.ElementButtonManaged;
import com.rk.rkstuff.client.gui.elements.ElementMultiTank;
import com.rk.rkstuff.client.gui.elements.ElementTextFieldTemp;
import com.rk.rkstuff.coolant.ContainerCoolantMixer;
import com.rk.rkstuff.coolant.CoolantStack;
import com.rk.rkstuff.coolant.tile.TileCoolantMixer;
import com.rk.rkstuff.network.PacketHandler;
import com.rk.rkstuff.util.RKConfig;
import com.rk.rkstuff.util.RKLog;
import com.rk.rkstuff.util.Textures;
import net.minecraft.entity.player.EntityPlayer;
import rk.com.core.io.IOStream;

public class GuiCoolantMixer extends GuiRKRReconfigurable<TileCoolantMixer, ContainerCoolantMixer> {
    private ElementMultiTank elementMultiTankInput1;
    private ElementMultiTank elementMultiTankInput2;
    private ElementMultiTank elementMultiTankOutput;

    private ElementTextFieldTemp elementTextFieldTemp;

    public GuiCoolantMixer(EntityPlayer player, TileCoolantMixer tile) {
        super(new ContainerCoolantMixer(player, tile), Textures.COOLANT_MIXER_GUI, tile);
        name = "Mixer";
    }

    @Override
    public void initGui() {
        super.initGui();

        elementMultiTankInput1 = new ElementMultiTank(this, 8, 10, tile.getMaxStorageInput(), "Input1:");
        elementMultiTankInput1.setInformationStack(tile.getCoolantStackRes1());
        addElement(elementMultiTankInput1);

        elementMultiTankInput2 = new ElementMultiTank(this, 27, 10, tile.getMaxStorageInput(), "Input2:");
        elementMultiTankInput2.setInformationStack(tile.getCoolantStackRes2());
        addElement(elementMultiTankInput2);

        elementMultiTankOutput = new ElementMultiTank(this, 49, 10, tile.getMaxStorageOutput(), "Output:");
        elementMultiTankOutput.setInformationStack(tile.getCoolantStackProd());
        addElement(elementMultiTankOutput);

        elementTextFieldTemp = new ElementTextFieldTemp(this, 75, 50, 45, 10);
        addElement(elementTextFieldTemp);
        elementTextFieldTemp.setText(String.format("%.2f", RKConfig.useCelsius ? CoolantStack.toCelsius(tile.getTargetTemperature()) :
                CoolantStack.toFahrenheit(tile.getTargetTemperature())));
        addElement(new ElementButtonManaged(this, 130, 49, 30, 12, "SET") {
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
        fontRendererObj.drawString("Target Temp:", 75, 25, 4210752);
        fontRendererObj.drawString(CoolantStack.toFormattedString(tile.getTargetTemperature()), 75, 35, 4210752);
    }

}
