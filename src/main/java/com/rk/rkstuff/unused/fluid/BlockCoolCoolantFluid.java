package com.rk.rkstuff.unused.fluid;

import com.rk.rkstuff.util.Reference;
import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.Fluid;

public class BlockCoolCoolantFluid extends BlockFluid {
    public BlockCoolCoolantFluid(Fluid fluid) {
        super(fluid, Material.water, Reference.FLUID_COOL_COOLANT);
    }
}