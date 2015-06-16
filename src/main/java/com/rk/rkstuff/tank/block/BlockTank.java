package com.rk.rkstuff.tank.block;

import com.rk.rkstuff.core.block.BlockRK;
import com.rk.rkstuff.helper.MultiBlockHelper;
import com.rk.rkstuff.tank.tile.TileTankAdapter;
import com.rk.rkstuff.util.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockTank extends BlockRK implements ITankBlock {
    public BlockTank() {
        super(Material.glass, Reference.BLOCK_TANK);
    }

    protected BlockTank(Material material, String blockName) {
        super(material, blockName);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta == 0) {
            return Blocks.coal_ore.getIcon(side, meta);
        } else {
            return Blocks.glass.getIcon(side, meta);
        }
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0) {
            return Blocks.coal_ore.getIcon(world, x, y, z, side);
        } else {
            return Blocks.glass.getIcon(world, x, y, z, side);
        }
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    private TileTankAdapter getTankAdapter(IBlockAccess access, int x, int y, int z, int meta) {
        if (meta == 0) return null;
        MultiBlockHelper.Bounds tmp = new MultiBlockHelper.Bounds(x, y, z);
        for (ForgeDirection direction : new ForgeDirection[]{ForgeDirection.NORTH, ForgeDirection.EAST, ForgeDirection.SOUTH, ForgeDirection.WEST}) {
            int i = 1;
            while (isValidTankBlock(access, x + direction.offsetX * i, y, z + direction.offsetZ * i)) {
                i++;
            }
            i--;
            tmp.add(x + direction.offsetX * i, y, z + direction.offsetZ * i);
        }

        for (MultiBlockHelper.Bounds.BlockIterator.BoundsPos pos : tmp) {
            Block block = access.getBlock(pos.x, pos.y, pos.z);
            if (block instanceof BlockTankAdapter) {
                return (TileTankAdapter) access.getTileEntity(pos.x, pos.y, pos.z);
            }
        }

        int i = 1;
        while (access.getBlock(tmp.getMinX(), tmp.getMinY() - i, tmp.getMinZ()) instanceof ITankBlock) {
            i++;
        }
        i--;

        if (i == 0) return null;

        return getTankAdapter(access, tmp.getMinX(), tmp.getMinY() - i, tmp.getMinZ(), meta);
    }

    public boolean isValidTankBlock(IBlockAccess world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        return block instanceof ITankBlock;
    }

    @Override
    public void onNeighborBlockChange(World access, int x, int y, int z, Block oldBlock) {
        TileTankAdapter adapter = getTankAdapter(access, x, y, z, access.getBlockMetadata(x, y, z));
        if (adapter != null) {
            if (!adapter.checkMultiBlockForm()) {
                adapter.reset();
            }
        }
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }
}
