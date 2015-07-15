package com.rk.rkstuff.helper;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.coolant.tile.ICoolantReceiver;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

public class FluidHelper {

    public static Fluid steam = null;
    public static Fluid water = null;

    public static boolean isSteam(Fluid fluid) {
        return fluid == steam;
    }

    public static boolean isUsedCoolant(Fluid fluid) {
        return fluid.getID() == RkStuff.fluidUsedCoolant.getID();
    }

    public static boolean isCoolant(Fluid fluid) {
        return fluid.getID() == RkStuff.fluidCoolant.getID();
    }

    public static boolean isWater(Fluid fluid) {
        return fluid == water;
    }

    public static int insertFluidIntoNeighbourFluidHandler(TileEntity source, ForgeDirection direction, FluidStack fluidStack, boolean doFill) {
        TileEntity localTileEntity = BlockHelper.getNeighbourTileEntity(source, direction);

        return (localTileEntity instanceof IFluidHandler) ? ((IFluidHandler) localTileEntity).fill(direction.getOpposite(), fluidStack, doFill) : 0;
    }

    public static int insertFluidIntoNeighbourFluidHandler(TileEntity source, int direction, FluidStack fluidStack, boolean doFill) {
        return insertFluidIntoNeighbourFluidHandler(source, ForgeDirection.VALID_DIRECTIONS[direction], fluidStack, doFill);
    }

    public static int outputCoolantToNeighbours(TileEntity[] neighbours, int maxAmount, double temperature) {
        if (maxAmount == 0) return 0;
        int totalInput = 0;
        int[] maxInput = new int[6];
        for (int i = 0; i < 6; i++) {
            if (!(neighbours[i] instanceof ICoolantReceiver)) continue;
            ICoolantReceiver rcv = (ICoolantReceiver) neighbours[i];
            maxInput[i] = rcv.receiveCoolant(ForgeDirection.values()[i].getOpposite(), Integer.MAX_VALUE, 0.0f, true);
            totalInput += maxInput[i];
        }

        float scale = maxAmount / (float) totalInput;
        if (scale > 1.0f) scale = 1.0f;

        int outputted = 0;
        for (int i = 0; i < 6; i++) {
            if (!(neighbours[i] instanceof ICoolantReceiver)) continue;
            ICoolantReceiver rcv = (ICoolantReceiver) neighbours[i];
            int amount = (int) Math.floor(maxInput[i] * scale);
            if (amount == 0) continue;
            outputted += rcv.receiveCoolant(ForgeDirection.values()[i].getOpposite(), amount, temperature, false);
        }

        return outputted;
    }

    public static int outputCoolantToNeighbours(TileEntity[] neighbours, byte[] sideCache, byte outputSide, int maxAmount, double temperature) {
        if (maxAmount == 0) return 0;
        int totalInput = 0;
        int[] maxInput = new int[6];
        for (int i = 0; i < 6; i++) {
            if (!(neighbours[i] instanceof ICoolantReceiver) || sideCache[i] != outputSide) continue;
            ICoolantReceiver rcv = (ICoolantReceiver) neighbours[i];
            maxInput[i] = rcv.receiveCoolant(ForgeDirection.values()[i].getOpposite(), Integer.MAX_VALUE, 0.0f, true);
            totalInput += maxInput[i];
        }

        float scale = maxAmount / (float) totalInput;
        if (scale > 1.0f) scale = 1.0f;

        int outputted = 0;
        for (int i = 0; i < 6; i++) {
            if (!(neighbours[i] instanceof ICoolantReceiver) || sideCache[i] != outputSide) continue;
            ICoolantReceiver rcv = (ICoolantReceiver) neighbours[i];
            int amount = (int) Math.floor(maxInput[i] * scale);
            if (amount == 0) continue;
            outputted += rcv.receiveCoolant(ForgeDirection.values()[i].getOpposite(), amount, temperature, false);
        }

        return outputted;
    }
}
