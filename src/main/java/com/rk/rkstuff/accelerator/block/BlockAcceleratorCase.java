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
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockAcceleratorCase extends BlockRK implements IAcceleratorCaseBlock {
    private IIcon[] icons = new IIcon[2];

    public BlockAcceleratorCase() {
        super(Material.iron, Reference.BLOCK_ACCELERATOR_CASE);
    }

    protected BlockAcceleratorCase(Material m, String name) {
        super(m, name);
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons[0] = iconRegister.registerIcon(Reference.MOD_ID + ":accelerator/" + Reference.BLOCK_ACCELERATOR_CASE + 1);
        icons[1] = iconRegister.registerIcon(Reference.MOD_ID + ":accelerator/" + Reference.BLOCK_ACCELERATOR_CASE + 2);
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

        if (block.getMaterial() == Material.air || AcceleratorHelper.isValidCaseOrCoreBlock(block)) {
            checkMultiStructure(world, x, y, z);
        }

        /* this way is more efficient, but the other way works for all IAcceleratorCaseBlocks
        for (ForgeDirection d : ForgeDirection.VALID_DIRECTIONS) {
            if (AcceleratorHelper.isValidCoreBlock(world, x + d.offsetX, y + d.offsetY, z + d.offsetZ)) {
                ForgeDirection o = d.getOpposite();
                if (AcceleratorHelper.isValidCaseOrCoreBlock(world, x + o.offsetX, y + o.offsetY, z + o.offsetZ)) {
                    checkMultiStructure(world, x, y, z); //without master.checkMultiBlockForm()
                    return;
                }

                for (ForgeDirection n : ForgeDirection.VALID_DIRECTIONS) {
                    if (n == d || n == o) continue;
                    if (!AcceleratorHelper.isValidCaseBlock(world, x + n.offsetX, y + n.offsetY, z + n.offsetZ)) {
                        checkMultiStructure(world, x, y, z); //without master.checkMultiBlockForm()
                        return;
                    }
                }
            }
        }
        */

    }

    private void checkMultiStructure(World world, int x, int y, int z) {
        MultiBlockHelper.Bounds b = new MultiBlockHelper.Bounds(x, y, z);
        b.extendDirections(1);
        for (MultiBlockHelper.Bounds.BlockIterator.BoundsPos p : b) {
            if (AcceleratorHelper.isValidCoreBlock(world, p.x, p.y, p.z)) {
                //we found a core block, now we will follow the core to the control block
                Pos controlPos = followCoreBlocks(world, p.x, p.y, p.z);
                if (controlPos != null) {
                    TileAcceleratorMaster master = BlockAcceleratorControlCase.getMasterFromControlPos(world, controlPos);
                    if (master != null && !master.checkAcceleratorRing()) {
                        master.reset();
                        break;
                    }
                }
            }
        }
    }

    private Pos followCoreBlocks(World world, int x, int y, int z) {
        AcceleratorHelper.AcceleratorCoreDir oldDir = AcceleratorHelper.AcceleratorCoreDir.XnZn;
        AcceleratorHelper.AcceleratorCoreDir dir = AcceleratorHelper.AcceleratorCoreDir.XnZn;
        boolean stop = false;
        while (!stop) {
            while (AcceleratorHelper.isValidCoreBlock(world, x + dir.xOff, y, z + dir.zOff)) {
                x += dir.xOff;
                z += dir.zOff;
                oldDir = dir;
            }
            dir = dir.getNext(false);
            if (dir == oldDir.opposite()) dir = dir.getNext(false); // don't move back
            if (dir == oldDir) {
                stop = true;
            }
        }

        for (ForgeDirection d : ForgeDirection.VALID_DIRECTIONS) {
            if (d == ForgeDirection.DOWN) continue;
            if (d == ForgeDirection.UP) continue;
            if (AcceleratorHelper.isValidControlCaseBlock(world, x + d.offsetX, y, z + d.offsetZ)) {
                return new Pos(x + d.offsetX, y, z + d.offsetZ);
            }
        }

        RKLog.info("No control blocks found!!!");
        return null;
    }

}
