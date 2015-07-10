package com.rk.rkstuff.accelerator.tile;

import cofh.api.inventory.IInventoryHandler;
import com.rk.rkstuff.core.tile.IMultiBlockMasterListener;
import com.rk.rkstuff.core.tile.TileMultiBlockMaster;
import com.rk.rkstuff.core.tile.TileRK;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import rk.com.core.io.IOStream;

import java.io.IOException;
import java.util.List;

public class TileAcceleratorControlItemIO extends TileRK implements IMultiBlockMasterListener, IInventoryHandler {

    private TileAcceleratorMaster master;
    private int mode;

    public boolean hasMaster() {
        return master != null;
    }

    @Override
    public void registerMaster(TileMultiBlockMaster tileMaster) {
        master = (TileAcceleratorMaster) tileMaster;
    }

    @Override
    public void unregisterMaster() {
        master = null;
    }

    @Override
    public void readData(IOStream data) throws IOException {
        mode = data.readFirst();
        markBlockForUpdate();
    }

    @Override
    public void writeData(IOStream data) {
        data.writeFirst(mode);
    }

    @Override
    public ItemStack insertItem(ForgeDirection forgeDirection, ItemStack itemStack, boolean b) {
        return null;
    }

    @Override
    public ItemStack extractItem(ForgeDirection forgeDirection, ItemStack itemStack, boolean b) {
        return null;
    }

    @Override
    public ItemStack extractItem(ForgeDirection forgeDirection, int i, boolean b) {
        return null;
    }

    @Override
    public List<ItemStack> getInventoryContents(ForgeDirection forgeDirection) {
        return null;
    }

    @Override
    public int getSizeInventory(ForgeDirection forgeDirection) {
        return 0;
    }

    @Override
    public boolean isEmpty(ForgeDirection forgeDirection) {
        return false;
    }

    @Override
    public boolean isFull(ForgeDirection forgeDirection) {
        return false;
    }

    @Override
    public ConnectionType canConnectInventory(ForgeDirection forgeDirection) {
        return null;
    }

    public void toggleIOMode() {
        if (!worldObj.isRemote) {
            mode++;
            mode %= 6;
            markDirty();
            markBlockForUpdate();
        }
    }

    public boolean isOutput() {
        return mode == 0;
    }

    public int getMode() {
        return mode;
    }
}
