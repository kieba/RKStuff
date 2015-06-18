package com.rk.rkstuff.coolant.block;

import com.rk.rkstuff.coolant.tile.TileCoolantExtractor;
import com.rk.rkstuff.core.block.BlockRK;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCoolantExtractor extends BlockRK implements ITileEntityProvider {

    public BlockCoolantExtractor() {
        super(Material.iron, Reference.BLOCK_COOLANT_EXTRACTOR);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(Reference.MOD_ID + ":coolant/" + Reference.BLOCK_COOLANT_EXTRACTOR);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileCoolantExtractor();
    }

}
