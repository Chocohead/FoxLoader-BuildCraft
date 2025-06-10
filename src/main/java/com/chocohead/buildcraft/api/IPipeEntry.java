package com.chocohead.buildcraft.api;

import net.minecraft.common.util.Direction.EnumDirection;

/**
 * Interface used to put objects into pipes, implemented by pipe tile entities.
 */
public interface IPipeEntry {
	void entityEntering(EntityPassiveItem item, EnumDirection orientation);

	boolean acceptItems();
}