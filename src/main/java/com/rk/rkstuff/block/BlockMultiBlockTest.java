package com.rk.rkstuff.block;

import com.rk.rkstuff.tile.TileMultiBlock;
import com.rk.rkstuff.tile.TileMultiBlockTest;
import com.rk.rkstuff.util.Reference;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockMultiBlockTest extends BlockMultiBlock {

    public BlockMultiBlockTest() {
        super(Material.iron);
        this.setBlockName(Reference.BLOCK_MULTI_BLOCK_TEST_NAME);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileMultiBlockTest();
    }

}
