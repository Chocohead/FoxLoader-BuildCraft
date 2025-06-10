package com.chocohead.buildcraft.pipes;

import net.minecraft.common.util.math.AxisAlignedBB;
import net.minecraft.common.block.icon.Icon;
import net.minecraft.common.block.icon.IconRegister;
import net.minecraft.common.util.Direction.EnumDirection;
import net.minecraft.common.entity.Entity;
import net.minecraft.common.entity.projectile.EntityThrownArrow;
import net.minecraft.common.entity.other.EntityItem;
import net.minecraft.common.entity.other.EntityMinecart;
import net.minecraft.common.item.ItemStack;

import java.util.Arrays;

import com.chocohead.buildcraft.Proxy;
import com.chocohead.buildcraft.Utils;
import com.chocohead.buildcraft.api.EntityPassiveItem;
import com.chocohead.buildcraft.api.IPowerReceptor;
import com.chocohead.buildcraft.api.Position;
import com.chocohead.buildcraft.api.PowerProvider;
import com.chocohead.buildcraft.mixins.EntityThrownArrowAccess;
import com.chocohead.buildcraft.pipes.logic.ObsidianPipeLogic;
import com.chocohead.buildcraft.pipes.transport.ItemPipeTransport;

public class ObsidianItemPipe extends Pipe<ItemPipeTransport> implements IPowerReceptor {
	public static final String ID = "obsidianItem";
	private static Icon texture;
	protected final PowerProvider power = new PowerProvider();
	private final int[] entitiesDropped = new int[32];
	private int entitiesDroppedIndex = 0;

	public ObsidianItemPipe() {
		super(new ItemPipeTransport(), new ObsidianPipeLogic(), ID);

		power.configure(1, 64, 1, 256);
		power.configurePowerPerdition(1, 1);
		Arrays.fill(entitiesDropped, -1);
	}

	@Override
	public void registerIcons(IconRegister register) {
		texture = register.registerIcon("buildcraft/obsidianPipe");
	}

	@Override
	public Icon getBlockTexture() {
		return texture;
	}

	@Override
	public String getName() {
		return "item.buildcraft.itemPipe.obsidian";
	}

	@Override
	public void onEntityCollidedWithBlock(Entity entity) {   
		super.onEntityCollidedWithBlock(entity);

		if (entity.isDead) {
			return;
		}

		if (canSuck(entity, 0)) {
			pullItemIntoPipe(entity, getSuckingOrientation(), 0);
		}
	}

	public boolean canSuck(Entity entity, int distance) {
		if (entity.isDead) {
			return false;
		} if (entity instanceof EntityItem) {
			EntityItem item = (EntityItem) entity;

			for (int i = 0; i < entitiesDropped.length; i++) {
				if (item.entityId == entitiesDropped[i]) {
					return false;
				}
			}

			return power.useEnergy(1, distance, false) >= distance;
		} else if (entity instanceof EntityThrownArrow) {
			return power.useEnergy(1, distance, false) >= distance;
		} else {
			return false;
		}
	}

	public EnumDirection getSuckingOrientation() {
		int connections = 0;
		Position connectedPos = null;

		for (EnumDirection direction : EnumDirection.VALID_DIRECTIONS) {
			Position pos = new Position(xCoord, yCoord, zCoord);
			pos.orientation = direction;
			pos.moveForwards(1);

			if (Utils.checkPipesConnections(world, (int) pos.x, (int) pos.y, (int) pos.z, xCoord, yCoord, zCoord)) {
				if (++connections == 1) {
					connectedPos = new Position(pos);
				}
			}
		}

		if (connections > 1 || connections == 0) {
			return EnumDirection.UNKNOWN;
		}
		return Utils.reverseDirection(connectedPos.orientation);
	}

	@Override
	public void doWork() {
		EnumDirection orientation = getSuckingOrientation();

		for (int distance = 1; distance < 5; distance++) {
			if (trySuck(orientation, distance)) {
				return;
			}
		}

		power.useEnergy(1, 1, true);
	}

