package com.rk.rkstuff.core.block;

import buildcraft.api.tools.IToolWrench;
import com.rk.rkstuff.core.tile.TileRK;
import com.rk.rkstuff.util.CreativeTabRKStuff;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockRK extends Block {

    public BlockRK(Material material, String blockName) {
        super(material);
        this.setCreativeTab(CreativeTabRKStuff.RK_STUFF_TAB);
        this.setHardness(0.5f);
        this.setBlockName(blockName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(Reference.MOD_ID + ":" + getUnwrappedUnlocalizedName(this.getUnlocalizedName()));
    }

    protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }

    @Override
    public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
        int offsetX = tileX - x;
        int offsetY = tileY - y;
        int offsetZ = tileZ - z;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (dir.offsetX == offsetX && dir.offsetY == offsetY && dir.offsetZ == offsetZ) {
                TileEntity te = world.getTileEntity(x, y, z);
                if (te instanceof TileRK) ((TileRK) te).onNeighborChange(dir);
                break;
            }
        }
    }


    /**
     * @return if Wrench was used
     */
    public boolean onWrench(World world, int x, int y, int z, int side, EntityPlayer player) {
        return false;
    }

    public boolean canBeWrenched(World world, int x, int y, int z, int side, EntityPlayer player) {
        TileRK tile = (TileRK) world.getTileEntity(x, y, z);
        if (tile != null) {
            return !tile.hasGui();
        }
        return true;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);


            Item item = player.getCurrentEquippedItem() != null ? player.getCurrentEquippedItem().getItem() : null;
            if (item instanceof IToolWrench && ((IToolWrench) item).canWrench(player, x, y, z)) {
                if (player.isSneaking()) {
                    if (!world.isRemote) {
                        world.func_147480_a(x, y, z, true);
                    }
                    return true;
                } else if (canBeWrenched(world, x, y, z, side, player)) {
                    if (!world.isRemote) {
                        return onWrench(world, x, y, z, side, player);
                    }
                }
            }

        return false;
    }

}
