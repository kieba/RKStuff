package com.rk.rkstuff.item;

import com.rk.rkstuff.RkStuff;
import com.rk.rkstuff.accelerator.AcceleratorControlCoreTypes;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockAcceleratorControlCore extends ItemBlock {

    public ItemBlockAcceleratorControlCore(Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }


    @Override
    public int getMetadata(int damageValue) {
        return damageValue;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return RkStuff.blockAcceleratorControlCore.getUnlocalizedName() + AcceleratorControlCoreTypes.values()[stack.getItemDamage()].name;
    }
    /*
    @Override
    public boolean onItemUse(ItemStack stack,
                             EntityPlayer player, World world, int x, int y,
                             int z, int side, float hitX, float hitY, float hitZ) {

        Block block = world.getBlock(x, y, z) ;
        if (block == Blocks.snow_layer && (world.getBlockMetadata(x, y, z) & 7) < 1)
        {
            side = 1;
        }
        else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush && !block.isReplaceable(world, x, y, z))
        {
            if (side == 0)
                y--;
            if (side == 1)
                y++;
            if (side == 2)
                z--;
            if (side == 3)
                z++;
            if (side == 4)
                x--;
            if (side == 5)
                x++;
        }
        if (stack.stackSize == 0) {
            return false;
        } else if (!player.canPlayerEdit(x, y, z, side, stack)) {
            return false;
        } else if (y == 255 && this.field_150939_a.getMaterial().isSolid()) {
            return false;
        }
        if (world.setBlock(x, y, z, RkStuff.blockAcceleratorControlCore, stack.getItemDamage(), 3)) {
            RkStuff.blockAcceleratorControlCore .onBlockPlacedBy(world, x, y, z, player, stack);
            stack.stackSize--;
            return true;
        }
        return false;
    }
    */
}
