package com.chocohead.buildcraft.items;

import com.fox2code.foxloader.registry.GameRegistry;

import net.minecraft.src.client.renderer.block.icon.IconRegister;
import net.minecraft.src.game.item.Item;

public class BuildCraftItem extends Item {
	public BuildCraftItem(int id) {
		super(id - GameRegistry.PARAM_ITEM_ID_DIFF);
	}

	@Override
	public void registerIcons(IconRegister register) {
		itemIcon = register.registerIcon("buildcraft/" + itemName.replace("item.buildcraft.", ""));
	}
}