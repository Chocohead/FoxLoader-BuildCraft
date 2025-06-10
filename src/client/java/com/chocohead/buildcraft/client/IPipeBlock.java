package com.chocohead.buildcraft.client;

import net.minecraft.common.util.Direction.EnumDirection;
import net.minecraft.common.world.BlockAccess;

public interface IPipeBlock {
	void prepareTextureFor(BlockAccess world, int x, int y, int z, EnumDirection connection);
}