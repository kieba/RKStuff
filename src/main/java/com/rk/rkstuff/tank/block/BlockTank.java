package com.rk.rkstuff.tank.block;

import com.rk.rkstuff.core.block.BlockRK;
import com.rk.rkstuff.helper.MultiBlockHelper;
import com.rk.rkstuff.tank.tile.TileTankAdapter;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockTank extends BlockRK implements ITankBlock {
    private IIcon[] icons = new IIcon[2];

    public BlockTank() {
        super(Material.glass, Reference.BLOCK_TANK);
    }

    protected BlockTank(Material material, String blockName) {
        super(material, blockName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons[0] = iconRegister.registerIcon(Reference.MOD_ID + ":tank/" + Reference.BLOCK_TANK + 1);
        icons[1] = iconRegister.registerIcon(Reference.MOD_ID + ":tank/" + Reference.BLOCK_TANK + 2);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta == 0) return icons[0];
        if (meta == 1) return icons[1];
        return icons[0];
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    protected boolean isBuild(int meta) {
        return meta != 0;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess access, int x, int y, int z, int side) {
        if (access.getBlock(x, y, z) instanceof ITankBlock) {
            return false;
        }
        return true;
    }

    private TileTankAdapter getTankAdapter(IBlockAccess access, int x, int y, int z, int meta) {
        isBuild(meta);
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

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public TileEntity getMasterTileEntity(IBlockAccess access, int x, int y, int z, int meta) {
        return getTankAdapter(access, x, y, z, meta);
    }
}
