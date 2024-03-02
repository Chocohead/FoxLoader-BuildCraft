package com.chocohead.buildcraft.items;

import net.minecraft.src.client.renderer.block.icon.Icon;
import net.minecraft.src.game.block.Block;
import net.minecraft.src.game.item.ItemBlock;
import net.minecraft.src.game.item.ItemStack;

import com.fox2code.foxloader.registry.GameRegistry;

import com.chocohead.buildcraft.blocks.PipeBlock;

public class PipeItem extends ItemBlock {
	public PipeItem(Block block, int id) {
		super(id - GameRegistry.PARAM_ITEM_ID_DIFF, block.blockID);

		setHasSubtypes(true);
		setMaxDamage(0);
	}

	@Override
	public Icon getIconFromDamage(int damage) {
		return Block.blocksList[blockID].getIcon(1, damage);
	}

	@Override
	public String getItemNameIS(ItemStack stack) {
		if (stack == null) return getItemName();
		return ((PipeBlock) Block.blocksList[blockID]).getBlockName(stack.getItemDamage());
	}

	@Override
	public int getPlacedBlockMetadata(int damage) {
		return damage;
	}
}