package com.rk.rkstuff.block.fusion;

import com.rk.rkstuff.block.BlockRK;
import com.rk.rkstuff.util.Reference;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

public class BlockFusionCase extends BlockRK implements IFusionCaseBlock {

    public BlockFusionCase() {
        super(Material.iron, Reference.BLOCK_FUSION_CASE);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta == 0) return Blocks.iron_ore.getIcon(0, 0);
        return Blocks.iron_block.getIcon(0, 0);
    }
}
