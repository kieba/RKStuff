package com.rk.rkstuff.coolant.block;

import com.rk.rkstuff.coolant.tile.TileCoolantInjector;
import com.rk.rkstuff.core.block.BlockRK;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCoolantInjector extends BlockRK implements ITileEntityProvider {

    public BlockCoolantInjector() {
        super(Material.iron, Reference.BLOCK_COOLANT_INJECTOR);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileCoolantInjector();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(Reference.MOD_ID + ":coolant/" + Reference.BLOCK_COOLANT_INJECTOR);
    }

}
