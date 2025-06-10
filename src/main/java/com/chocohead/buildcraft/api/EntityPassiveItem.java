package com.chocohead.buildcraft.api;

import net.minecraft.common.util.Direction.EnumDirection;
import net.minecraft.common.block.tileentity.TileEntity;
import net.minecraft.common.entity.other.EntityItem;
import net.minecraft.common.item.ItemStack;
import net.minecraft.common.world.World;
import com.mojang.nbt.CompoundTag;

public class EntityPassiveItem {
	public final SafeTimeTracker synchroTracker = new SafeTimeTracker();
	private static int maxId = 0;

	protected final World world;
	public double posX, posY, posZ;
	public int entityId;
	public float speed = 0.01F;
	public ItemStack item;	
	public TileEntity container;
	public int deterministicRandomization = 0;

	public EntityPassiveItem(World world) {
		this.world = world;
		entityId = maxId;

		if (++maxId >= Integer.MAX_VALUE) {
			maxId = 0;
		}
	}

	public EntityPassiveItem(World world, double x, double y, double z) {
		this(world);
		posX = x;
		posY = y;
		posZ = z;
	}

	public void setPosition(double x, double y, double z) {
		posX = x;
		posY = y;
		posZ = z;
	}

	public EntityPassiveItem(World world, double x, double y, double z, ItemStack stack) {
		this(world, x, y, z);
		this.item = stack.copy();
	}

	public void readFromNBT(CompoundTag nbt) {
		posX = nbt.getDouble("x");
		posY = nbt.getDouble("y");
		posZ = nbt.getDouble("z");
		speed = nbt.getFloat("speed");
		item = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("item"));
	}

	public void writeToNBT(CompoundTag nbt) {
		nbt.setDouble("x", posX);
		nbt.setDouble("y", posY);
		nbt.setDouble("z", posZ);
		nbt.setFloat("speed", speed);
		CompoundTag item = new CompoundTag();
		this.item.writeToNBT(item);
		nbt.setCompoundTag("item", item);
	}

	public EntityItem toEntityItem(World world, EnumDirection dir) {
		if (!world.isRemote) {
			Position motion = new Position(0, 0, 0, dir);
			motion.moveForwards(0.1 + speed * 2F);

			EntityItem entity = new EntityItem(world, posX, posY, posZ, item);

			float amp = world.rand.nextFloat() * 0.04F - 0.02F;
			entity.motionX = world.rand.nextGaussian() * amp + motion.x;
			entity.motionY = world.rand.nextGaussian() * amp + motion.y;
			entity.motionZ = world.rand.nextGaussian() * amp + motion.z;
			world.entityJoinedWorld(entity);

			entity.delayBeforeCanPickup = 20;
			return entity;
		} else {
			return null;
		}
	}
}