package com.chocohead.buildcraft.api;

import java.util.Objects;

import net.minecraft.src.game.Direction.EnumDirection;
import net.minecraft.src.game.block.tileentity.TileEntity;
import net.minecraft.src.game.nbt.NBTTagCompound;

public class Position {
	public double x, y, z;
	public EnumDirection orientation;

	public Position(double x, double y, double z) {
		this(x, y, z, EnumDirection.UNKNOWN);
	}

	public Position(double x, double y, double z, EnumDirection orientation) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.orientation = orientation;
	}

	public Position(Position pos) {
		this(pos.x, pos.y, pos.z, pos.orientation);
	}

	public Position(NBTTagCompound nbt) {
		this(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
	}

	public Position(TileEntity tile) {
		this(tile.xCoord, tile.yCoord, tile.zCoord);
	}

	public void set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void set(double x, double y, double z, EnumDirection direction) {
		set(x, y, z);
		orientation = direction;
	}

	public void moveRight(double step) {
		switch (orientation) {
		case SOUTH:
			x -= step;
			break;
		case NORTH:
			x += step;
			break;
		case EAST:
			z += step;
			break;
		case WEST:
			z -= step;
			break;
		default:
			break;
		}
	}

	public void moveLeft(double step) {
		moveRight(-step);
	}

	public void moveForwards(double step) {
		switch (orientation) {
		case UP:
			y += step;
			break;
		case DOWN:
			y -= step;
			break;
		case SOUTH:
			z += step;
			break;
		case NORTH:
			z -= step;	
			break;
		case EAST:
			x += step;
			break;
		case WEST:
			x -= step;
			break;
		case UNKNOWN:
			break;
		}
	}	

	public void moveBackwards(double step) {
		moveForwards(-step);
	}

	public void moveUp(double step) {
		switch (orientation) {
		case SOUTH:
		case NORTH:
		case EAST:
		case WEST:
			y += step;
			break;
		default:
			break;
		}
	}

	public void moveDown(double step) {
		moveUp(-step);
	}

	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setDouble("x", x);
		nbt.setDouble("y", y);
		nbt.setDouble("z", z);
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, z, orientation);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || !(obj instanceof Position)) return false;
		Position that = (Position) obj;
		return x == that.x && y == that.y && z == that.z && orientation == that.orientation;
	}

	@Override
	public String toString () {
		return '{' + x + ", " + y + ", " + z + '}';
	}

	public Position min(Position pos) {
		return new Position(Math.min(x, pos.x), Math.min(y, pos.y), Math.min(z, pos.z), orientation);
	}

	public Position max(Position pos) {
		return new Position(Math.max(x, pos.x), Math.max(y, pos.y), Math.max(z, pos.z), orientation);
	}
}