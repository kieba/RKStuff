package com.rk.rkstuff.block.fusion;

import com.rk.rkstuff.block.BlockRK;
import com.rk.rkstuff.util.Reference;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

public class BlockFusionCore extends BlockRK implements IFusionCoreBlock {

    public BlockFusionCore() {
        super(Material.iron, Reference.BLOCK_FUSION_CORE);
    }

    @Override
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
        return Blocks.gold_block.getIcon(0, 0);
    }

}
