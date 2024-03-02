package com.chocohead.buildcraft.entities;

import net.minecraft.src.game.entity.Entity;
import net.minecraft.src.game.level.DataWatcher;
import net.minecraft.src.game.level.World;
import net.minecraft.src.game.nbt.NBTTagCompound;

import com.chocohead.buildcraft.blocks.QuarryTileEntity;
import com.chocohead.buildcraft.entities.BlockEntity.Texture;

public class QuarryArmEntity extends Entity {
	private double sizeX, sizeZ;
	private BlockEntity xArm, yArm, zArm, head;

	public double targetX, targetY, targetZ;
	private double angle;
	public double headPosX, headPosY, headPosZ;
	public double speed = 0.03;
	private double baseY;

	private QuarryTileEntity listener;
	private boolean inProgressionXZ = false;
	private boolean inProgressionY = false;

	public QuarryArmEntity(World world) {
		super(world);
	}

	public QuarryArmEntity(World world, double x, double y, double z, double width, double height) {
		super(world);

		setPosition(x, y, z);

		motionX = 0;
		motionY = 0;
		motionZ = 0;
		prevPosX = x;
		prevPosY = y;
		prevPosZ = z;
		sizeX = height;
		sizeZ = width;
		noClip = true;
		baseY = y;

		headPosX = x;
		headPosY = y - 2;
		headPosZ = z;

		setTarget(headPosX, headPosY, headPosZ);
		inProgressionXZ = false;
		inProgressionY = false;

		xArm = new BlockEntity(world, x, y, z, width, 0.5, 0.5);
		xArm.texture = Texture.DrillArm;
		world.entityJoinedWorld(xArm);

		yArm = new BlockEntity(world, x, y, z, 0.5, 1, 0.5);
		yArm.texture = Texture.DrillArm;
		world.entityJoinedWorld(yArm);

		zArm = new BlockEntity(world, x, y, z, 0.5, 0.5, height);
		zArm.texture = Texture.DrillArm;
		world.entityJoinedWorld(zArm);

		head = new BlockEntity(world, x, y, z, 0.2, 1, 0.2);
		head.texture = Texture.DrillHead;
		world.entityJoinedWorld(head);
		head.shadowSize = 1F;

		updatePosition();
	}

	@Override
	protected void entityInit() {
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setDouble("sizeX", getSizeX());
		nbt.setDouble("sizeZ", sizeZ);

		nbt.setDouble("targetX", targetX);
		nbt.setDouble("targetY", targetY);
		nbt.setDouble("targetZ", targetZ);
		nbt.setDouble("angle", angle);

		nbt.setDouble("headPosX", headPosX);
		nbt.setDouble("headPosY", headPosY);
		nbt.setDouble("headPosZ", headPosZ);

		nbt.setDouble("baseY", baseY);
		nbt.setDouble("speed", speed);

		nbt.setBoolean("progressionXZ", inProgressionXZ);
		nbt.setBoolean("progressionY", inProgressionY);

		NBTTagCompound xArmStore = new NBTTagCompound();
		NBTTagCompound yArmStore = new NBTTagCompound();
		NBTTagCompound zArmStore = new NBTTagCompound();
		NBTTagCompound headStore = new NBTTagCompound();

		nbt.setTag("xArm", xArmStore);
		nbt.setTag("yArm", yArmStore);
		nbt.setTag("zArm", zArmStore);
		nbt.setTag("head", headStore);

		xArm.writeToNBT(xArmStore);
		yArm.writeToNBT(yArmStore);
		zArm.writeToNBT(zArmStore);
		head.writeToNBT(headStore);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		sizeX = nbt.getDouble("sizeX");
		sizeZ = nbt.getDouble("sizeZ");

		targetX = nbt.getDouble("targetX");
		targetY = nbt.getDouble("targetY");
		targetZ = nbt.getDouble("targetZ");
		angle = nbt.getDouble("angle");

		headPosX = nbt.getDouble("headPosX");
		headPosY = nbt.getDouble("headPosY");
		headPosZ = nbt.getDouble("headPosZ");

		baseY = nbt.getDouble("baseY");
		speed = nbt.getDouble("speed");

		inProgressionXZ = nbt.getBoolean("progressionXZ");
		inProgressionY = nbt.getBoolean("progressionY");

		NBTTagCompound xArmStore = nbt.getCompoundTag("xArm");
		NBTTagCompound yArmStore = nbt.getCompoundTag("yArm");
		NBTTagCompound zArmStore = nbt.getCompoundTag("zArm");
		NBTTagCompound headStore = nbt.getCompoundTag("head");

		xArm = new BlockEntity(worldObj);
		yArm = new BlockEntity(worldObj);
		zArm = new BlockEntity(worldObj);
		head = new BlockEntity(worldObj);

		xArm.texture = Texture.DrillArm;
		yArm.texture = Texture.DrillArm;
		zArm.texture = Texture.DrillArm;
		head.texture = Texture.DrillHead;

		xArm.readFromNBT(xArmStore);
		yArm.readFromNBT(yArmStore);
		zArm.readFromNBT(zArmStore);
		head.readFromNBT(headStore);
	}

