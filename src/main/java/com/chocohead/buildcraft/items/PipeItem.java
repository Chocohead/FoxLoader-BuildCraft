package com.chocohead.buildcraft.items;

import java.util.ArrayList;

import net.minecraft.common.block.icon.Icon;
import net.minecraft.common.block.Block;
import net.minecraft.common.block.Blocks;
import net.minecraft.common.item.block.ItemBlock;
import net.minecraft.common.item.ItemStack;

import com.chocohead.buildcraft.blocks.PipeBlock;

public class PipeItem extends ItemBlock {
	public PipeItem(Block block) {
		super(block);

		setHasSubtypes(true);
		setMaxDamage(0);
	}

	@Override
	public Icon getIconFromDamage(int damage) {
		return Blocks.BLOCKS_LIST[blockID].getIcon(1, damage);
	}

	@Override
	public String getItemNameIS(ItemStack stack) {
		if (stack == null) return getItemName();
		return ((PipeBlock) Blocks.BLOCKS_LIST[blockID]).getBlockName(stack.getItemDamage());
	}

	@Override
	public int getPlacedBlockMetadata(int damage) {
		return damage;
	}

	@Override
	public void populateCreativeInventory(ArrayList<ItemStack> stacks) {
		for (int i = 0; i <= 8; i++) {
			stacks.add(new ItemStack(this, 1, i));
		}
	}
}