	@SuppressWarnings("incomplete-switch")
	private AxisAlignedBB getSuckingBox(EnumDirection orientation, int distance) {		
		if (orientation == EnumDirection.UNKNOWN) return null;
		Position p1 = new Position(xCoord, yCoord, zCoord, orientation);
		Position p2 = new Position(xCoord, yCoord, zCoord, orientation);

		switch (orientation) {
		case EAST:
			p1.x += distance;
			p2.x += 1 + distance;
			break;
		case WEST:
			p1.x -= (distance - 1);
			p2.x -= distance;
			break;
		case UP:
		case DOWN:
			p1.x += distance + 1;
			p2.x -= distance;
			p1.z += distance + 1;
			p2.z -= distance;
			break;
		case SOUTH:
			p1.z += distance;
			p2.z += distance + 1;
			break;
		case NORTH:
			p1.z -= (distance - 1);
			p2.z -= distance;
			break;
		}

		switch (orientation) {
		case EAST:
		case WEST:
			p1.y += distance + 1;
			p2.y -= distance;
			p1.z += distance + 1;
			p2.z -= distance;
			break;
		case UP:
			p1.y += distance + 1;
			p2.y += distance;
			break;
		case DOWN:
			p1.y -= (distance - 1);
			p2.y -= distance;
			break;
		case SOUTH:
		case NORTH:
			p1.y += distance + 1;
			p2.y -= distance;
			p1.x += distance + 1;
			p2.x -= distance;
			break;
		}

		Position min = p1.min(p2);
		Position max = p1.max(p2);
		return AxisAlignedBB.getAABBPool().getAABB(min.x, min.y, min.z, max.x, max.y, max.z);	
	}

	private boolean trySuck(EnumDirection orientation, int distance) {
		AxisAlignedBB box = getSuckingBox(orientation, distance);
		if (box == null) {
			return false;
		}

		for (Entity entity : world.getEntitiesWithinAABB(Entity.class, box)) {
			if (canSuck(entity, distance)) {
				pullItemIntoPipe(entity, orientation, distance);
				return true;
			}

			if (distance == 1 && entity instanceof EntityMinecart) {
				EntityMinecart cart = (EntityMinecart) entity;

				if (!cart.isDead && cart.minecartType == 1) {
					for (int slot = 0; slot < cart.getSizeInventory(); slot++) {
						ItemStack stack = cart.getStackInSlot(slot);

						if (stack != null && stack.stackSize > 0) {
							stack = cart.decrStackSize(slot, 1);

							if (stack != null && power.useEnergy(1, 1, true) == 1) {
								EntityItem item = new EntityItem(world, cart.posX, cart.posY + 0.3F, cart.posZ, stack);
								item.delayBeforeCanPickup = 10;
								world.entityJoinedWorld(item);
								pullItemIntoPipe(item, orientation, 1);
								return true;
							}
						}
					}
				}
			}
		}

		return false;
	}

	public void pullItemIntoPipe(Entity entity, EnumDirection orientation, int distance) {
		if (world.isRemote) return;		

		if (orientation != EnumDirection.UNKNOWN) {
			world.playSoundAtEntity(
					entity,
					"random.pop",
					0.2F,
					((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1F) * 2F);

			ItemStack stack = null;
			if (entity instanceof EntityItem) {
				EntityItem item = (EntityItem) entity;
				Proxy.pickupItem(world, item, this.container);

				int energyUsed = power.useEnergy(distance, item.item.stackSize * distance, true);
				if (distance == 0 || energyUsed / distance == item.item.stackSize) {
					stack = item.item;
					Proxy.removeEntity(entity);
				} else {
					stack = item.item.splitStack(energyUsed / distance);
				}
			} else if (entity instanceof EntityThrownArrow) {
				power.useEnergy(distance, distance, true);
				stack = ((EntityThrownArrowAccess) entity).callGetArrowItemstack();
				Proxy.removeEntity(entity);
			}

			EntityPassiveItem passive = new EntityPassiveItem(world, xCoord + 0.5, yCoord + Utils.getPipeHeight(stack), zCoord + 0.5, stack);
			transport.entityEntering(passive, Utils.reverseDirection(orientation));
		}
	}

	@Override
	public void onDropped(EntityItem item) {		
		if (++entitiesDroppedIndex >= entitiesDropped.length) {
			entitiesDroppedIndex = 0;
		}

		entitiesDropped[entitiesDroppedIndex] = item.entityId;
	}

	@Override
	public PowerProvider getPowerProvider() {
		return power;
	}
}