package com.rk.rkstuff.coolant.block;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.coolant.tile.TileCoolantPipe;
import com.rk.rkstuff.core.block.BlockRK;
import com.rk.rkstuff.core.tile.INeighbourListener;
import com.rk.rkstuff.util.Reference;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.LinkedList;

public class BlockCoolantPipe extends BlockRK implements ITileEntityProvider {

    private float cableDiameter = 4.0F / 16.0F;

    public BlockCoolantPipe() {
        super(Material.iron, Reference.BLOCK_COOLANT_PIPE);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileCoolantPipe();
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isBlockNormalCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
        int offsetX = x - tileX;
        int offsetY = y - tileY;
        int offsetZ = z - tileZ;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (dir.offsetX == offsetX && dir.offsetY == offsetY && dir.offsetZ == offsetZ) {
                TileEntity tileEntity = world.getTileEntity(x, y, z);
                if (tileEntity instanceof INeighbourListener) {
                    ((INeighbourListener) tileEntity).onNeighborTileChange(dir.getOpposite());
                    break;
                }
            }
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileCoolantPipe) ((TileCoolantPipe) te).onBlockPlaced();
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileCoolantPipe) {
            TileCoolantPipe tile = (TileCoolantPipe) te;
            float hDiameter = cableDiameter / 2.0F;
            float[] bounds = new float[]{-hDiameter, hDiameter, -hDiameter, hDiameter, -hDiameter, hDiameter};
            if (tile != null) {
                bounds = getBounds(tile);
            }
            double minY = y + bounds[0];
            double maxY = y + bounds[1];
            double minZ = z + bounds[2];
            double maxZ = z + bounds[3];
            double minX = x + bounds[4];
            double maxX = x + bounds[5];
            return AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
        }
        return AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
    }

    private float[] getBounds(TileCoolantPipe tile) {
        // minY, maxY, minZ, maxZ, minX, maxX
        float[] bounds = new float[6];
        boolean[] connected = tile.getConnectedSides();
        float hDiameter = cableDiameter / 2.0F;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            float width = 0.5F;
            if (!connected[dir.ordinal()]) {//(sides & dir.flag) != dir.flag) {
                width = hDiameter;
            }
            if (dir.ordinal() % 2 == 0) {
                width *= -1;
            }
            bounds[dir.ordinal()] = 0.5F + width;
        }
        return bounds;
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 origin, Vec3 direction) {
        MovingObjectPosition hit = null;
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileCoolantPipe) {
            LinkedList<AxisAlignedBB> bbs = getAllAABBs((TileCoolantPipe) tile);
            for (AxisAlignedBB bb : bbs) {
                setBlockBounds((float) bb.minX, (float) bb.minY, (float) bb.minZ, (float) bb.maxX, (float) bb.maxY, (float) bb.maxZ);
                hit = super.collisionRayTrace(world, x, y, z, origin, direction);
                if (hit != null) {
                    break;
                }
            }
        }
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        return hit;
    }

    private LinkedList<AxisAlignedBB> getAllAABBs(TileCoolantPipe tile) {
        LinkedList<AxisAlignedBB> bbs = new LinkedList<AxisAlignedBB>();
        float cableMin = 0.5F - cableDiameter / 2.0F;
        float cableMax = 0.5F + cableDiameter / 2.0F;
        int conSides = 0;
        boolean[] isConnected = tile.getConnectedSides();
        for (int i = 0; i < 6; i++) {
            if (isConnected[i]) {
                conSides |= (0x01 << i);
            }
        }

        if (conSides != 0x03 && conSides != 0x0C && conSides != 0x30) {
            bbs.add(AxisAlignedBB.getBoundingBox(cableMin, cableMin, cableMin, cableMax, cableMax, cableMax));
        }
        if (isConnected[ForgeDirection.WEST.ordinal()]) {
            bbs.add(AxisAlignedBB.getBoundingBox(0.0F, cableMin, cableMin, cableMax, cableMax, cableMax));
        }
        if (isConnected[ForgeDirection.EAST.ordinal()]) {
            bbs.add(AxisAlignedBB.getBoundingBox(cableMin, cableMin, cableMin, 1.0F, cableMax, cableMax));
        }

        if (isConnected[ForgeDirection.DOWN.ordinal()]) {
            bbs.add(AxisAlignedBB.getBoundingBox(cableMin, 0.0F, cableMin, cableMax, cableMax, cableMax));
        }

        if (isConnected[ForgeDirection.UP.ordinal()]) {
            bbs.add(AxisAlignedBB.getBoundingBox(cableMin, cableMin, cableMin, cableMax, 1.0F, cableMax));
        }

        if (isConnected[ForgeDirection.NORTH.ordinal()]) {
            bbs.add(AxisAlignedBB.getBoundingBox(cableMin, cableMin, 0.0F, cableMax, cableMax, cableMax));
        }

        if (isConnected[ForgeDirection.SOUTH.ordinal()]) {
            bbs.add(AxisAlignedBB.getBoundingBox(cableMin, cableMin, cableMin, cableMax, cableMax, 1.0F));
        }
        return bbs;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return RkStuff.coolCoolant.getFlowingIcon();
    }
}