	@Override
	public void setWorld(World world) {
		super.setWorld(world);

		rand = world.rand;
        dataWatcher = new DataWatcher();
        dataWatcher.addObject(0, (byte) 0);
	}

	public void joinToWorld(World world) {
		setWorld(world);
		xArm.setWorld(world);
		yArm.setWorld(world);
		zArm.setWorld(world);
		head.setWorld(world);

		world.entityJoinedWorld(this);
		world.entityJoinedWorld(xArm);
		world.entityJoinedWorld(yArm);
		world.entityJoinedWorld(zArm);
		world.entityJoinedWorld(head);
	}

	public void setTarget(double x, double y, double z) {
		targetX = x;
		targetY = y;
		targetZ = z;

		double dX = targetX - headPosX;
		double dZ = targetZ - headPosZ;
		angle = Math.atan2(dZ, dX);

		inProgressionXZ = true;
		inProgressionY = true;
	}

	public double[] getTarget() {
		return new double[] {targetX, targetY, targetZ};
	}

	public void setListener(QuarryTileEntity listener) {
		this.listener = listener;
	}

	@Override
	public void onUpdate() {
		if (speed > 0) {
			doMove(speed);
		}
	}

	public void doMove(double instantSpeed) {
		super.onUpdate();

		if (inProgressionXZ) {
			if (Math.abs(targetX - headPosX) < instantSpeed * 2
					&& Math.abs(targetZ - headPosZ) < instantSpeed * 2) {
				headPosX = targetX;
				headPosZ = targetZ;

				inProgressionXZ = false;

				if (listener != null && !inProgressionY) {
					listener.positionReached(this);
				}
			} else {
				headPosX += Math.cos(angle) * instantSpeed;
				headPosZ += Math.sin(angle) * instantSpeed;
			}
		}

		if (inProgressionY) {
			if (Math.abs(targetY - headPosY) < instantSpeed * 2) {
				headPosY = targetY;

				inProgressionY = false;

				if (listener != null && !inProgressionXZ) {
					listener.positionReached(this);
				}
			} else {
				if (targetY > headPosY) {
					headPosY += instantSpeed / 2;
				} else {
					headPosY -= instantSpeed / 2;
				}
			}

		}
		
		updatePosition();
	}
	
	public void updatePosition() {
		xArm.setPosition(xArm.posX, xArm.posY, headPosZ + 0.25);
		yArm.ySize = baseY - headPosY - 1;
		yArm.setPosition(headPosX + 0.25, headPosY + 1, headPosZ + 0.25);
		zArm.setPosition(headPosX + 0.25, zArm.posY, zArm.posZ);
		head.setPosition(headPosX + 0.4, headPosY, headPosZ + 0.4);
	}

	@Override
	public void setEntityDead() {
		xArm.setEntityDead();
		yArm.setEntityDead();
		zArm.setEntityDead();
		head.setEntityDead();
		super.setEntityDead();
	}

	public double[] getHeadPosition() {
		return new double[] {headPosX, headPosY, headPosZ};
	}

	public void setHeadPosition(double x, double y, double z) {
		headPosX = x;
		headPosY = y;
		headPosZ = z;
	}

	public double getSizeX() {
		return sizeX;
	}
}