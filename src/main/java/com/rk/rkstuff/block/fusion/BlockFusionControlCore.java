package com.rk.rkstuff.block.fusion;

import com.rk.rkstuff.block.BlockRK;
import com.rk.rkstuff.util.Reference;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

public class BlockFusionControlCore extends BlockRK implements IFusionControlCoreBlock {

    public BlockFusionControlCore() {
        super(Material.iron, Reference.BLOCK_FUSION_CONTROL_CORE);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta == 0) return Blocks.diamond_ore.getIcon(0, 0);
        return Blocks.diamond_block.getIcon(0, 0);
    }
}
