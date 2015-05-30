package com.rk.rkstuff.tile;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.block.BlockSolarInput;
import com.rk.rkstuff.block.BlockSolarMaster;
import com.rk.rkstuff.block.BlockSolarOutput;
import com.rk.rkstuff.block.ISolarBlock;
import com.rk.rkstuff.helper.MultiBlockHelper;
import com.rk.rkstuff.helper.Pos;
import com.rk.rkstuff.helper.RKLog;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileSolarMaster extends TileMultiBlockMaster {

    private int count;
    private double MAX_MB_PER_PANEL = 1;
    private int MAX_TANK_MB_PER_PANEL = 1000;
    private double productionLastTick = 0;


    private double coolCoolantTank = 0;
    private double hotCoolantTank = 0;



    @Override
    public boolean checkMultiBlockForm() {
        return computeMultiStructureBounds() != null;
    }


    @Override
    protected MultiBlockHelper.Bounds setupStructure() {
        MultiBlockHelper.Bounds tmpBounds = computeMultiStructureBounds();
        for(MultiBlockHelper.Bounds.BlockIterator.BoundsPos pos : tmpBounds){
            boolean hasNorth = pos.hasBlock(ForgeDirection.NORTH);
            boolean hasEast = pos.hasBlock(ForgeDirection.EAST);
            boolean hasSouth = pos.hasBlock(ForgeDirection.SOUTH);
            boolean hasWest = pos.hasBlock(ForgeDirection.WEST);

            int meta = 0;
            meta |= ((hasNorth ? 1 : 0) << (ForgeDirection.NORTH.ordinal() - 2));
            meta |= ((hasEast ? 1 : 0) << (ForgeDirection.EAST.ordinal() - 2));
            meta |= ((hasSouth ? 1 : 0) << (ForgeDirection.SOUTH.ordinal() - 2));
            meta |= ((hasWest ? 1 : 0) << (ForgeDirection.WEST.ordinal() - 2));

            worldObj.setBlockMetadataWithNotify(pos.x, pos.y, pos.z, meta, 2);

            //Update TileIOBlocks
            Block targetBlock = worldObj.getBlock(pos.x, pos.y, pos.z);
            if (targetBlock instanceof BlockSolarInput || targetBlock instanceof BlockSolarOutput) {
                IMultiBlockMasterListener masterListener = (IMultiBlockMasterListener) worldObj.getTileEntity(pos.x, pos.y, pos.z);
                masterListener.registerMaster(this);
            }
        }
        return tmpBounds;
    }

    private MultiBlockHelper.Bounds computeMultiStructureBounds(){
        MultiBlockHelper.Bounds tmpBounds = new MultiBlockHelper.Bounds(xCoord, yCoord, zCoord);
        for(ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS){
            if(direction == ForgeDirection.UP) continue;
            if(direction == ForgeDirection.DOWN) continue;

            int i = 0;
            while (isValidMultiblock(xCoord + direction.offsetX * i, yCoord  + direction.offsetY * i, zCoord  + direction.offsetZ * i)) {
                i++;
            }
            i--;
            tmpBounds.add(xCoord + direction.offsetX * i, yCoord  + direction.offsetY * i, zCoord  + direction.offsetZ * i);
    }

        boolean isValid = true;
        for (int x = tmpBounds.getMinX() - 1; x <= tmpBounds.getMaxX() + 1; x++) {
            for (int z = tmpBounds.getMinZ() - 1; z <= tmpBounds.getMaxZ() + 1; z++) {

                if (x == (tmpBounds.getMinX() - 1) || x == (tmpBounds.getMaxX() + 1)
                        || z == (tmpBounds.getMinZ() - 1) || z == (tmpBounds.getMaxZ() + 1)) {
                    if (isValidMultiblock(x, yCoord, z)) {
                        isValid = false;
                        break;
                    }
                } else {
                    if (!isValidMultiblock(x, yCoord, z)) {
                        isValid = false;
                        break;
                    }
                    if (!new Pos(x, yCoord, z).equals(getPosition()) && isMasterBlock(x, yCoord, z)) {
                        isValid = false;
                        break;
                    }
                }
            }
            if(!isValid) break;
        }

        if(isValid){
            count = tmpBounds.getWidthX() * tmpBounds.getWidthZ();
            return tmpBounds;
        }else {
            return null;
        }
    }

    @Override
    public void resetStructure() {
        if(bounds != null){
            for(Pos pos : bounds){
                worldObj.setBlockMetadataWithNotify(pos.x, pos.y, pos.z, 0, 2);
                Block targetBlock = worldObj.getBlock(pos.x, pos.y, pos.z);
                if (targetBlock instanceof BlockSolarInput || targetBlock instanceof BlockSolarOutput) {
                    IMultiBlockMasterListener masterListener = (IMultiBlockMasterListener) worldObj.getTileEntity(pos.x, pos.y, pos.z);
                    masterListener.unregisterMaster();
                }
            }
        }
    }

    public double getCoolCoolantTank() {
        return coolCoolantTank;
    }

    public void setCoolCoolantTank(double coolCoolantTank) {
        this.coolCoolantTank = coolCoolantTank;
    }

    public double getHotCoolantTank() {
        return hotCoolantTank;
    }

    public void setHotCoolantTank(double hotCoolantTank) {
        this.hotCoolantTank = hotCoolantTank;
    }


    @Override
    protected void updateMaster() {
        if (worldObj.getWorldTime() % 250 == 0) {
            RKLog.info("Time: " + worldObj.getWorldTime() % 24000);
            RKLog.info("AfterTime: " + (worldObj.getWorldTime() + 3000) % 24000);
            RKLog.info("Production: " + getProductionLastTick());
        }
        double amountConvert = count * getCurrentProductionPerSolar();

        amountConvert = Math.min(amountConvert, getMaxTankCapacity() - hotCoolantTank);
        amountConvert = Math.min(amountConvert, coolCoolantTank);

        productionLastTick = amountConvert;

        hotCoolantTank += amountConvert;
        coolCoolantTank -= amountConvert;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        coolCoolantTank = data.getDouble("coolCoolantAmount");
        hotCoolantTank = data.getDouble("hotCoolantAmount");
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setDouble("coolCoolantAmount", coolCoolantTank);
        data.setDouble("hotCoolantAmount", hotCoolantTank);
    }

    private boolean isValidMultiblock(int x, int y, int z){
        Block block = worldObj.getBlock(x, y, z);
        return block instanceof ISolarBlock;
    }

    private boolean isMasterBlock(int x, int y, int z) {
        Block block = worldObj.getBlock(x, y, z);
        return block instanceof BlockSolarMaster;
    }


    public int getMaxTankCapacity() {
        return MAX_TANK_MB_PER_PANEL * count;
    }


    public double getProductionLastTick() {
        return productionLastTick;
    }

    public double getProductionMaximal() {
        return MAX_MB_PER_PANEL * count;
    }

    private double getCurrentProductionPerSolar() {
        long time = worldObj.getWorldTime();
        time += 3000;
        time %= 24000;

        if (time <= 18000) {
            return (0.42 - 0.5 * Math.cos(2 * Math.PI * time / 18000) + 0.08 * Math.cos(4 * Math.PI * time / 18000)) * MAX_MB_PER_PANEL;
        } else {
            return 0;
        }
    }

    @Override
    protected boolean hasGui() {
        return true;
    }

    @Override
    public void writeData(IOStream data) {
        data.writeLast(count);
        data.writeLast(getCoolCoolantTank());
        data.writeLast(getHotCoolantTank());
        data.writeLast(getProductionLastTick());
    }

    @Override
    public void readData(IOStream data) throws IOException {
        count = data.readFirstInt();
        coolCoolantTank = data.readFirstDouble();
        hotCoolantTank = data.readFirstDouble();
        productionLastTick = data.readLastDouble();
    }
}
