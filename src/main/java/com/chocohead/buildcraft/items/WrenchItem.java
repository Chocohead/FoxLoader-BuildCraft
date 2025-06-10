package com.chocohead.buildcraft.items;

import net.minecraft.common.stats.Achievements;
import net.minecraft.common.block.Block;
import net.minecraft.common.block.Blocks;
import net.minecraft.common.entity.player.EntityPlayer;
import net.minecraft.common.item.ItemStack;
import net.minecraft.common.world.World;

public class WrenchItem extends BuildCraftItem {
	public WrenchItem(String id) {
		super(id);
	}

    @Override
    public boolean isFull3D() {
        return true;
    }

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            Block block = Blocks.BLOCKS_LIST[world.getBlockId(x, y, z)];

            if (block != null && block.doWrenchRotation(world, x, y, z, world.getBlockMetadata(x, y, z), facing, player)) {
            	player.triggerAchievement(Achievements.A_WRENCH_IN_THE_WORKS);
                world.playAuxSFX(2022, x, y, z, 0);
                world.markBlockNeedsUpdate(x, y, z);
                return true;
            }
        }

        return false;
	}
}