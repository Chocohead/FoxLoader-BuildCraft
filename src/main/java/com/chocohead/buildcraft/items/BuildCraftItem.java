package com.chocohead.buildcraft.items;

import net.minecraft.common.block.icon.IconRegister;
import net.minecraft.common.item.Item;

public class BuildCraftItem extends Item {
	public BuildCraftItem(String id) {
		super(id);
	}

	@Override
	public void registerIcons(IconRegister register) {
		itemIcon = register.registerIcon("buildcraft/" + itemName.replace("item.buildcraft.", ""));
	}
}