package com.chocohead.buildcraft.pipes;

import net.minecraft.common.util.Direction.EnumDirection;
import net.minecraft.common.block.tileentity.TileEntity;
import net.minecraft.common.world.World;
import com.mojang.nbt.CompoundTag;

import com.chocohead.buildcraft.blocks.PipeTileEntity;

public abstract class PipeLike {
	public int xCoord;
	public int yCoord;
	public int zCoord;
	public World world;
	public PipeTileEntity<?> container;

	public void setPosition(int xCoord, int yCoord, int zCoord) {
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.zCoord = zCoord;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public void setTile(PipeTileEntity<?> tile) {
		this.container = tile;
	}

	public void writeToNBT(CompoundTag nbt) {
	}

	public void readFromNBT(CompoundTag nbt) {
	}

	public void initialize() {
	}

	public void onBlockPlaced() {
	}

	public void onNeighborBlockChange() {
	}

	public boolean isPipeConnected(TileEntity tile) {
		return true;
	}

	public boolean inputOpen(EnumDirection from) {
		return true;
	}

	public boolean outputOpen(EnumDirection to) {
		return true;
	}
}