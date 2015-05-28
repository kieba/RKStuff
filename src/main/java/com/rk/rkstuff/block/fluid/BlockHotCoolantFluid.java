package com.rk.rkstuff.block.fluid;

import com.rk.rkstuff.util.Reference;
import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.Fluid;

public class BlockHotCoolantFluid extends BlockFluid {
    public BlockHotCoolantFluid(Fluid fluid) {
        super(fluid, Material.lava, Reference.FLUID_HOT_COOLANT_NAME);
    }
}