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

public class BlockFusionControlCase extends BlockRK implements IFusionControlCaseBlock {

    public BlockFusionControlCase() {
        super(Material.iron, Reference.BLOCK_FUSION_CONTROL_CASE);
    }

    public BlockFusionControlCase(Material m, String name) {
        super(m, name);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta == 0) return Blocks.coal_ore.getIcon(0, 0);
        return Blocks.coal_block.getIcon(0, 0);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (world.getBlockMetadata(x, y, z) == 0) {
            //multiblock is not built
            return;
        }

        if (block.getMaterial() == Material.air || FusionHelper.isValidCaseOrCoreControlBlock(block)) {
            TileFusionControlMaster master = BlockFusionControlCase.getMasterFromControlPos(world, new Pos(x, y, z));
            if (master != null && !master.checkFusionControl()) {
                master.reset();
            }
        }
    }

    public static TileFusionControlMaster getMasterFromControlPos(World world, Pos controlPos) {
        if (!(world.getBlock(controlPos.x, controlPos.y, controlPos.z) instanceof IFusionControlCaseBlock)) return null;
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
