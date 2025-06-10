package com.chocohead.buildcraft.api;

import net.minecraft.common.world.BlockAccess;

public interface IPipeConnection {
	boolean isPipeConnected(BlockAccess world, int x1, int y1, int z1, int x2, int y2, int z2);
}