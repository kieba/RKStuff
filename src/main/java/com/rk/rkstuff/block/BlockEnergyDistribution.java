package com.rk.rkstuff.block;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.tile.TileBoilerBaseMaster;
import com.rk.rkstuff.tile.TileEnergyDistribution;
import com.rk.rkstuff.util.Reference;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockEnergyDistribution extends BlockRK implements ITileEntityProvider, IPeripheralProvider {

    public BlockEnergyDistribution() {
        super(Material.iron);
        setBlockName(Reference.BLOCK_ENERGY_DISTRIBUTION);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEnergyDistribution();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if(world.getTileEntity(x, y, z) instanceof TileEnergyDistribution) {
            player.openGui(RkStuff.INSTANCE, Reference.GUI_ID_ENERGY_DISTRIBUTION, world, x, y, z);
        }
        return true;
    }

    @Override
    public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
        return (IPeripheral) world.getTileEntity(x, y, z);
    }
}
