package com.rk.rkstuff.coolant.block;

import com.rk.rkstuff.coolant.tile.TileHeatPump;
import com.rk.rkstuff.core.block.BlockRKReconfigurable;
import com.rk.rkstuff.util.Reference;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockHeatPump extends BlockRKReconfigurable {
    public BlockHeatPump() {
        super(Material.iron, Reference.BLOCK_HEAT_PUMP);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileHeatPump();
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons = new IIcon[6];
        icons[0] = iconRegister.registerIcon(Reference.MOD_ID + ":coolant/" + Reference.BLOCK_HEAT_PUMP + "Disabled");
        icons[1] = iconRegister.registerIcon(Reference.MOD_ID + ":coolant/" + Reference.BLOCK_HEAT_PUMP + "Input1");
        icons[2] = iconRegister.registerIcon(Reference.MOD_ID + ":coolant/" + Reference.BLOCK_HEAT_PUMP + "Input2");
        icons[3] = iconRegister.registerIcon(Reference.MOD_ID + ":coolant/" + Reference.BLOCK_HEAT_PUMP + "Output1");
        icons[4] = iconRegister.registerIcon(Reference.MOD_ID + ":coolant/" + Reference.BLOCK_HEAT_PUMP + "Output2");
        icons[5] = iconRegister.registerIcon(Reference.MOD_ID + ":coolant/" + Reference.BLOCK_HEAT_PUMP + "Facing");
    }
}
