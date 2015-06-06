package com.rk.rkstuff.block;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.tile.TileSolarMaster;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockSolarMaster extends BlockSolar implements ISolarBlock, ITileEntityProvider, IPeripheralProvider {
    public BlockSolarMaster() {
        super(Reference.BLOCK_SOLAR_MASTER);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        super.registerBlockIcons(iconRegister);
        icons[1] = iconRegister.registerIcon(Reference.MOD_ID + ":solar/" + Reference.BLOCK_SOLAR_MASTER);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileSolarMaster();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if(world.getTileEntity(x, y, z) instanceof TileSolarMaster) {
            player.openGui(RkStuff.INSTANCE, Reference.GUI_ID_SOLAR, world, x, y, z);
        }
        return true;
    }

    @Override
    public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
        return (IPeripheral) world.getTileEntity(x, y, z);
    }
}
