package com.rk.rkstuff.block;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.helper.Pos;
import com.rk.rkstuff.tile.TileTeleporter;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import java.util.UUID;

public class BlockTeleporter extends BlockRK implements ITileEntityProvider {

    public BlockTeleporter() {
        super(Material.iron, Reference.BLOCK_TELEPORTER);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
        super.onNeighborBlockChange(world, x, y, z, b);
        if (!world.isRemote && world.getStrongestIndirectPower(x, y, z) > 0) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileTeleporter) {
                ((TileTeleporter) tile).teleport();
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        if (world.isRemote) {
            return true;
        } else {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileTeleporter) {
                TileTeleporter tileTeleporter = (TileTeleporter) tile;
                ItemStack stack = player.getHeldItem();
                if (stack != null && stack.getItem() == RkStuff.itemLinker) {
                    NBTTagCompound tag = stack.getTagCompound();
                    if (tag != null && tag.hasKey("pos")) {
                        if (tileTeleporter.setUUID(new UUID(tag.getLong("uuidMSB"), tag.getLong("uuidLSB")))) {
                            //uuid set
                            player.addChatMessage(new ChatComponentText("Teleporter-Link established!"));
                        } else {
                            //uuid not set
                            player.addChatMessage(new ChatComponentText("Teleporter-Link not established!"));
                        }
                    } else {
                        if (tag == null) tag = new NBTTagCompound();
                        tag.setLong("uuidMSB", tileTeleporter.getUuid().getMostSignificantBits());
                        tag.setLong("uuidLSB", tileTeleporter.getUuid().getLeastSignificantBits());
                        Pos pos = tileTeleporter.getPosition();
                        tag.setIntArray("pos", new int[]{pos.x, pos.y, pos.z});
                        tag.setString("dim", world.getWorldInfo().getWorldName());
                        stack.setTagCompound(tag);
                        player.addChatMessage(new ChatComponentText("Saved teleporter position!"));
                    }
                }
            }
        }
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileTeleporter();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(String.format("%s:%s", Reference.MOD_ID.toLowerCase(), Reference.BLOCK_TELEPORTER));
    }

}
