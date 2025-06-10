package com.chocohead.buildcraft.items;

import java.util.ArrayList;

import net.minecraft.common.block.icon.Icon;
import net.minecraft.common.block.Block;
import net.minecraft.common.block.Blocks;
import net.minecraft.common.item.block.ItemBlock;
import net.minecraft.common.item.ItemStack;

public class EngineItem extends ItemBlock {
	public EngineItem(Block block) {
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
		if (stack != null) {
			switch (stack.getItemDamage()) {
			case 0:
				return "item.buildcraft.engine.geared";
			case 1:
				return "item.buildcraft.engine.steam";
			}
		}
		return getItemName();
	}

	@Override
	public int getPlacedBlockMetadata(int damage) {
		return damage;
	}

	@Override
	public void populateCreativeInventory(ArrayList<ItemStack> stacks) {
		for (int i = 0; i <= 1; i++) {
			stacks.add(new ItemStack(this, 1, i));
		}
	}
}