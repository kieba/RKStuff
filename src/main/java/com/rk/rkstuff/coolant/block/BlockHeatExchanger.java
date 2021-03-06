package com.rk.rkstuff.coolant.block;

import com.rk.rkstuff.coolant.tile.TileHeatExchanger;
import com.rk.rkstuff.core.block.BlockRKReconfigurable;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockHeatExchanger extends BlockRKReconfigurable implements ITileEntityProvider {

    public IIcon[] blockIcons = new IIcon[4];

    public BlockHeatExchanger() {
        super(Material.iron, Reference.BLOCK_HEAT_EXCHANGER);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileHeatExchanger();
    }


    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (world.isRemote) return;
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileHeatExchanger) {
            ((TileHeatExchanger) te).onNeighbourBlockChange();
        }
        return;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcons[0] = iconRegister.registerIcon(Reference.MOD_ID + ":coolant/" + Reference.BLOCK_HEAT_EXCHANGER + "Disabled");
        blockIcons[1] = iconRegister.registerIcon(Reference.MOD_ID + ":coolant/" + Reference.BLOCK_HEAT_EXCHANGER + "Input");
        blockIcons[2] = iconRegister.registerIcon(Reference.MOD_ID + ":coolant/" + Reference.BLOCK_HEAT_EXCHANGER + "Output");
        blockIcons[3] = iconRegister.registerIcon(Reference.MOD_ID + ":coolant/" + Reference.BLOCK_HEAT_EXCHANGER + "Facing");
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileHeatExchanger) {
            return ((TileHeatExchanger) te).getTexture(side, 0);
        }
        return blockIcons[0];
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return blockIcons[0];
    }
}
