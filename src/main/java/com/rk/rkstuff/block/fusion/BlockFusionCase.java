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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockFusionCase extends BlockRK implements IFusionCaseBlock {

    public BlockFusionCase() {
        super(Material.iron, Reference.BLOCK_FUSION_CASE);
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

        for (ForgeDirection d : ForgeDirection.VALID_DIRECTIONS) {
            if (FusionHelper.isValidCoreBlock(world, x + d.offsetX, y + d.offsetY, z + d.offsetZ)) {
                ForgeDirection o = d.getOpposite();
                if (FusionHelper.isValidCaseOrCoreBlock(world, x + o.offsetX, y + o.offsetY, z + o.offsetZ)) {
                    resetMultiStructure(world, x, y, z);
                    return;
                }

                for (ForgeDirection n : ForgeDirection.VALID_DIRECTIONS) {
                    if (n == d || n == o) continue;
                    if (!FusionHelper.isValidCaseBlock(world, x + n.offsetX, y + n.offsetY, z + n.offsetZ)) {
                        resetMultiStructure(world, x, y, z);
                        return;
                    }
                }
            }
        }

    }

    private void resetMultiStructure(World world, int x, int y, int z) {
        MultiBlockHelper.Bounds b = new MultiBlockHelper.Bounds(x, y, z);
        b.add(x - 1, y - 1, z - 1);
        b.add(x + 1, y + 1, z + 1);
        for (MultiBlockHelper.Bounds.BlockIterator.BoundsPos p : b) {
            if (FusionHelper.isValidCoreBlock(world, p.x, p.y, p.z)) {
                //we found a core block, now we will follow the core to the control block
                Pos controlPos = followCoreBlocks(world, new Pos(p.x, p.y, p.z));
                if (controlPos != null) {
                    TileFusionControlMaster master = getMasterFromControlPos(world, controlPos);
                    if (master != null) {
                        master.reset();
                        break;
                    }
                }
            }
        }
    }

    private Pos followCoreBlocks(World world, Pos coreBlock) {
        Pos current = coreBlock.clone();
        FusionHelper.FusionCoreDir oldDir = null;
        FusionHelper.FusionCoreDir dir = FusionHelper.FusionCoreDir.XnZn;
        boolean stop = false;
        while (!stop) {
            while (FusionHelper.isValidCoreBlock(world, current.x + dir.xOff, current.y, current.z + dir.zOff)) {
                current.x += dir.xOff;
                current.z += dir.zOff;
                oldDir = dir;
            }
            dir = dir.getNext(false);
            if (oldDir != null && dir == oldDir.opposite()) dir = dir.getNext(false); // don't move back
            if (dir == oldDir) {
                stop = true;
            }
        }

        for (ForgeDirection d : ForgeDirection.VALID_DIRECTIONS) {
            if (d == ForgeDirection.DOWN) continue;
            if (d == ForgeDirection.UP) continue;
            if (FusionHelper.isValidControlCaseBlock(world, current.x + d.offsetX, current.y, current.z + d.offsetZ)) {
                return new Pos(current.x + d.offsetX, current.y, current.z + d.offsetZ);
            }
        }

        RKLog.info("No control blocks found!!!");
        return null;
    }

    private TileFusionControlMaster getMasterFromControlPos(World world, Pos controlPos) {
        MultiBlockHelper.Bounds tmpBounds = new MultiBlockHelper.Bounds(controlPos.x, controlPos.y, controlPos.z);

        //get fusion control base bounds
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            int i = 1;
            while (FusionHelper.isValidControlBlock(world, controlPos.x + direction.offsetX * i, controlPos.y + direction.offsetY * i, controlPos.z + direction.offsetZ * i)) {
                i++;
            }
            i--;
            tmpBounds.add(controlPos.x + direction.offsetX * i, controlPos.y + direction.offsetY * i, controlPos.z + direction.offsetZ * i);
        }

        int xMin = tmpBounds.getMinX();
        int yMin = tmpBounds.getMinY();
        int zMin = tmpBounds.getMinZ();
        int xMax = tmpBounds.getMaxX();
        int yMax = tmpBounds.getMaxY();
        int zMax = tmpBounds.getMaxZ();
        for (MultiBlockHelper.Bounds.BlockIterator.BoundsPos bp : tmpBounds) {
            if (bp.x == xMin || bp.x == xMax || bp.y == yMin || bp.y == yMax || bp.z == zMin || bp.z == zMax) {
                if (world.getBlock(bp.x, bp.y, bp.z) instanceof BlockFusionControlMaster) {
                    TileEntity t = world.getTileEntity(bp.x, bp.y, bp.z);
                    if (t instanceof TileFusionControlMaster) {
                        return (TileFusionControlMaster) t;
                    }
                }
            }
        }
        RKLog.info("No master block found!!!");
        return null;
    }
}
