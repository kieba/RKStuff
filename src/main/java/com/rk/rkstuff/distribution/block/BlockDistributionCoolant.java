package com.rk.rkstuff.distribution.block;

import cofh.lib.util.helpers.ServerHelper;
import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.core.block.BlockRK;
import com.rk.rkstuff.distribution.tile.TileDistribution;
import com.rk.rkstuff.distribution.tile.TileDistributionCoolant;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDistributionCoolant extends BlockRK implements ITileEntityProvider, IPeripheralProvider {

    private IIcon[] icons = new IIcon[3];

    public BlockDistributionCoolant() {
        super(Material.iron, Reference.BLOCK_DISTRIBUTION_COOLANT);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileDistributionCoolant();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (!super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ)) {
            if (world.getTileEntity(x, y, z) instanceof TileDistributionCoolant && ServerHelper.isServerWorld(world)) {
                player.openGui(RkStuff.INSTANCE, Reference.GUI_ID_DISTRIBUTION_COOLANT, world, x, y, z);
            }
        }
        return true;
    }

    @Override
    public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
        return (IPeripheral) world.getTileEntity(x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons[0] = loadIconById("Input", iconRegister);
        icons[1] = loadIconById("Output", iconRegister);
        icons[2] = loadIconById("Disabled", iconRegister);
    }

    protected IIcon loadIconById(String suffix, IIconRegister iconRegister) {
        return iconRegister.registerIcon(Reference.MOD_ID + ":distribution/" + Reference.BLOCK_DISTRIBUTION_COOLANT + suffix);
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        return icons[((TileDistribution) world.getTileEntity(x, y, z)).getSides()[side]];
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return icons[0];
    }
}
