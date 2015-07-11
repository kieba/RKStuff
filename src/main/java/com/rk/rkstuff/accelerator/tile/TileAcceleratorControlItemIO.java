package com.rk.rkstuff.accelerator.tile;

import com.rk.rkstuff.core.tile.IMultiBlockMasterListener;
import com.rk.rkstuff.core.tile.TileMultiBlockMaster;
import com.rk.rkstuff.core.tile.TileRK;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileAcceleratorControlItemIO extends TileRK implements IMultiBlockMasterListener, IInventory {

    private TileLHCMaster master;
    private int slot;

    public boolean hasMaster() {
        return master != null;
    }

    @Override
    public void registerMaster(TileMultiBlockMaster tileMaster) {
        master = (TileLHCMaster) tileMaster;
    }

    @Override
    public void unregisterMaster() {
        master = null;
    }

    @Override
    public void readData(IOStream data) throws IOException {
        slot = data.readFirst();
        markBlockForUpdate();
    }

    @Override
    public void writeData(IOStream data) {
        data.writeFirst(slot);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        slot = tag.getInteger("slot");

    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("slot", slot);
    }

    public void incrementSlot() {
        if (!worldObj.isRemote) {
            slot++;
            slot %= 6;
            markDirty();
            markBlockForUpdate();
        }
    }

    public void decrementSlot() {
        if (!worldObj.isRemote) {
            slot--;
            if (slot < 0) slot = 5;
            markDirty();
            markBlockForUpdate();
        }
    }

    public boolean isOutput() {
        return slot == 0;
    }

    public int getSlot() {
        return slot;
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int s) {
        if (hasMaster()) {
            return master.getInventory().getStackInSlot(slot);
        }
        return null;
    }

    @Override
    public ItemStack decrStackSize(int s, int amount) {
        if (hasMaster()) {
            master.getInventory().decrStackSize(slot, amount);
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int s) {
        if (hasMaster()) {
            master.getInventory().getStackInSlotOnClosing(slot);
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int s, ItemStack stack) {
        if (hasMaster() && !isOutput()) {
            master.getInventory().setInventorySlotContents(slot, stack);
        }
    }

    @Override
    public String getInventoryName() {
        if (hasMaster()) {
            return master.getInventory().getInventoryName();
        }
        return null;
    }

    @Override
    public boolean hasCustomInventoryName() {
        if (hasMaster()) {
            return master.getInventory().hasCustomInventoryName();
        }
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        if (hasMaster()) {
            return master.getInventory().getInventoryStackLimit();
        }
        return 0;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return false;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int s, ItemStack stack) {
        return hasMaster() && !isOutput();
    }
}
