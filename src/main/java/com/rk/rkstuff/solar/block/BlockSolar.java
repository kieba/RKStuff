package com.rk.rkstuff.solar.block;

import com.rk.rkstuff.core.block.BlockRK;
import com.rk.rkstuff.helper.MultiBlockHelper;
import com.rk.rkstuff.solar.tile.TileSolarMaster;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;


public class BlockSolar extends BlockRK implements ISolarBlock {
    protected IIcon[] icons = new IIcon[18];

    public BlockSolar() {
        this(Reference.BLOCK_SOLAR);
    }

    protected BlockSolar(String blockName) {
        super(Material.iron, blockName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons[0] = loadIconById(1, iconRegister);
        icons[1] = loadIconById(2, iconRegister);
        icons[2] = loadIconById(3, iconRegister);
        icons[3] = loadIconById(9, iconRegister);
        icons[4] = loadIconById(7, iconRegister);
        icons[5] = loadIconById(8, iconRegister);
        icons[6] = loadIconById(6, iconRegister);
        icons[7] = loadIconById(18, iconRegister);
        icons[8] = loadIconById(12, iconRegister);
        icons[9] = loadIconById(15, iconRegister);
        icons[10] = loadIconById(4, iconRegister);
        icons[11] = loadIconById(16, iconRegister);
        icons[12] = loadIconById(10, iconRegister);
        icons[13] = loadIconById(13, iconRegister);
        icons[14] = loadIconById(5, iconRegister);
        icons[15] = loadIconById(17, iconRegister);
        icons[16] = loadIconById(11, iconRegister);
        icons[17] = loadIconById(14, iconRegister);
    }

    protected IIcon loadIconById(int id, IIconRegister iconRegister) {
        return iconRegister.registerIcon(Reference.MOD_ID + ":solar/" + Reference.BLOCK_SOLAR + id);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (ForgeDirection.UP.ordinal() == side) {
            return icons[2 + meta];
        } else if (ForgeDirection.DOWN.ordinal() == side) {
            return icons[0];
        } else {
            return icons[1];
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (world.getBlockMetadata(x, y, z) == 0) return;
        int meta = world.getBlockMetadata(x, y, z);
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            if (direction == ForgeDirection.DOWN) continue;
            if (direction == ForgeDirection.UP) continue;
            boolean hasNeigbor = (meta >> ((direction.ordinal() - 2)) & 0x01) == 1;
            boolean isNeighborSolarBlock = world.getBlock(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ) instanceof ISolarBlock;
            if (!hasNeigbor && isNeighborSolarBlock || hasNeigbor && !isNeighborSolarBlock) {
                resetStructure(world, x, y, z);
                return;
            }
        }
    }

    private void resetStructure(World world, int x, int y, int z) {
        MultiBlockHelper.Bounds tmpBounds = new MultiBlockHelper.Bounds(x, y, z);
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            if (direction == ForgeDirection.UP) continue;
            if (direction == ForgeDirection.DOWN) continue;

            int i = 0;
            while (isValidMultiblock(world, x + direction.offsetX * i, y + direction.offsetY * i, z + direction.offsetZ * i)) {
                i++;
            }
            i--;
            tmpBounds.add(x + direction.offsetX * i, y + direction.offsetY * i, z + direction.offsetZ * i);
        }

        for (MultiBlockHelper.Bounds.BlockIterator.BoundsPos pos : tmpBounds) {
            if (world.getBlock(pos.x, pos.y, pos.z) instanceof BlockSolarMaster) {
                TileSolarMaster master = (TileSolarMaster) world.getTileEntity(pos.x, pos.y, pos.z);
                master.reset();
            }
        }
    }

    private boolean isValidMultiblock(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        return block instanceof ISolarBlock;
    }
}
