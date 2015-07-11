package com.rk.rkstuff.tank.tile;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.coolant.CoolantStack;
import com.rk.rkstuff.core.modinteraction.IWailaBodyProvider;
import com.rk.rkstuff.core.tile.IMultiBlockMasterListener;
import com.rk.rkstuff.core.tile.TileMultiBlockMaster;
import com.rk.rkstuff.helper.MultiBlockHelper;
import com.rk.rkstuff.tank.block.BlockTankAdapter;
import com.rk.rkstuff.tank.block.BlockTankBevelLarge;
import com.rk.rkstuff.tank.block.BlockTankBevelSmall;
import com.rk.rkstuff.tank.block.ITankBlock;
import com.rk.rkstuff.util.RKLog;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import rk.com.core.io.IOStream;

import java.io.IOException;
import java.util.List;

public class TileTankAdapter extends TileMultiBlockMaster implements IWailaBodyProvider {
    private int maxStorage = 0;
    private FluidStack currentFluidStack;
    private CoolantStack currentCoolantStack;
    private int tick = 0;

    @Override
    public boolean hasGui() {
        return false;
    }

    @Override
    public boolean checkMultiBlockForm() {
        return computeMultiStructureBounds() != null;
    }

    public int getInnerRadiusX() {
        if (bounds == null) return 0;
        return (bounds.getWidthX() - 2) / 2;
    }

    public int getInnerRadiusZ() {
        if (bounds == null) return 0;
        return (bounds.getWidthZ() - 2) / 2;
    }

    public int getOuterRadius() {
        if (bounds == null) return 0;
        return getInnerRadiusX() + 1;
    }

    public int getInnerHeight() {
        if (bounds == null) return 0;
        return (bounds.getHeight()) - 2;
    }

    public int getOuterHeight() {
        if (bounds == null) return 0;
        return bounds.getHeight();
    }

    public float getFillHeight() {
        if (maxStorage == 0) return 0;
        return getInnerHeight() * ((float) getCurrentStorage() / maxStorage);
    }

