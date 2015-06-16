package com.rk.rkstuff.fusion.tank;

import com.rk.rkstuff.core.block.BlockRK;
import com.rk.rkstuff.util.Reference;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

public class BlockFusionCore extends BlockRK implements IFusionCoreBlock {

    public BlockFusionCore() {
        super(Material.iron, Reference.BLOCK_FUSION_CORE);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta == 0) return Blocks.gold_ore.getIcon(0, 0);
        return Blocks.gold_block.getIcon(0, 0);
    }

}
