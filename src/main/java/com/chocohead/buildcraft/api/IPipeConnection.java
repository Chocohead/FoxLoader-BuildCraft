package com.chocohead.buildcraft.api;

import net.minecraft.src.game.level.IBlockAccess;

public interface IPipeConnection {
	boolean isPipeConnected(IBlockAccess world, int x1, int y1, int z1, int x2, int y2, int z2);
}