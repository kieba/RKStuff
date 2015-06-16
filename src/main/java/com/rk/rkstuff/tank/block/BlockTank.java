package com.rk.rkstuff.tank.block;

import com.rk.rkstuff.core.block.BlockRK;
import com.rk.rkstuff.helper.MultiBlockHelper;
import com.rk.rkstuff.tank.tile.TileTankAdapter;
import com.rk.rkstuff.util.Reference;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockTank extends BlockRK implements ITankBlock {
    public BlockTank() {
        super(Material.iron, Reference.BLOCK_TANK);
    }

    protected BlockTank(Material material, String blockName) {
        super(material, blockName);
    }

    @Override
    public IIcon getIcon(int meta, int side) {
        if (meta == 0) {
            return Blocks.coal_ore.getIcon(meta, side);
        } else {
            return Blocks.glass.getIcon(meta, side);
        }
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int meta) {
        if (meta == 0) {
            return Blocks.coal_ore.getIcon(world, x, y, z, meta);
        } else {
            return Blocks.glass.getIcon(world, x, y, z, meta);
        }
    }

    @Override
    public boolean isOpaqueCube() {
        return true;
    }

    protected TileTankAdapter getTankAdapter(int x, int y, int z, int meta) {
        if (meta == 0) return null;
        //Search on same Level
        return null;
    }

    private TileTankAdapter levelSearch(int x, int y, int z, int meta) {
        MultiBlockHelper.Bounds tmp = new MultiBlockHelper.Bounds(x, y, z);
        for (ForgeDirection direction : new ForgeDirection[]{ForgeDirection.NORTH, ForgeDirection.EAST}) {
            int i = 1;
            /*while (isValidTankBlock(xCoord + direction.offsetX * i, yCoord, zCoord + direction.offsetZ * i)) {
                i++;
            }
            i--;
            tmpBounds.add(xCoord + direction.offsetX * i, yCoord, zCoord + direction.offsetZ * i);
            direction = direction.getOpposite();
            tmpBounds.add(xCoord + direction.offsetX * i, yCoord, zCoord + direction.offsetZ * i);
            */
        }


        return null;
    }


}
