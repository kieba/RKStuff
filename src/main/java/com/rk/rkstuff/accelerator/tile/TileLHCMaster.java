package com.rk.rkstuff.accelerator.tile;

import com.rk.rkstuff.accelerator.AcceleratorHelper;
import com.rk.rkstuff.accelerator.LHCRecipe;
import com.rk.rkstuff.accelerator.LHCRecipeRegistry;
import com.rk.rkstuff.util.RKLog;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import org.apache.logging.log4j.Level;

public class TileLHCMaster extends TileAcceleratorMaster {

    private LHCInventory inventory = new LHCInventory();
    private LHCRecipe currentRecipe;

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        inventory.writeToNBT("inv", tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        inventory.readFromNBT("inv", tag);
    }

    @Override
    protected void updateMaster() {
        super.updateMaster();

        if (currentRecipe == null) {
            currentRecipe = LHCRecipeRegistry.getRecipeCrafting(accelerator.getMaxSpeed(), inventory.stacks, 1, 5);
            if (currentRecipe != null) {
                ItemStack result = currentRecipe.getResult();
                if (inventory.stacks[0] != null && (!inventory.stacks[0].isItemEqual(result) ||
                        (inventory.stacks[0].stackSize + result.stackSize) > result.getMaxStackSize())) {
                    currentRecipe = null;
                } else {

                    RKLog.log(Level.INFO, "removed: " + LHCRecipeRegistry.removeRecipeFromStacks(inventory.stacks, 1, 5, currentRecipe));

                }
            }
        }
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    public float injectMass() {
        if (currentRecipe == null) return 0.0f;
        return currentRecipe.getMass();
    }

    @Override
    public void preAcceleration() {

    }

    @Override
    public void postAcceleration() {

    }

    @Override
    public void onInitialize() {

    }

    @Override
    public void onUnInitialize() {

    }

    @Override
    public void onRoundFinished() {

    }

    @Override
    public void onToSlow() {
        currentRecipe = null;
    }

    @Override
    public float getAccelerationEnergy(float maxEnergy) {
        float amount = Math.min(maxEnergy, getStoredEnergy());
        removeEnergy(amount);
        return amount;
    }

    @Override
    public void collide() {
        if (inventory.stacks[0] == null) {
            inventory.stacks[0] = currentRecipe.getResult().copy();
        } else {
            inventory.stacks[0].stackSize += currentRecipe.getResult().stackSize;
        }
        currentRecipe = null;
    }

    @Override
    public float produce() {
        return 0;
    }

    @Override
    public boolean isCollideMode() {
        return true;
    }

    @Override
    protected void setup(AcceleratorHelper.AcceleratorPos pos, Block block, TileEntity tile) {

    }

    @Override
    protected void reset(AcceleratorHelper.AcceleratorPos pos, Block block, TileEntity tile) {

    }

    public IInventory getInventory() {
        return inventory;
    }

    private class LHCInventory implements IInventory {

        private ItemStack[] stacks = new ItemStack[6];

        @Override
        public int getSizeInventory() {
            return stacks.length;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return stacks[slot];
        }

        @Override
        public ItemStack decrStackSize(int slot, int maxAmount) {
            ItemStack s = stacks[slot];
            if (s == null) return null;
            int amount = Math.min(maxAmount, stacks[slot].stackSize);
            ItemStack newStack = s.splitStack(amount);
            if (s.stackSize == 0) stacks[slot] = null;
            return newStack;
        }

        @Override
        public ItemStack getStackInSlotOnClosing(int slot) {
            return stacks[slot];
        }

        @Override
        public void setInventorySlotContents(int slot, ItemStack stack) {
            stacks[slot] = stack;
        }

        @Override
        public String getInventoryName() {
            return "LHC Inventory";
        }

        @Override
        public boolean hasCustomInventoryName() {
            return false;
        }

        @Override
        public int getInventoryStackLimit() {
            return 64;
        }

        @Override
        public void markDirty() {

        }

        @Override
        public boolean isUseableByPlayer(EntityPlayer player) {
            return true;
        }

        @Override
        public void openInventory() {

        }

        @Override
        public void closeInventory() {

        }

        @Override
        public boolean isItemValidForSlot(int slot, ItemStack stack) {
            return slot != 0;
        }

        public void writeToNBT(String prefix, NBTTagCompound tag) {
            for (int i = 0; i < 6; i++) {
                if (stacks[i] != null) {
                    NBTTagCompound t = new NBTTagCompound();
                    stacks[i].writeToNBT(t);
                    tag.setTag(prefix + "item" + i, t);
                }
            }
        }

        public void readFromNBT(String prefix, NBTTagCompound tag) {
            for (int i = 0; i < 6; i++) {
                if (tag.hasKey(prefix + "item" + i)) {
                    stacks[i] = new ItemStack(Blocks.air);
                    stacks[i].readFromNBT(tag.getCompoundTag(prefix + "item" + i));
                }
            }
        }
    }
}
