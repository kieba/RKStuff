package com.rk.rkstuff.block;

import com.rk.rkstuff.helper.MultiBlockHelper;
import com.rk.rkstuff.tile.TileBoilerBaseMaster;
import com.rk.rkstuff.tile.TileBoilerBaseOutput;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockBoilerBaseOutput extends BlockRK implements ITileEntityProvider, IBoilerBaseBlock {

    private IIcon[] icons = new IIcon[2];

    public BlockBoilerBaseOutput() {
        super(Material.iron, Reference.BLOCK_BOILER_BASE_OUTPUT);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons[0] = iconRegister.registerIcon(Reference.MOD_ID + ":boiler/" + Reference.BLOCK_BOILER_BASE_OUTPUT + 1);
        icons[1] = iconRegister.registerIcon(Reference.MOD_ID + ":boiler/" + Reference.BLOCK_BOILER_BASE_OUTPUT + 2);
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile instanceof TileBoilerBaseOutput) {
            TileBoilerBaseOutput outputTile = (TileBoilerBaseOutput) tile;
            if(outputTile.outputSteam()) {
                return icons[1];
            } else {
                return icons[0];
            }
        }
        return getIcon(side, 0);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return icons[0];
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if(!world.isRemote) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if(tile instanceof TileBoilerBaseOutput) {
                ((TileBoilerBaseOutput) tile).toggleOutput();
                return true;
            }
        }
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileBoilerBaseOutput();
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block changeBlock) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0) return;
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            if (direction == ForgeDirection.DOWN) continue;
            if (direction == ForgeDirection.UP) continue;
            boolean hasNeighbour = (meta >> ((direction.ordinal() - 2)) & 0x01) == 1;
            boolean isNeighbourBoilerBaseBlock = isValidBoilerBase(world, x + direction.offsetX, y, z + direction.offsetZ);
            if (!hasNeighbour && isNeighbourBoilerBaseBlock || hasNeighbour && !isNeighbourBoilerBaseBlock) {
                TileBoilerBaseMaster master = getMaster(world, x, y, z);
                if(master != null) master.reset();
                return;
            }
        }
    }

    private boolean isValidBoilerBase(World world, int x, int y, int z){
        Block block = world.getBlock(x, y, z);
        return block instanceof IBoilerBaseBlock || block instanceof BlockBoilerBaseMaster;
    }

    @Override
    public TileBoilerBaseMaster getMaster(World world, int x, int y, int z) {
        if(world.getBlockMetadata(x, y, z) == 0) return null;

        MultiBlockHelper.Bounds tmpBounds = new MultiBlockHelper.Bounds(x, y, z);
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            if (direction == ForgeDirection.UP) continue;
            if (direction == ForgeDirection.DOWN) continue;

            int i = 0;
            while (isValidBoilerBase(world, x + direction.offsetX * i, y, z + direction.offsetZ * i)) {
                i++;
            }
            i--;
            tmpBounds.add(x + direction.offsetX * i, y, z + direction.offsetZ * i);

        }
        for (MultiBlockHelper.Bounds.BlockIterator.BoundsPos pos : tmpBounds) {
            if (world.getBlock(pos.x, pos.y, pos.z) instanceof BlockBoilerBaseMaster) {
                TileBoilerBaseMaster master = (TileBoilerBaseMaster) world.getTileEntity(pos.x, pos.y, pos.z);
                if(master.isBuild()) return master;
            }
        }
        return null;
    }
}
