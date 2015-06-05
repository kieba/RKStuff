package com.rk.rkstuff.block;

import com.rk.rkstuff.tile.TileModelTest;
import com.rk.rkstuff.util.Reference;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockModelTest extends BlockRK implements ITileEntityProvider {

    public BlockModelTest() {
        super(Material.iron);
        setBlockName(Reference.BLOCK_MODEL_TEST_NAME);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileModelTest();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        if (player.isSneaking()) {
            ((TileModelTest) world.getTileEntity(x, y, z)).addRotaion();
        } else {
            ((TileModelTest) world.getTileEntity(x, y, z)).subRotaion();
        }
        return true;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isBlockNormalCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return -1;
    }


}
