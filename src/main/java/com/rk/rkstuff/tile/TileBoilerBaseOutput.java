package com.rk.rkstuff.tile;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.helper.FluidHelper;
import com.rk.rkstuff.network.PacketHandler;
import com.rk.rkstuff.network.message.ICustomMessage;
import com.rk.rkstuff.network.message.MessageCustom;
import javafx.stage.FileChooser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileBoilerBaseOutput extends TileRK implements IBoilerBaseTile, IFluidHandler, ICustomMessage {

    private TileBoilerBaseMaster master;
    //if true => outputSteam else coldCoolant
    private boolean outputSteam = false;

    public void toggleOutput() {
        outputSteam = !outputSteam;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void setMaster(int masterX, int masterY, int masterZ) {
        master = (TileBoilerBaseMaster) worldObj.getTileEntity(masterX, masterY, masterZ);
    }

    @Override
    public void resetMaster() {
        master = null;
    }

    public boolean hasMaster() {
        return master != null;
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
            } else if(!outputSteam && FluidHelper.isColdCoolant(resource.getFluid())) {
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
            } else {
                return master.drainColdCoolant(maxDrain, doDrain);
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
                return FluidHelper.isSteam(fluid) && master.canDrainSteam();
            } else  {
                return FluidHelper.isColdCoolant(fluid) && master.canDrainColdCoolant();
            }
        }
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        if(hasMaster()) {
            if(outputSteam) {
                return master.getTankInfoSteam();
            } else {
                return master.getTankInfoColdCoolant();
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
    }

    @Override
    public void writeData(IOStream data) {
        data.writeLast(outputSteam);
    }
}
