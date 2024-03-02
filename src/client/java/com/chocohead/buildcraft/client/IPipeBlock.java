package com.chocohead.buildcraft.client;

import net.minecraft.src.game.Direction.EnumDirection;
import net.minecraft.src.game.level.IBlockAccess;

public interface IPipeBlock {
	void prepareTextureFor(IBlockAccess world, int x, int y, int z, EnumDirection connection);
}