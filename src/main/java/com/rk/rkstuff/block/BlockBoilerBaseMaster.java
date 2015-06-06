package com.rk.rkstuff.block;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.tile.TileBoilerBaseMaster;
import com.rk.rkstuff.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockBoilerBaseMaster extends BlockRK implements ITileEntityProvider, IPeripheralProvider {

    private IIcon[] icons = new IIcon[14];

    public BlockBoilerBaseMaster() {
        super(Material.iron, Reference.BLOCK_BOILER_BASE_MASTER);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons[0] = iconRegister.registerIcon(Reference.MOD_ID + ":boiler/" + Reference.BLOCK_BOILER_BASE + 1);
        icons[1] = iconRegister.registerIcon(Reference.MOD_ID + ":boiler/" + Reference.BLOCK_BOILER_BASE_MASTER);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (ForgeDirection.UP.ordinal() == side) {
            return icons[0];
        } else if (ForgeDirection.DOWN.ordinal() == side) {
            return icons[0];
        } else {
            return icons[1];
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if(world.getTileEntity(x, y, z) instanceof TileBoilerBaseMaster) {
            player.openGui(RkStuff.INSTANCE, Reference.GUI_ID_BOILER, world, x, y, z);
        }
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileBoilerBaseMaster();
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

    public TileBoilerBaseMaster getMaster(World world, int x, int y, int z) {
        TileBoilerBaseMaster master = (TileBoilerBaseMaster) world.getTileEntity(x, y, z);
        if(master.isBuild()) return master;
        return null;
    }

    @Override
    public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
        return (IPeripheral) world.getTileEntity(x, y, z);
    }
}
