package com.rk.rkstuff.block.fusion;

import com.rk.rkstuff.block.BlockRK;
import com.rk.rkstuff.util.Reference;
import net.minecraft.block.material.Material;

public class BlockFusionCase extends BlockRK implements IFusionCaseBlock {

    public BlockFusionCase() {
        super(Material.iron, Reference.BLOCK_FUSION_CASE);
    }

}
