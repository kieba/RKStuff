package com.rk.rkstuff.block.fluid;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

public class BlockCoolCoolantFluid extends BlockFluid {
    public BlockCoolCoolantFluid(Fluid fluid) {
        super(fluid, Material.water, "coolCoolant");
    }
}