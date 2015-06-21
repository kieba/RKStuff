package com.rk.rkstuff.core.modinteraction;

import com.rk.rkstuff.core.block.BlockRK;
import com.rk.rkstuff.core.block.IBlockMulti;
import cpw.mods.fml.common.Optional;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import java.util.List;

@Optional.Interface(iface = "mcp.mobius.waila.api.IWailaDataProvider", modid = "Waila")
public class WailaBlockHandler implements IWailaDataProvider {

    @Optional.Method(modid = "Waila")
    public static void callbackRegister(IWailaRegistrar register) {
        WailaBlockHandler instance = new WailaBlockHandler();

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
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentBody, IWailaDataAccessor accessor, IWailaConfigHandler configHandler) {
        if (accessor.getBlock() instanceof IBlockMulti) {
            MovingObjectPosition position = accessor.getPosition();
            TileEntity masterTe = ((IBlockMulti) accessor.getBlock()).getMasterTileEntity(accessor.getWorld(), position.blockX, position.blockY, position.blockZ, accessor.getMetadata());
            if (masterTe == null) return currentBody;
            if (masterTe.xCoord == position.blockX &&
                    masterTe.yCoord == position.blockY &&
                    masterTe.zCoord == position.blockZ) {
                return currentBody;
            }
            if (masterTe instanceof IWailaBodyProvider) {
                return ((IWailaBodyProvider) masterTe).getWailaBody(itemStack, currentBody, accessor, configHandler);
            }
        }
        return currentBody;
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