    @Override
    protected MultiBlockHelper.Bounds setupStructure() {
        MultiBlockHelper.Bounds bounds = computeMultiStructureBounds();
        for (MultiBlockHelper.Bounds.BlockIterator.BoundsPos pos : bounds) {
            if (worldObj.getBlock(pos.x, pos.y, pos.z) instanceof ITileEntityProvider) {
                TileEntity entity = worldObj.getTileEntity(pos.x, pos.y, pos.z);
                if (entity instanceof IMultiBlockMasterListener) {
                    ((IMultiBlockMasterListener) entity).registerMaster(this);
                }
            }

            if (pos.isEdge()) {
                int meta = 0;
                if (pos.y == bounds.getMinY()) meta |= (0x01 << 1);
                if (pos.x == bounds.getMinX()) meta |= 0x01;
                if (pos.z == bounds.getMinZ()) meta |= (0x01 << 2);

                worldObj.setBlock(pos.x, pos.y, pos.z, RkStuff.blockTankBevelSmall, meta, 2);
                continue;
            }
            if (!pos.isEdge() && pos.isBorder()) {
                if (pos.y == bounds.getMinY()) {
                    if (pos.x == bounds.getMinX()) {
                        worldObj.setBlock(pos.x, pos.y, pos.z, RkStuff.blockTankBevelLarge, 6, 2);
                        continue;
                    }
                    if (pos.x == bounds.getMaxX()) {
                        worldObj.setBlock(pos.x, pos.y, pos.z, RkStuff.blockTankBevelLarge, 7, 2);
                        continue;
                    }
                    if (pos.z == bounds.getMinZ()) {
                        worldObj.setBlock(pos.x, pos.y, pos.z, RkStuff.blockTankBevelLarge, 5, 2);
                        continue;
                    }
                    if (pos.z == bounds.getMaxZ()) {
                        worldObj.setBlock(pos.x, pos.y, pos.z, RkStuff.blockTankBevelLarge, 4, 2);
                        continue;
                    }
                }
                if (pos.y == bounds.getMaxY()) {
                    if (pos.x == bounds.getMinX()) {
                        worldObj.setBlock(pos.x, pos.y, pos.z, RkStuff.blockTankBevelLarge, 2, 2);
                        continue;
                    }
                    if (pos.x == bounds.getMaxX()) {
                        worldObj.setBlock(pos.x, pos.y, pos.z, RkStuff.blockTankBevelLarge, 3, 2);
                        continue;
                    }
                    if (pos.z == bounds.getMinZ()) {
                        worldObj.setBlock(pos.x, pos.y, pos.z, RkStuff.blockTankBevelLarge, 1, 2);
                        continue;
                    }
                    if (pos.z == bounds.getMaxZ()) {
                        worldObj.setBlock(pos.x, pos.y, pos.z, RkStuff.blockTankBevelLarge, 0, 2);
                        continue;
                    }
                }
                if (pos.x == bounds.getMinX() && pos.z == bounds.getMinZ()) {
                    worldObj.setBlock(pos.x, pos.y, pos.z, RkStuff.blockTankBevelLarge, 10, 2);
                    continue;
                }
                if (pos.x == bounds.getMinX() && pos.z == bounds.getMaxZ()) {
                    worldObj.setBlock(pos.x, pos.y, pos.z, RkStuff.blockTankBevelLarge, 8, 2);
                    continue;
                }
                if (pos.x == bounds.getMaxX() && pos.z == bounds.getMinZ()) {
                    worldObj.setBlock(pos.x, pos.y, pos.z, RkStuff.blockTankBevelLarge, 9, 2);
                    continue;
                }
                if (pos.x == bounds.getMaxX() && pos.z == bounds.getMaxZ()) {
                    worldObj.setBlock(pos.x, pos.y, pos.z, RkStuff.blockTankBevelLarge, 11, 2);
                    continue;
                }
                if (pos.y == bounds.getMinY()) {
                    worldObj.setBlockMetadataWithNotify(pos.x, pos.y, pos.z, 2, 2);
                    continue;
                }
                worldObj.setBlockMetadataWithNotify(pos.x, pos.y, pos.z, 1, 2);
            }

        }
        maxStorage = (bounds.getHeight() - 2) * (bounds.getWidthX() - 2) * (bounds.getWidthZ() - 2) * 16000;
        RKLog.info(bounds);
        RKLog.info("MaxStorage: " + maxStorage);
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        return bounds;
    }

    private MultiBlockHelper.Bounds computeMultiStructureBounds() {
        MultiBlockHelper.Bounds tmpBounds = new MultiBlockHelper.Bounds(xCoord, yCoord, zCoord);

        //get boiler base bounds
        for (ForgeDirection direction : new ForgeDirection[]{ForgeDirection.NORTH, ForgeDirection.EAST}) {
            int i = 1;
            while (isValidTankBlock(xCoord + direction.offsetX * i, yCoord, zCoord + direction.offsetZ * i)) {
                i++;
            }
            i--;
            tmpBounds.add(xCoord + direction.offsetX * i, yCoord, zCoord + direction.offsetZ * i);
            direction = direction.getOpposite();
            tmpBounds.add(xCoord + direction.offsetX * i, yCoord, zCoord + direction.offsetZ * i);
        }

        //calculate high
        int i = 0;
        while (isValidTankBlock(tmpBounds.getMinX(), yCoord + i, tmpBounds.getMinZ())) {
            i++;
        }
        i--;
        tmpBounds.add(tmpBounds.getMinX(), yCoord + i, tmpBounds.getMinZ());

        //check if there are only BoilerBseBlocks within the bounds of the boiler base
        for (MultiBlockHelper.Bounds.BlockIterator.BoundsPos pos : tmpBounds) {
            if (pos.isBorder()) {
                if (!isValidTankBlock(pos.x, pos.y, pos.z)) {
                    return null;
                }
            } else {
                if (!(worldObj.getBlock(pos.x, pos.y, pos.z).getMaterial() == Material.air)) {
                    return null;
                }
            }
        }

        //check volume
        if (!(tmpBounds.getHeight() > 2 &&
                tmpBounds.getWidthX() > 2 &&
                tmpBounds.getWidthZ() > 2))
            return null;

        //check border
        MultiBlockHelper.Bounds extendedBounds = tmpBounds.clone();
        extendedBounds.extendDirections(1);

        for (MultiBlockHelper.Bounds.BlockIterator.BoundsPos pos : extendedBounds) {
            if (!pos.isBorder()) continue;
            if (worldObj.getBlock(pos.x, pos.y, pos.z) instanceof ITankBlock) {
                return null;
            }
        }


        return tmpBounds;
    }

