package com.rk.rkstuff.block;

import com.rk.rkstuff.helper.RKLog;
import com.rk.rkstuff.tile.TileBoilerBaseMaster;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockBoilerTank extends BlockRK {

    private IIcon[] icons = new IIcon[14];

    public BlockBoilerTank() {
        super(Material.iron, Reference.BLOCK_BOILER_TANK);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons[0] = loadIconById(1, iconRegister);
        icons[1] = loadIconById(2, iconRegister);
        icons[6] = loadIconById(3, iconRegister);
        icons[3] = loadIconById(4, iconRegister);
        icons[4] = loadIconById(5, iconRegister);
        icons[5] = loadIconById(6, iconRegister);
        icons[2] = loadIconById(7, iconRegister);
        icons[7] = loadIconById(8, iconRegister);
        icons[8] = loadIconById(9, iconRegister);
        icons[9] = loadIconById(10, iconRegister);
        icons[10] = loadIconById(11, iconRegister);
    }

    protected IIcon loadIconById(int id, IIconRegister iconRegister) {
        return iconRegister.registerIcon(Reference.MOD_ID + ":boiler/" + Reference.BLOCK_BOILER_TANK + id);
    }

    @Override
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
    public void onNeighborBlockChange(World world, int x, int y, int z, Block changeBlock) {
        if (world.getBlockMetadata(x, y, z) == 0) return;

        int i = 0;
        while (isValidBoilerTank(world, x, y - i, z)) {
            i++;
        }

        Block block = world.getBlock(x, y - i, z);
        TileBoilerBaseMaster master = null;
        if(block instanceof IBoilerBaseBlock) {
            master = ((IBoilerBaseBlock) block).getMaster(world, x, y - i, z);
        } else if(block instanceof BlockBoilerBaseMaster) {
            master = ((BlockBoilerBaseMaster) block).getMaster(world, x, y - i, z);
        }

        if(master != null) {
            if(!master.checkMultiBlockForm()) {
                master.reset();
            }
        } else {
            RKLog.error("No master found for BlockBoilerTank! Metadata: " + world.getBlockMetadata(x, y, z));
        }

    }

    @Override
    public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
        if (world.getBlockMetadata(x, y, z) == 0) return;

        int i = 0;
        while (isValidBoilerTank(world, x, y - i, z)) {
            i++;
        }

        Block block = world.getBlock(x, y - i, z);
        TileBoilerBaseMaster master = null;
        if (block instanceof IBoilerBaseBlock) {
            master = ((IBoilerBaseBlock) block).getMaster(world, x, y - i, z);
        } else if (block instanceof BlockBoilerBaseMaster) {
            master = ((BlockBoilerBaseMaster) block).getMaster(world, x, y - i, z);
        }

        if (master != null) {
            if (!master.isBuild()) {
                if (master.getTemperature() >= 300) {
                    entity.setFire(10);
                }
            }
        } else {
            RKLog.error("No master found for BlockBoilerTank! Metadata: " + world.getBlockMetadata(x, y, z));
        }
    }

    private boolean isValidBoilerTank(World world, int x, int y, int z){
        Block block = world.getBlock(x, y, z);
        return block instanceof BlockBoilerTank;
    }
}
