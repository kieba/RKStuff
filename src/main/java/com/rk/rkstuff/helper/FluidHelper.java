package com.rk.rkstuff.helper;

import com.rk.rkstuff.RkStuff;
import javafx.stage.FileChooser;
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

    public static boolean isHotCoolant(Fluid fluid) {
        return fluid.getID() == RkStuff.hotCoolant.getID();
    }

    public static boolean isCoolCoolant(Fluid fluid) {
        return fluid.getID() == RkStuff.coolCoolant.getID();
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


}
