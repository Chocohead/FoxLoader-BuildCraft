package com.chocohead.buildcraft.api;

import net.minecraft.src.game.Direction.EnumDirection;
import net.minecraft.src.game.block.tileentity.TileEntity;
import net.minecraft.src.game.level.World;

public interface IAreaProvider {
	int xMin();
	int yMin();
	int zMin();

	int xMax();
	int yMax();
	int zMax();

	default void removeFromWorld() {
	}

	static IAreaProvider getNearby(World world, int x, int y, int z) {
		for (EnumDirection direction : EnumDirection.VALID_DIRECTIONS) {
			TileEntity tile = world.getBlockTileEntity(x + direction.offsX, y + direction.offsY, z + direction.offsZ);
			if (tile instanceof IAreaProvider) return (IAreaProvider) tile;
		}

    	return null;
    }
}