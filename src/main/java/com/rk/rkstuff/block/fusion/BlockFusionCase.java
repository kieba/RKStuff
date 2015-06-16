package com.rk.rkstuff.block.fusion;

import com.rk.rkstuff.block.BlockRK;
import com.rk.rkstuff.helper.FusionHelper;
import com.rk.rkstuff.helper.MultiBlockHelper;
import com.rk.rkstuff.helper.Pos;
import com.rk.rkstuff.helper.RKLog;
import com.rk.rkstuff.tile.fusion.TileFusionControlMaster;
import com.rk.rkstuff.util.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockFusionCase extends BlockRK implements IFusionCaseBlock {

    public BlockFusionCase() {
        super(Material.iron, Reference.BLOCK_FUSION_CASE);
    }

    protected BlockFusionCase(Material m, String name) {
        super(m, name);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta == 0) return Blocks.iron_ore.getIcon(0, 0);
        return Blocks.iron_block.getIcon(0, 0);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (world.getBlockMetadata(x, y, z) == 0) {
            //multiblock is not built
            return;
        }

        if (block.getMaterial() == Material.air || FusionHelper.isValidCaseOrCoreBlock(block)) {
            checkMultiStructure(world, x, y, z);
        }

        /* this way is more efficient, but the other way works for all IFusionCaseBlocks
        for (ForgeDirection d : ForgeDirection.VALID_DIRECTIONS) {
            if (FusionHelper.isValidCoreBlock(world, x + d.offsetX, y + d.offsetY, z + d.offsetZ)) {
                ForgeDirection o = d.getOpposite();
                if (FusionHelper.isValidCaseOrCoreBlock(world, x + o.offsetX, y + o.offsetY, z + o.offsetZ)) {
                    checkMultiStructure(world, x, y, z); //without master.checkMultiBlockForm()
                    return;
                }

                for (ForgeDirection n : ForgeDirection.VALID_DIRECTIONS) {
                    if (n == d || n == o) continue;
                    if (!FusionHelper.isValidCaseBlock(world, x + n.offsetX, y + n.offsetY, z + n.offsetZ)) {
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
        b.add(x - 1, y - 1, z - 1);
        b.add(x + 1, y + 1, z + 1);
        for (MultiBlockHelper.Bounds.BlockIterator.BoundsPos p : b) {
            if (FusionHelper.isValidCoreBlock(world, p.x, p.y, p.z)) {
                //we found a core block, now we will follow the core to the control block
                Pos controlPos = followCoreBlocks(world, p.x, p.y, p.z);
                if (controlPos != null) {
                    TileFusionControlMaster master = BlockFusionControlCase.getMasterFromControlPos(world, controlPos);
                    if (master != null && !master.checkFusionRing()) {
                        master.reset();
                        break;
                    }
                }
            }
        }
    }

    private Pos followCoreBlocks(World world, int x, int y, int z) {
        FusionHelper.FusionCoreDir oldDir = FusionHelper.FusionCoreDir.XnZn;
        FusionHelper.FusionCoreDir dir = FusionHelper.FusionCoreDir.XnZn;
        boolean stop = false;
        while (!stop) {
            while (FusionHelper.isValidCoreBlock(world, x + dir.xOff, y, z + dir.zOff)) {
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
            if (FusionHelper.isValidControlCaseBlock(world, x + d.offsetX, y, z + d.offsetZ)) {
                return new Pos(x + d.offsetX, y, z + d.offsetZ);
            }
        }

        RKLog.info("No control blocks found!!!");
        return null;
    }

}
