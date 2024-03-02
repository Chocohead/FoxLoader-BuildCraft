package com.chocohead.buildcraft.pipes;

import net.minecraft.src.game.Direction.EnumDirection;
import net.minecraft.src.game.block.tileentity.TileEntity;
import net.minecraft.src.game.level.World;
import net.minecraft.src.game.nbt.NBTTagCompound;

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

	public void writeToNBT(NBTTagCompound nbt) {
	}

	public void readFromNBT(NBTTagCompound nbt) {
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