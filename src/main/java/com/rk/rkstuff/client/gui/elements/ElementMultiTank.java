package com.rk.rkstuff.client.gui.elements;

import cofh.lib.gui.GuiBase;
import cofh.lib.gui.element.ElementFluidTank;
import cofh.lib.util.helpers.StringHelper;
import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.coolant.CoolantStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import java.util.List;

public class ElementMultiTank extends ElementFluidTank {
    private int maxCapacity;
    private CoolantStack coolantStack;
    private boolean isFluid = true;

    public ElementMultiTank(GuiBase guiBase, int i, int i1, int maxCapacity) {
        super(guiBase, i, i1, null);
        tank = new FluidTank(maxCapacity);
        this.maxCapacity = maxCapacity;
    }

    public void setInformationStack(CoolantStack coolantStack) {
        tank = new FluidTank(new FluidStack(RkStuff.fluidCoolant, coolantStack.getAmount()), maxCapacity);
        this.coolantStack = coolantStack;
        isFluid = false;
    }

    public void setInformationStack(FluidStack fluidStack) {
        tank = new FluidTank(fluidStack, maxCapacity);
        this.coolantStack = null;
        isFluid = true;
    }

    public void removeInformationStack() {
        tank = new FluidTank(maxCapacity);
        this.coolantStack = null;
        isFluid = true;
    }

    @Override
    public void addTooltip(List<String> list) {
        if (isFluid) {
            super.addTooltip(list);
        } else {
            list.add(StringHelper.getFluidName(this.tank.getFluid()) + " (" + coolantStack.getFormattedString() + ")");
            list.add("" + this.tank.getFluidAmount() + " / " + this.tank.getCapacity() + " mb");
        }

    }
}
