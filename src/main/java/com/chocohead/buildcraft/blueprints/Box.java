package com.chocohead.buildcraft.blueprints;

import net.minecraft.src.game.level.World;
import net.minecraft.src.game.nbt.NBTTagCompound;

import com.chocohead.buildcraft.LaserKind;
import com.chocohead.buildcraft.Proxy;
import com.chocohead.buildcraft.Utils;
import com.chocohead.buildcraft.api.IAreaProvider;
import com.chocohead.buildcraft.api.Position;
import com.chocohead.buildcraft.entities.BlockEntity;

public class Box {
	public int xMin, yMin, zMin, xMax, yMax, zMax;
	private BlockEntity lasers[];

	public Box() {
		reset();
	}

	public void reset() {
		xMin = Integer.MAX_VALUE;
		yMin = Integer.MAX_VALUE;
		zMin = Integer.MAX_VALUE;
		xMax = Integer.MAX_VALUE;
		yMax = Integer.MAX_VALUE;
		zMax = Integer.MAX_VALUE;
	}

	public boolean isInitialized() {
		return xMin != Integer.MAX_VALUE;
	}

	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("xMin", xMin);
		nbt.setInteger("yMin", yMin);
		nbt.setInteger("zMin", zMin);
		nbt.setInteger("xMax", xMax);
		nbt.setInteger("yMax", yMax);
		nbt.setInteger("zMax", zMax);
	}

	public void initialize(NBTTagCompound nbt) {
		xMin = nbt.getInteger("xMin");
		yMin = nbt.getInteger("yMin");
		zMin = nbt.getInteger("zMin");
		xMax = nbt.getInteger("xMax");
		yMax = nbt.getInteger("yMax");
		zMax = nbt.getInteger("zMax");
	}

	public void initialize(IAreaProvider area) {
		initialize(area.xMin(), area.yMin(), area.zMin(), area.xMax(), area.yMax(), area.zMax());
	}

	public void initialize(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax) {
		this.xMin = xMin;
		this.yMin = yMin;
		this.zMin = zMin;
		this.xMax = xMax;
		this.yMax = yMax;
		this.zMax = zMax;
	}

	public Position p1() {
		return new Position(xMin, yMin, zMin);
	}
	
	public Position p2() {
		return new Position(xMax, yMax, zMax);
	}

	public void createLasers(World world, LaserKind kind) {
		if (lasers == null) {
			lasers = Utils.createLaserBox(world, xMin, yMin, zMin, xMax, yMax, zMax, kind);
		}
	}

	public void deleteLasers() {
		if (lasers != null) {
			for (BlockEntity laser : lasers) {
				Proxy.removeEntity(laser);
			}

			lasers = null;
		}
	}

	public int sizeX() {
		return xMax - xMin + 1;
	}

	public int sizeY() {
		return yMax - yMin + 1;
	}

	public int sizeZ() {
		return zMax - zMin + 1;
	}
}