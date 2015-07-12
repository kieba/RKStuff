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
    private String description;

    public ElementMultiTank(GuiBase guiBase, int posX, int posY, int maxCapacity) {
        super(guiBase, posX, posY, null);
        tank = new FluidTank(maxCapacity);
        this.maxCapacity = maxCapacity;
    }

    public ElementMultiTank(GuiBase guiBase, int posX, int posY, int maxCapacity, String description) {
        this(guiBase, posX, posY, maxCapacity);
        this.description = description;
    }

    public void setInformationStack(CoolantStack coolantStack) {
        tank = new FluidTankCoolantAdapter(coolantStack, maxCapacity);
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
        if (description != null) {
            list.add(description);
        }
        if (isFluid) {
            super.addTooltip(list);
        } else {
            list.add(StringHelper.getFluidName(this.tank.getFluid()) + " (" + coolantStack.getFormattedString() + ")");
            list.add("" + this.tank.getFluidAmount() + " / " + this.tank.getCapacity() + " mB");
        }
    }

    private class FluidTankCoolantAdapter extends FluidTank {
        CoolantStack coolantStack;

        public FluidTankCoolantAdapter(CoolantStack stack, int capacity) {
            super(capacity);
            setFluid(new FluidStack(RkStuff.fluidCoolant, stack.getAmount()));
            coolantStack = stack;
        }

        @Override
        public int getFluidAmount() {
            return coolantStack.getAmount();
        }
    }


}
