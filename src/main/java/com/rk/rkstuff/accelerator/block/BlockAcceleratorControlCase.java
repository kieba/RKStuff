package com.rk.rkstuff.accelerator.block;

import com.rk.rkstuff.accelerator.AcceleratorHelper;
import com.rk.rkstuff.accelerator.tile.TileAcceleratorMaster;
import com.rk.rkstuff.core.block.BlockRK;
import com.rk.rkstuff.helper.MultiBlockHelper;
import com.rk.rkstuff.util.Pos;
import com.rk.rkstuff.util.RKLog;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockAcceleratorControlCase extends BlockRK implements IAcceleratorControlCaseBlock {

    private IIcon[] icons = new IIcon[2];

    public BlockAcceleratorControlCase() {
        super(Material.iron, Reference.BLOCK_ACCELERATOR_CONTROL_CASE);
    }

    public BlockAcceleratorControlCase(Material m, String name) {
        super(m, name);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons[0] = iconRegister.registerIcon(Reference.MOD_ID + ":accelerator/" + Reference.BLOCK_ACCELERATOR_CONTROL_CASE + "Normal");
        icons[1] = iconRegister.registerIcon(Reference.MOD_ID + ":accelerator/" + Reference.BLOCK_ACCELERATOR_CONTROL_CASE + "Build");
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta == 0) return icons[0];
        return icons[1];
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (world.getBlockMetadata(x, y, z) == 0) {
            //multiblock is not built
            return;
        }

        if (block.getMaterial() == Material.air || AcceleratorHelper.isValidCaseOrCoreControlBlock(block)) {
            TileAcceleratorMaster master = BlockAcceleratorControlCase.getMasterFromControlPos(world, new Pos(x, y, z));
            if (master != null && !master.checkAcceleratorControl()) {
                master.reset();
            }
        }
    }

    public static TileAcceleratorMaster getMasterFromControlPos(World world, Pos controlPos) {
        if (!(world.getBlock(controlPos.x, controlPos.y, controlPos.z) instanceof IAcceleratorControlCaseBlock))
            return null;
        MultiBlockHelper.Bounds tmpBounds = new MultiBlockHelper.Bounds(controlPos.x, controlPos.y, controlPos.z);

        //get accelerator control base bounds
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            int i = 1;
            while (AcceleratorHelper.isValidControlBlock(world, controlPos.x + direction.offsetX * i, controlPos.y + direction.offsetY * i, controlPos.z + direction.offsetZ * i)) {
                i++;
            }
            i--;
            tmpBounds.add(controlPos.x + direction.offsetX * i, controlPos.y + direction.offsetY * i, controlPos.z + direction.offsetZ * i);
        }

        for (MultiBlockHelper.Bounds.BlockIterator.BoundsPos bp : tmpBounds) {
            if (bp.isBorder()) {
                if (world.getBlock(bp.x, bp.y, bp.z) instanceof BlockLHCMaster) {
                    TileEntity t = world.getTileEntity(bp.x, bp.y, bp.z);
                    if (t instanceof TileAcceleratorMaster) {
                        return (TileAcceleratorMaster) t;
                    }
                }
            }
        }
        RKLog.info("No master block found!!!");
        return null;
    }
}
