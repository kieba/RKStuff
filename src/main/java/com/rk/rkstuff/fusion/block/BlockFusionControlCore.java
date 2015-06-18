package com.rk.rkstuff.fusion.block;

import com.rk.rkstuff.core.block.BlockRK;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class BlockFusionControlCore extends BlockRK implements IFusionControlCoreBlock {

    private IIcon[] icons = new IIcon[2];

    public BlockFusionControlCore() {
        super(Material.iron, Reference.BLOCK_FUSION_CONTROL_CORE);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons[0] = iconRegister.registerIcon(Reference.MOD_ID + ":fusion/" + Reference.BLOCK_FUSION_CONTROL_CORE + 1);
        icons[1] = iconRegister.registerIcon(Reference.MOD_ID + ":fusion/" + Reference.BLOCK_FUSION_CONTROL_CORE + 2);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta == 0) return icons[0];
        return icons[1];
    }
}
