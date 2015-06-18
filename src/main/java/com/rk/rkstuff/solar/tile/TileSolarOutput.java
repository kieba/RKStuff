package com.rk.rkstuff.solar.tile;

import com.rk.rkstuff.coolant.tile.ICoolantConnection;
import com.rk.rkstuff.coolant.tile.ICoolantReceiver;
import com.rk.rkstuff.core.tile.IMultiBlockMasterListener;
import com.rk.rkstuff.core.tile.TileMultiBlockMaster;
import com.rk.rkstuff.core.tile.TileRK;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class TileSolarOutput extends TileRK implements IMultiBlockMasterListener, ICoolantConnection {
    private TileSolarMaster master;

    @Override
    protected boolean cacheNeighbours() {
        return true;
    }

    @Override
    public void readData(IOStream data) throws IOException {
    }

    @Override
    public void writeData(IOStream data) {

    }

    @Override
    public void registerMaster(TileMultiBlockMaster tileMaster) {
        master = (TileSolarMaster) tileMaster;
    }

    @Override
    public void unregisterMaster() {
        master = null;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (master != null && master.getCoolantBuffer().getAmount() > 1) {
            int totalInput = 0;
            int[] maxInput = new int[6];
            for (int i = 0; i < 6; i++) {
                TileEntity te = getNeighbour(i);
                if (te == null || !(te instanceof ICoolantReceiver) || te instanceof TileSolarInput) continue;
                ICoolantReceiver rcv = (ICoolantReceiver) te;
                maxInput[i] = rcv.receiveCoolant(ForgeDirection.values()[i], Integer.MAX_VALUE, 0.0f, true);
                totalInput += maxInput[i];
            }

            float scale = master.getCoolantBuffer().getAmount() / (float) totalInput;
            if (scale > 1.0f) scale = 1.0f;

            for (int i = 0; i < 6; i++) {
                if (maxInput[i] == 0) continue;
                ICoolantReceiver rcv = (ICoolantReceiver) getNeighbour(i);
                int amount = (int) Math.floor(maxInput[i] * scale);
                int received = rcv.receiveCoolant(ForgeDirection.values()[i], amount, master.getCoolantBuffer().getTemperature(), false);
                master.getCoolantBuffer().remove(received);
            }
        }
    }

    @Override
    public boolean canConnect(ForgeDirection from) {
        if (from == ForgeDirection.UP) return false;
        return true;
    }
}
