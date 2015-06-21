package com.rk.rkstuff.core.modinteraction;

import com.rk.rkstuff.core.block.BlockRK;
import cpw.mods.fml.common.Optional;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;

@Optional.Interface(iface = "mcp.mobius.waila.api.IWailaDataProvider", modid = "Waila")
public class WailaTileHandler implements IWailaDataProvider {

    @Optional.Method(modid = "Waila")
    public static void callbackRegister(IWailaRegistrar register) {
        WailaTileHandler instance = new WailaTileHandler();

        register.registerBodyProvider(instance, BlockRK.class);
        register.registerHeadProvider(instance, BlockRK.class);
        register.registerTailProvider(instance, BlockRK.class);
        register.registerStackProvider(instance, BlockRK.class);
    }

    @Override
    @Optional.Method(modid = "Waila")
    public ItemStack getWailaStack(IWailaDataAccessor iWailaDataAccessor, IWailaConfigHandler iWailaConfigHandler) {
        return iWailaDataAccessor.getStack();
    }

    @Override
    @Optional.Method(modid = "Waila")
    public List<String> getWailaHead(ItemStack itemStack, List<String> list, IWailaDataAccessor iWailaDataAccessor, IWailaConfigHandler iWailaConfigHandler) {
        return list;
    }

    @Override
    @Optional.Method(modid = "Waila")
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentBordy, IWailaDataAccessor accessor, IWailaConfigHandler configHandler) {
        if (accessor.getTileEntity() != null && accessor.getTileEntity() instanceof IWailaBodyProvider) {
            return ((IWailaBodyProvider) accessor.getTileEntity()).getWailaBody(itemStack, currentBordy, accessor, configHandler);
        }
        return currentBordy;
    }

    @Override
    @Optional.Method(modid = "Waila")
    public List<String> getWailaTail(ItemStack itemStack, List<String> list, IWailaDataAccessor iWailaDataAccessor, IWailaConfigHandler iWailaConfigHandler) {
        return list;
    }

    @Override
    @Optional.Method(modid = "Waila")
    public NBTTagCompound getNBTData(EntityPlayerMP entityPlayerMP, TileEntity tileEntity, NBTTagCompound nbtTagCompound, World world, int i, int i1, int i2) {
        if (tileEntity != null)
            tileEntity.writeToNBT(nbtTagCompound);

        return nbtTagCompound;
    }
}
