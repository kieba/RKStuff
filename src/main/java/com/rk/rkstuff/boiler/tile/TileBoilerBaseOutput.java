package com.rk.rkstuff.boiler.tile;

import com.rk.rkstuff.coolant.tile.ICoolantConnection;
import com.rk.rkstuff.core.tile.IMultiBlockMasterListener;
import com.rk.rkstuff.core.tile.TileMultiBlockMaster;
import com.rk.rkstuff.core.tile.TileRK;
import com.rk.rkstuff.helper.FluidHelper;
import com.rk.rkstuff.network.PacketHandler;
import com.rk.rkstuff.network.message.ICustomMessage;
import com.rk.rkstuff.network.message.MessageCustom;
import com.rk.rkstuff.util.RKLog;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileBoilerBaseOutput extends TileRK implements IMultiBlockMasterListener, IFluidHandler, ICoolantConnection, ICustomMessage {

    private TileBoilerBaseMaster master;
    //if true => outputSteam else coldCoolant
    private boolean outputSteam = false;

    public void toggleOutput() {
        outputSteam = !outputSteam;
        RKLog.info("Output Steam: " + outputSteam);
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public boolean hasMaster() {
        return master != null;
    }

    public boolean outputSteam() {
        return outputSteam;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (hasMaster() && !outputSteam) {
            int amount = FluidHelper.outputCoolantToNeighbours(neighbours, master.getMaxOutputCoolant(), master.getTemperature());
            master.drainCoolant(amount);
        }
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if(hasMaster()) {
            if(outputSteam && FluidHelper.isSteam(resource.getFluid())) {
                return drain(from, resource.amount, doDrain);
            }
        }
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        if(hasMaster()) {
            if(outputSteam) {
                return master.drainSteam(maxDrain, doDrain);
            }
        }
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        if(hasMaster()) {
            if(outputSteam) {
                return FluidHelper.isSteam(fluid);
            }
        }
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        if(hasMaster()) {
            if(outputSteam) {
                return master.getTankInfoOutput();
            }
        }
        return new FluidTankInfo[0];
    }

    @Override
    public Packet getDescriptionPacket() {
        return PacketHandler.INSTANCE.getPacketFrom(new MessageCustom(this));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        outputSteam = nbt.getBoolean("outputSteam");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("outputSteam", outputSteam);
    }

    @Override
    public void readData(IOStream data) throws IOException {
        outputSteam = data.readFirstBoolean();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord); //re-render block
    }

    @Override
    public void writeData(IOStream data) {
        data.writeLast(outputSteam);
    }

    @Override
    protected boolean cacheNeighbours() {
        return true;
    }

    @Override
    public void registerMaster(TileMultiBlockMaster tileMaster) {
        master = (TileBoilerBaseMaster) tileMaster;
    }

    @Override
    public void unregisterMaster() {
        master = null;
    }

    @Override
    public boolean canConnect(ForgeDirection from) {
        return true;
    }
}
