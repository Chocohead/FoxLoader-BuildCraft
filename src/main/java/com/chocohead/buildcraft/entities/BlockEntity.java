package com.chocohead.buildcraft.entities;

import net.minecraft.common.entity.Entity;
import net.minecraft.common.world.World;
import com.mojang.nbt.CompoundTag;

public class BlockEntity extends Entity {
	public enum Texture {
		Red("redLaser"),
		Blue("blueLaser"),
		Stripes("stripedLaser"),
		DrillArm("drillArm"),
		DrillHead("drillHead");
		public final String texture;

		private Texture(String texture) {
			this.texture = "buildcraft/" + texture;
		}
	}
	public double xSize, ySize, zSize;
	public Texture texture;
	public float shadowSize = 0;

	public BlockEntity(World world) {
		super(world);

		preventEntitySpawning = false;
		noClip = true;
		isImmuneToFire = true;
	}

	public BlockEntity(World world, double x, double y, double z, double xSize, double ySize, double zSize) {
		this(world);

		motionX = 0;
		motionY = 0;
		motionZ = 0;
		prevPosX = x;
		prevPosY = y;
		prevPosZ = z;
		this.xSize = xSize;
		this.ySize = ySize;
		this.zSize = zSize;	

		setPosition(x, y, z);
	}

	public BlockEntity(World world, double x, double y, double z, double xSize, double ySize, double zSize, Texture texture) {
		this(world, x, y, z, xSize, ySize, zSize);

		this.texture = texture;
	}

	@Override
	protected void entityInit() {
	}

	@Override
	protected void writeEntityToNBT(CompoundTag nbt) {
		nbt.setDouble("xSize", xSize);
		nbt.setDouble("ySize", ySize);
		nbt.setDouble("zSize", zSize);
		nbt.setFloat("shadow", shadowSize);
	}

	@Override
	protected void readEntityFromNBT(CompoundTag nbt) {
		xSize = nbt.getDouble("xSize");
		ySize = nbt.getDouble("ySize");
		zSize = nbt.getDouble("zSize");
		shadowSize = nbt.getFloat("shadow");
	}

	@Override
	public void moveEntity(double x, double y, double z) {
		setPosition(posX + x, posY + y, posZ + z);
	}

	@Override
	public void setPosition(double x, double y, double z) {
		posX = x;
		posY = y;
		posZ = z;

		boundingBox.minX = posX;
		boundingBox.minY = posY;
		boundingBox.minZ = posZ;

		boundingBox.maxX = posX + xSize;
		boundingBox.maxY = posY + ySize;
		boundingBox.maxZ = posZ + zSize;
	}

	@Override
	public boolean canBeCollidedWith() {
		return !isDead;
	}
}