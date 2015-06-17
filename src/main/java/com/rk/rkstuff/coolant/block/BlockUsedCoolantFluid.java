package com.rk.rkstuff.coolant.block;

import com.rk.rkstuff.util.Reference;
import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.Fluid;

public class BlockUsedCoolantFluid extends BlockFluid {
    public BlockUsedCoolantFluid(Fluid fluid) {
        super(fluid, Material.water, Reference.FLUID_USED_COOLANT);
    }
}