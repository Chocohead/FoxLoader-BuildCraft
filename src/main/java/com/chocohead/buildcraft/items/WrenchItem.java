package com.chocohead.buildcraft.items;

import net.minecraft.src.game.achievements.AchievementList;
import net.minecraft.src.game.block.Block;
import net.minecraft.src.game.entity.player.EntityPlayer;
import net.minecraft.src.game.item.ItemStack;
import net.minecraft.src.game.level.World;

public class WrenchItem extends BuildCraftItem {
	public WrenchItem(int id) {
		super(id);
	}

    @Override
    public boolean isFull3D() {
        return true;
    }

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int facing, float hitX, float hitY, float hitZ) {
        if (!world.multiplayerWorld) {
            Block block = Block.blocksList[world.getBlockId(x, y, z)];

            if (block != null && block.doWrenchRotation(world, x, y, z, world.getBlockMetadata(x, y, z), player.isSneaking())) {
            	player.triggerAchievement(AchievementList.throwAWrenchInTheWorks);
                world.playAuxSFX(2022, x, y, z, 0);
                world.markBlockNeedsUpdate(x, y, z);
                return true;
            }
        }

        return false;
	}
}