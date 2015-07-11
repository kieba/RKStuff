package com.rk.rkstuff.client.gui;

import cofh.core.gui.element.TabInfo;
import cofh.core.render.IconRegistry;
import cofh.lib.gui.GuiBase;
import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.client.gui.elements.ElementMultiTank;
import com.rk.rkstuff.coolant.CoolantStack;
import com.rk.rkstuff.tank.ContainerTankInteraction;
import com.rk.rkstuff.tank.tile.TileTankInteraction;
import com.rk.rkstuff.util.Textures;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

public class GuiTankInteraction extends GuiBase {
    private TileTankInteraction tile;
    private static final int SIZE_X = 176;
    private static final int SIZE_Y = 166;
    private static final int MAX = 53;
    private ElementMultiTank elementMultiTank;

    public GuiTankInteraction(EntityPlayer player, TileTankInteraction tile) {
        super(new ContainerTankInteraction(player, tile), Textures.TANK_INTERACTION_GUI);
        this.tile = tile;

    }

    @Override
    public void initGui() {
        super.initGui();
        elementMultiTank = new ElementMultiTank(this, 10,10,tile.getMaster().getMaxStorage());
        addElement(elementMultiTank);
        addTab(new TabInfo(this, "test"));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float tick, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(tick, mouseX, mouseY);
        int x = (this.width - SIZE_X) / 2;
        int y = (this.height - SIZE_Y) / 2;


        //fontRendererObj.drawString("Storage: " + ((tile.getMaster() != null) ? tile.getMaster().getCurrentStorage() : "none"), x + 10, y + 10, 0x00000000);
    }

    @Override
    protected void updateElementInformation() {
        super.updateElementInformation();
        if(tile.getMaster().isFluidStack()){
            elementMultiTank.setInformationStack(tile.getMaster().getCurrentFluidStack());
        } else if(tile.getMaster().isCoolantStack()){
            elementMultiTank.setInformationStack(tile.getMaster().getCurrentCoolantStack());
        } else {
            elementMultiTank.removeInformationStack();
        }
    }

    public IIcon getIcon(String var1) {
        return IconRegistry.getIcon(var1);
    }



    private class FluidTankFluidAdapter implements IFluidTank {
        private FluidStack stack;

        private FluidTankFluidAdapter(FluidStack stack){
            this.stack = stack;
        }

        @Override
        public FluidStack getFluid() {
            return stack;
        }

        @Override
        public int getFluidAmount() {
            return stack.amount;
        }

        @Override
        public int getCapacity() {
            return tile.getMaster().getMaxStorage();
        }

        @Override
        public FluidTankInfo getInfo() {
            return new FluidTankInfo(getFluid(), tile.getMaster().getMaxStorage());
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            return 0;
        }

        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            return null;
        }
    }
    private class FluidTankCoolantAdapter implements IFluidTank {
        private CoolantStack stack;

        private FluidTankCoolantAdapter(CoolantStack stack){
            this.stack = stack;
        }

        @Override
        public FluidStack getFluid() {
            return new FluidStack(RkStuff.fluidCoolant, stack.getAmount());
        }

        @Override
        public int getFluidAmount() {
            return stack.getAmount();
        }

        @Override
        public int getCapacity() {
            return tile.getMaster().getMaxStorage();
        }

        @Override
        public FluidTankInfo getInfo() {
            return new FluidTankInfo(getFluid(), tile.getMaster().getMaxStorage());
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            return 0;
        }

        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            return null;
        }
    }
}
