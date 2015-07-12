package com.rk.rkstuff.client.gui;

import cofh.core.render.IconRegistry;
import cofh.lib.gui.GuiBase;
import com.rk.rkstuff.accelerator.AcceleratorConfig;
import com.rk.rkstuff.accelerator.ContainerLHCMaster;
import com.rk.rkstuff.accelerator.tile.TileLHCMaster;
import com.rk.rkstuff.client.gui.tab.TabInfoDynamic;
import com.rk.rkstuff.coolant.CoolantStack;
import com.rk.rkstuff.helper.GuiHelper;
import com.rk.rkstuff.util.Textures;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;

import java.util.ArrayList;
import java.util.List;

public class GuiLHCMaster extends GuiBase {

    private static final int SIZE_X = 176;
    private static final int SIZE_Y = 166;

    private TileLHCMaster tile;
    private TabInfoDynamic info;

    public GuiLHCMaster(EntityPlayer player, TileLHCMaster tile) {
        super(new ContainerLHCMaster(player, tile), Textures.LHC_MASTER_GUI);
        this.tile = tile;
        this.drawInventory = false;
    }

    public IIcon getIcon(String var1) {
        return IconRegistry.getIcon(var1);
    }

    @Override
    public void initGui() {
        super.initGui();
        info = new TabInfoDynamic(this, "");
        this.addTab(info);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float tick, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(tick, mouseX, mouseY);

        this.mc.getTextureManager().bindTexture(Textures.LHC_MASTER_GUI);
        int x = (this.width - SIZE_X) / 2;
        int y = (this.height - SIZE_Y) / 2;
        //drawTexturedModalRect(x, y, 0, 0, SIZE_X, SIZE_Y);

        int energyPx = Math.round(53 * (float) tile.getStoredEnergyRF() / (float) tile.getMaxEnergyStorageRF());
        int speedPx = Math.round(80 * tile.getSpeed() / tile.getMaxSpeed());
        if (speedPx > 80) speedPx = 80;
        int coolantPx = Math.round(53 * (float) tile.getTotalCoolant() / (float) tile.getTotalMaxCoolant());

        drawTexturedModalRect(x + 12, y + 69 - energyPx, 176, 53 - energyPx, 12, energyPx);

        drawTexturedModalRect(x + 47, y + 73, 176, 53, speedPx, 5);

        drawTexturedModalRect(x + 152, y + 69 - coolantPx, 188, 53 - coolantPx, 12, coolantPx);

        if (GuiHelper.isInArea(mouseX, mouseY, x + 12, y + 16, x + 24, y + 69)) {
            //draw tooltip cool coolant
            List<String> list = new ArrayList<String>(2);
            list.add("Energy:");
            list.add(String.format("%d/%d RF", tile.getStoredEnergyRF(), tile.getMaxEnergyStorageRF()));
            this.func_146283_a(list, mouseX, mouseY);
        } else if (GuiHelper.isInArea(mouseX, mouseY, x + 47, y + 73, x + 127, y + 78)) {
            //draw tooltip hot coolant
            List<String> list = new ArrayList<String>(2);
            list.add("Speed:");
            list.add(String.format("%.2f/%.2f bps", tile.getSpeed(), tile.getMaxSpeed()));
            this.func_146283_a(list, mouseX, mouseY);
        } else if (GuiHelper.isInArea(mouseX, mouseY, x + 152, y + 16, x + 164, y + 69)) {
            //draw tooltip prod
            List<String> list = new ArrayList<String>(2);
            list.add("Coolant:");
            list.add(String.format("%d/%d mB", tile.getTotalCoolant(), tile.getTotalMaxCoolant()));
            list.add("Temperature: " + CoolantStack.toFormattedString(tile.getAvgCoolantTemp()));
            this.func_146283_a(list, mouseX, mouseY);
        }

        StringBuffer sb = new StringBuffer();
        if (tile.isToSlow()) {
            sb.append("To less energy!\n");
        }
        sb.append("Efficiency: ");
        sb.append(String.format("%.2f%%\n", tile.getAccelerator().getEfficiency() * 100));
        sb.append("MaxSpeed: ");
        sb.append(String.format("%.2f bps\n", tile.getMaxSpeed()));
        for (int i = 0; i < AcceleratorConfig.ACCELERATOR_SIDE_COUNT; i++) {
            sb.append(String.format("Coolant %d: \n", i));
            CoolantStack stack = tile.getCoolantStack(i);
            sb.append(String.format("%d @ %s\n", stack.getAmount(), CoolantStack.toFormattedString(stack.getTemperature())));
        }

        info.setText(sb.toString());
    }
}
