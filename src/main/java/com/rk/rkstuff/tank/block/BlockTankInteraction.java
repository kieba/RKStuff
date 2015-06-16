package com.rk.rkstuff.tank.block;

import com.rk.rkstuff.core.block.BlockRK;
import com.rk.rkstuff.util.Reference;
import net.minecraft.block.material.Material;

public class BlockTankInteraction extends BlockRK implements ITankBlock {
    public BlockTankInteraction() {
        super(Material.iron, Reference.BLOCK_TANK_INTERACTION);
    }
}