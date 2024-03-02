package com.chocohead.buildcraft.api;

import net.minecraft.src.game.Direction.EnumDirection;

/**
 * Interface used to put objects into pipes, implemented by pipe tile entities.
 */
public interface IPipeEntry {
	void entityEntering(EntityPassiveItem item, EnumDirection orientation);

	boolean acceptItems();
}