package com.rk.rkstuff.boiler.block;

import com.rk.rkstuff.boiler.tile.TileBoilerBaseMaster;
import com.rk.rkstuff.core.block.BlockRK;
import com.rk.rkstuff.helper.MultiBlockHelper;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockBoilerBase extends BlockRK implements IBoilerBaseBlock {

    private IIcon[] icons = new IIcon[2];

    public BlockBoilerBase() {
        super(Material.iron, Reference.BLOCK_BOILER_BASE);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons[0] = loadIconById(1, iconRegister);
        icons[1] = loadIconById(2, iconRegister);
    }

    protected IIcon loadIconById(int id, IIconRegister iconRegister) {
        return iconRegister.registerIcon(Reference.MOD_ID + ":boiler/" + Reference.BLOCK_BOILER_BASE + id);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (ForgeDirection.UP.ordinal() == side) {
            return icons[0];
        } else if (ForgeDirection.DOWN.ordinal() == side) {
            return icons[0];
        } else {
            return icons[1];
        }
    }
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block changeBlock) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0) return;
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            if (direction == ForgeDirection.DOWN) continue;
            if (direction == ForgeDirection.UP) continue;
            boolean hasNeighbour = (meta >> ((direction.ordinal() - 2)) & 0x01) == 1;
            boolean isNeighbourBoilerBaseBlock = isValidBoilerBase(world, x + direction.offsetX, y, z + direction.offsetZ);
            if (!hasNeighbour && isNeighbourBoilerBaseBlock || hasNeighbour && !isNeighbourBoilerBaseBlock) {
                TileBoilerBaseMaster master = getMaster(world, x, y, z);
                if(master != null) master.reset();
                return;
            }
        }
    }

    private boolean isValidBoilerBase(World world, int x, int y, int z){
        Block block = world.getBlock(x, y, z);
        return block instanceof IBoilerBaseBlock || block instanceof BlockBoilerBaseMaster;
    }

    @Override
    public TileBoilerBaseMaster getMaster(World world, int x, int y, int z) {
        if(world.getBlockMetadata(x, y, z) == 0) return null;

        MultiBlockHelper.Bounds tmpBounds = new MultiBlockHelper.Bounds(x, y, z);
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            if (direction == ForgeDirection.UP) continue;
            if (direction == ForgeDirection.DOWN) continue;

            int i = 0;
            while (isValidBoilerBase(world, x + direction.offsetX * i, y, z + direction.offsetZ * i)) {
                i++;
            }
            i--;
            tmpBounds.add(x + direction.offsetX * i, y, z + direction.offsetZ * i);

        }
        for (MultiBlockHelper.Bounds.BlockIterator.BoundsPos pos : tmpBounds) {
            if (world.getBlock(pos.x, pos.y, pos.z) instanceof BlockBoilerBaseMaster) {
                TileBoilerBaseMaster master = (TileBoilerBaseMaster) world.getTileEntity(pos.x, pos.y, pos.z);
                if(master.isBuild()) return master;
            }
        }
        return null;
    }
}
