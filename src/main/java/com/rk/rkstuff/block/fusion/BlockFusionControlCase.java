package com.rk.rkstuff.block.fusion;

import com.rk.rkstuff.block.BlockRK;
import com.rk.rkstuff.util.Reference;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

public class BlockFusionControlCase extends BlockRK implements IFusionControlCaseBlock {

    public BlockFusionControlCase() {
        super(Material.iron, Reference.BLOCK_FUSION_CONTROL_CASE);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta == 0) return Blocks.coal_ore.getIcon(0, 0);
        return Blocks.coal_block.getIcon(0, 0);
    }
}