    private boolean isValidTankBlock(int x, int y, int z) {
        Block block = worldObj.getBlock(x, y, z);
        if (block instanceof BlockTankAdapter) {
            if (x == xCoord &&
                    y == yCoord &&
                    z == zCoord) {
                return true;
            } else {
                return false;
            }
        }
        return block instanceof ITankBlock;
    }


    @Override
    protected void resetStructure() {
        for (MultiBlockHelper.Bounds.BlockIterator.BoundsPos pos : bounds) {
            Block block = worldObj.getBlock(pos.x, pos.y, pos.z);
            if (block instanceof ITankBlock) {
                if (block instanceof BlockTankBevelSmall ||
                        block instanceof BlockTankBevelLarge) {
                    worldObj.setBlock(pos.x, pos.y, pos.z, RkStuff.blockTank);
                }
                worldObj.setBlockMetadataWithNotify(pos.x, pos.y, pos.z, 0, 2);

                if (block instanceof ITileEntityProvider) {
                    TileEntity entity = worldObj.getTileEntity(pos.x, pos.y, pos.y);
                    if (entity instanceof IMultiBlockMasterListener) {
                        ((IMultiBlockMasterListener) entity).unregisterMaster();
                    }
                }
            }
        }
    }

    @Override
    protected void updateMaster() {
        if (tick == 20) {
            tick = 0;
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        } else {
            tick++;
        }
    }

    @Override
    public void readData(IOStream data) throws IOException {
        if (data.available() == 0) {
            bounds = null;
            currentCoolantStack = null;
            currentFluidStack = null;
            maxStorage = 0;
            return;
        }
        bounds = new MultiBlockHelper.Bounds(0, 0, 0);
        bounds.readData(data);
        currentFluidStack = null;
        currentCoolantStack = null;
        if (data.readFirstBoolean()) { //hasFluidStack
            currentFluidStack = new FluidStack(FluidRegistry.getFluid(data.readFirstInt()), data.readFirstInt());
        }

        if (data.readFirstBoolean()) {
            currentCoolantStack = new CoolantStack(data.readFirstInt(), data.readFirstFloat());
        }
        maxStorage = data.readFirstInt();
    }

