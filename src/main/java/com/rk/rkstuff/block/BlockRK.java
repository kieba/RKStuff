package com.rk.rkstuff.block;

import com.rk.rkstuff.helper.RKLog;
import com.rk.rkstuff.util.CreativeTabRKStuff;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;

public abstract class BlockRK extends Block {

    protected BlockRK(Material material) {
        super(material);
        this.setCreativeTab(CreativeTabRKStuff.RK_STUFF_TAB);
        this.setHardness(0.5f);
    }

    @Override
    public String getUnlocalizedName() {
        return String.format("tile.%s:%s", Reference.MOD_ID, getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(getUnwrappedUnlocalizedName(this.getUnlocalizedName()));
    }

    protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }

}
