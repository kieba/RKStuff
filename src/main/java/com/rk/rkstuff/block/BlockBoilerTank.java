package com.rk.rkstuff.block;

import com.rk.rkstuff.helper.MultiBlockHelper;
import com.rk.rkstuff.helper.RKLog;
import com.rk.rkstuff.tile.IBoilerBaseTile;
import com.rk.rkstuff.tile.TileBoilerBaseMaster;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockBoilerTank extends BlockRK {

    private IIcon[] icons = new IIcon[14];

    public BlockBoilerTank() {
        super(Material.iron);
        setBlockName(Reference.BLOCK_BOILER_TANK);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        for (int i = 0; i < icons.length; i++) {
            icons[i] = iconRegister.registerIcon(Reference.MOD_ID + ":" + getUnwrappedUnlocalizedName(this.getUnlocalizedName()) + (i+1));
        }
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        //TODO: map icons to side
        return Blocks.dirt.getIcon(side, meta);
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

        if(master != null && !master.checkMultiBlockForm()) {
            master.reset();
        } else {
            RKLog.error("No master found for BlockBoilerTank! Metadata: " + world.getBlockMetadata(x, y, z));
        }

    }

    private boolean isValidBoilerTank(World world, int x, int y, int z){
        Block block = world.getBlock(x, y, z);
        return block instanceof BlockBoilerTank;
    }
}