    @Override
    public void writeData(IOStream data) {
        if (bounds == null) return;
        bounds.writeData(data);
        if (currentFluidStack != null) {
            data.writeLast(true);
            data.writeLast(currentFluidStack.getFluid().getID());
            data.writeLast(currentFluidStack.amount);
        } else {
            data.writeLast(false);
        }

        if (currentCoolantStack != null) {
            data.writeLast(true);
            data.writeLast(currentCoolantStack.getAmount());
            data.writeLast(currentCoolantStack.getTemperature());
        } else {
            data.writeLast(false);
        }

        data.writeLast(maxStorage);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(xCoord - getInnerRadiusX(), yCoord, zCoord - getInnerRadiusX(), xCoord + getInnerRadiusX() + 1, yCoord + getInnerHeight() + 1, zCoord + getInnerRadiusX() + 1);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        String type = data.getString("storageType");
        if (type.equals("coolant")) {
            currentCoolantStack = new CoolantStack();
            currentCoolantStack.readFromNBT("defaultCoolantStackExtendedVersion", data);
        } else if (type.equals("fluid")) {
            int amount = data.getInteger("amount");
            int fluidId = data.getInteger("fluidId");
            currentFluidStack = new FluidStack(FluidRegistry.getFluid(fluidId), amount);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        if (currentCoolantStack != null) {
            data.setString("storageType", "coolant");
            currentCoolantStack.writeToNBT("defaultCoolantStackExtendedVersion", data);
        } else if (currentFluidStack != null) {
            data.setString("storageType", "fluid");
            data.setInteger("amount", currentFluidStack.amount);
            data.setInteger("fluidId", currentFluidStack.getFluidID());
        } else {
            data.setString("storageType", "none");
        }
    }

    public boolean isCoolantStack() {
        return currentCoolantStack != null;
    }

    public boolean isFluidStack() {
        return currentFluidStack != null;
    }

    public Object getStorageStack() {
        if (isCoolantStack()) {
            return currentCoolantStack;
        } else if (isFluidStack()) {
            return currentFluidStack;
        }
        return null;
    }

    public FluidStack getCurrentFluidStack() {
        return currentFluidStack;
    }

    public CoolantStack getCurrentCoolantStack() {
        return currentCoolantStack;
    }

    public int getCurrentStorage() {
        if (currentFluidStack != null) {
            return currentFluidStack.amount;
        } else if (currentCoolantStack != null) {
            return currentCoolantStack.getAmount();
        } else {
            return 0;
        }
    }

    public int getMaxStorage() {
        return maxStorage;
    }

    public int addFluid(FluidStack resource, boolean doFill) {
        if (isCoolantStack()) return 0;
        int maxAmount = resource.amount;
        maxAmount = Math.min(maxAmount, getMaxStorage());
        if (currentFluidStack == null) {
            if (doFill) {
                currentFluidStack = new FluidStack(resource.getFluid(), maxAmount);
            }
            return maxAmount;
        } else {
            if (currentFluidStack.getFluid().equals(resource.getFluid())) {
                maxAmount = Math.min(maxAmount, maxStorage - currentFluidStack.amount);
                if (doFill) {
                    currentFluidStack.amount += maxAmount;
                }
            } else {
                return 0;
            }
        }
        return maxAmount;
    }

    public FluidStack drainFluid(int maxAmount, boolean doDrain) {
        if (!isFluidStack()) return null;
        int amount = Math.min(maxAmount, currentFluidStack.amount);
        Fluid fluid = currentFluidStack.getFluid();
        if (doDrain) {
            currentFluidStack.amount -= amount;
            if (currentFluidStack.amount == 0) {
                currentFluidStack = null;
            }
        }
        return new FluidStack(fluid, amount);
    }


    public int receiveCoolant(int max, float temperature, boolean simulate) {
        if (isFluidStack()) return 0;
        int maxAmount = Math.min(max, getMaxStorage() - getCurrentStorage());
        if (!simulate) {
            if (currentCoolantStack == null) {
                currentCoolantStack = new CoolantStack(maxAmount, temperature);
            } else {
                currentCoolantStack.add(maxAmount, temperature);
            }
        }
        return maxAmount;
    }

    public void removeCoolant(int amount) {
        currentCoolantStack.remove(amount);
        if (currentCoolantStack.getAmount() <= 0) {
            currentCoolantStack = null;
        }
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentBody, IWailaDataAccessor accessor, IWailaConfigHandler configHandler) {
        if (isCoolantStack()) {
            currentBody.add("Type: Coolant");
            currentBody.add("Temperature: " + getCurrentCoolantStack().getFormattedString());
        } else if (isFluidStack()) {
            currentBody.add("Type: Fluid");
            currentBody.add("Fluid: " + getCurrentFluidStack().getFluid().getLocalizedName(getCurrentFluidStack()));
        }
        currentBody.add(String.format("Storage: %d/%d mB", getCurrentStorage(), getMaxStorage()));
        return currentBody;
    }
}
