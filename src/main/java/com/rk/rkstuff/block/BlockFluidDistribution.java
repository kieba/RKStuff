package com.rk.rkstuff.block;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.tile.TileEnergyDistribution;
import com.rk.rkstuff.tile.TileFluidDistribution;
import com.rk.rkstuff.util.Reference;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockFluidDistribution extends BlockRK implements ITileEntityProvider {

    public BlockFluidDistribution() {
        super(Material.iron);
        setBlockName(Reference.BLOCK_FLUID_DISTRIBUTION);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileFluidDistribution();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if(world.getTileEntity(x, y, z) instanceof TileFluidDistribution) {
            player.openGui(RkStuff.INSTANCE, Reference.GUI_ID_FLUID_DISTRIBUTION, world, x, y, z);
        }
        return true;
    }
}
