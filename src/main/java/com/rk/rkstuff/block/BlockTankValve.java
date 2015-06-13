package com.rk.rkstuff.block;

import com.rk.rkstuff.util.Reference;
import net.minecraft.block.material.Material;

public class BlockTankValve extends BlockRK implements ITankBlock {
    public BlockTankValve() {
        super(Material.iron, Reference.BLOCK_TANK_VALVE);
    }
}
