package com.chocohead.buildcraft.pipes.transport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.common.entity.inventory.IInventory;
import net.minecraft.common.util.Direction.EnumDirection;
import net.minecraft.common.block.tileentity.TileEntity;
import net.minecraft.common.entity.other.EntityItem;
import net.minecraft.common.world.World;
import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.ListTag;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import com.chocohead.buildcraft.StackUtil;
import com.chocohead.buildcraft.Utils;
import com.chocohead.buildcraft.api.EntityPassiveItem;
import com.chocohead.buildcraft.api.IPipeEntry;
import com.chocohead.buildcraft.api.Position;
import com.chocohead.buildcraft.blocks.PipeTileEntity;
import com.chocohead.buildcraft.blocks.QuarryTileEntity;
import com.chocohead.buildcraft.blocks.TransportPipeTileEntity;

public class ItemPipeTransport extends PipeTransport implements IPipeEntry, SpecialItemTransportPipe {
	public class EntityData {
		public final EntityPassiveItem item;
		public EnumDirection orientation;
		boolean toCenter = true;

		public EntityData(EntityPassiveItem item, EnumDirection orientation) {
			this.item = item;
			this.orientation = orientation;
		}
	}
	private final Int2ObjectMap<EntityData> travellingEntities = new Int2ObjectOpenHashMap<>();
	private final List<EntityData> entitiesToLoad = new ArrayList<>();

	public Iterable<EntityData> getTravellingEntities() {
		return travellingEntities.values();
	}

	public EntityData[] cloneTravellingEntities() {
		EntityData[] out = new EntityData[travellingEntities.size()];
		int i = 0;
		for (EntityData data : getTravellingEntities()) {
			out[i++] = data;
		}
		return out;
	}

	@Override
	public void writeToNBT(CompoundTag nbt) {
		ListTag<CompoundTag> list = new ListTag<>();

		for (EntityData data : getTravellingEntities()) {
			CompoundTag item = new CompoundTag();
			list.setTag(item);
			data.item.writeToNBT(item);
			item.setBoolean("toCenter", data.toCenter);
			item.setInteger("orientation", data.orientation.ordinal());
		}

		nbt.setTag("travellingEntities", list);
	}

	@Override
	public void readFromNBT(CompoundTag nbt) {
		ListTag<CompoundTag> list = nbt.getTagList("travellingEntities");

		for (CompoundTag item : list) {
			EntityPassiveItem entity = new EntityPassiveItem(world);
			entity.readFromNBT(item);
			entity.container = container;

			EntityData data = new EntityData(entity, EnumDirection.VALID_DIRECTIONS[item.getInteger("orientation")]);
			data.toCenter = item.getBoolean("toCenter"); 
			entitiesToLoad.add(data);
		}
	}

	@Override
	public boolean isPipeConnected(TileEntity tile) {
		return tile instanceof TransportPipeTileEntity || tile instanceof IPipeEntry || tile instanceof IInventory || tile instanceof QuarryTileEntity;
	}

	@Override
	public boolean acceptItems() {
		return true;
	}

	@Override
	public void entityEntering(EntityPassiveItem item, EnumDirection orientation) {
		adjustSpeed(item);			

		if (!travellingEntities.containsKey(item.entityId)) {
			travellingEntities.put(item.entityId, new EntityData(item, orientation));

			item.container = container;
		}

		if (orientation != EnumDirection.UP && orientation != EnumDirection.DOWN) {
			item.setPosition(item.posX, yCoord + Utils.getPipeHeight(item.item), item.posZ);
		}

		if (container.getPipe() instanceof SpecialItemTransportPipe) {
			((SpecialItemTransportPipe) container.getPipe()).entityEntered(item, orientation);
		}

		if (!world.isRemote) {
			if (item.synchroTracker.markTimeIfDelay(world, 20)) {
				/*/ FIXME: what about the other items???
				CoreProxy.sendToPlayers(createItemPacket(item, orientation),
						xCoord, yCoord, zCoord, 50,
						mod_BuildCraftTransport.instance);*/
			}
		}
	}

	@Override
	public void adjustSpeed(EntityPassiveItem item) {
		if (container.getPipe() instanceof SpecialItemTransportPipe) {
			((SpecialItemTransportPipe) container.getPipe()).adjustSpeed(item);
		} else {
			SpecialItemTransportPipe.super.adjustSpeed(item);
		}
	}

	protected List<EnumDirection> getPossibleMovements(Position pos, EntityPassiveItem item) {
		List<EnumDirection> movements = new ArrayList<>();
		EnumDirection entry = Utils.reverseDirection(pos.orientation);

		for (EnumDirection direction : EnumDirection.VALID_DIRECTIONS) {
			if (direction == entry) continue; //Don't bounce back
			if (container.getPipe().outputOpen(direction)) {
				Position newPos = new Position(pos);
				newPos.orientation = direction;
				newPos.moveForwards(1);

				if (canReceivePipeObjects(newPos, item)) {
					movements.add(newPos.orientation);
				}
			}
		}

		return filterPossibleMovements(movements, pos, item);
	}

	protected boolean canReceivePipeObjects(Position pos, EntityPassiveItem item) {
		if (!Utils.checkPipesConnections(world, (int) pos.x, (int) pos.y, (int) pos.z, xCoord, yCoord, zCoord)) {
			return false;
		}

		TileEntity entity = world.getBlockTileEntity((int) pos.x, (int) pos.y, (int) pos.z);
		if (entity instanceof IPipeEntry) {
			return true;
		} else if (entity instanceof PipeTileEntity<?>) {
			PipeTileEntity<?> pipe = (PipeTileEntity<?>) entity;

			return pipe.getPipe().transport instanceof ItemPipeTransport;
		} else if (entity instanceof IInventory) {					
			if (StackUtil.checkAvailableSlot((IInventory) entity, item.item, false, Utils.reverseDirection(pos.orientation))) {
				return true;
			}
		}

		return false;
	}

	@Override
	public List<EnumDirection> filterPossibleMovements(List<EnumDirection> possibleOrientations, Position pos, EntityPassiveItem item) {
		if (container.getPipe() instanceof SpecialItemTransportPipe) {
			return ((SpecialItemTransportPipe) container.getPipe()).filterPossibleMovements(possibleOrientations, pos, item);
		} else {
			return SpecialItemTransportPipe.super.filterPossibleMovements(possibleOrientations, pos, item);
		}
	}

	public EnumDirection resolveDestination(EntityData data) {
		List<EnumDirection> listOfPossibleMovements = getPossibleMovements(new Position(xCoord, yCoord, zCoord, data.orientation), data.item);

		if (listOfPossibleMovements.isEmpty()) {
			return EnumDirection.UNKNOWN;
		} else {
			int i;
			if (world.isRemote || world.getClass() != World.class) {
				i = Math.abs(data.item.entityId + xCoord + yCoord + zCoord + data.item.deterministicRandomization) % listOfPossibleMovements.size();
			} else {
				i = world.rand.nextInt(listOfPossibleMovements.size());
			}

			return listOfPossibleMovements.get(i);
		}
	}

	@Override
	public void updateEntity() {
		for (EntityData data : entitiesToLoad) {
			travellingEntities.put(data.item.entityId, data);
		}
		entitiesToLoad.clear();

		Position motion = new Position(0, 0, 0);
		for (Iterator<? extends EntityData> it = travellingEntities.values().iterator(); it.hasNext();) {
			EntityData data = it.next();

			motion.set(data.item.posX, data.item.posY, data.item.posZ, data.orientation);
			motion.moveForwards(data.item.speed);
			data.item.setPosition(motion.x, motion.y, motion.z);

			if ((data.toCenter && middleReached(data)) || outOfBounds(data)) {
				data.toCenter = false;
				
				// Reajusting to the middle 

				data.item.setPosition(xCoord + 0.5, yCoord + Utils.getPipeHeight(data.item.item), zCoord + 0.5);

				EnumDirection nextOrientation = resolveDestination(data);
				if (nextOrientation == EnumDirection.UNKNOWN) {
					it.remove();

					EntityItem dropped = data.item.toEntityItem(world, data.orientation);
					if (dropped != null) {
						onDropped(dropped);
					}
				} else {
					data.orientation = nextOrientation;
				}
				
				
			} else if (!data.toCenter && endReached(data)) {
				it.remove();

				motion.set(xCoord, yCoord, zCoord, data.orientation);
				motion.moveForwards(1);
				TileEntity tile = world.getBlockTileEntity((int) motion.x, (int) motion.y, (int) motion.z);

				if (tile instanceof IPipeEntry) {
					((IPipeEntry) tile).entityEntering(data.item, data.orientation);
				} else if (tile instanceof PipeTileEntity<?> && ((PipeTileEntity<?>) tile).getPipe().transport instanceof ItemPipeTransport) {
					PipeTileEntity<?> pipe = (PipeTileEntity<?>) tile;

					((ItemPipeTransport) pipe.getPipe().transport).entityEntering(data.item, data.orientation);
				} else if (tile instanceof IInventory) {
					if (!world.isRemote) {
						if (StackUtil.checkAvailableSlot((IInventory) tile, data.item.item, true, Utils.reverseDirection(motion.orientation))
								&& data.item.item.stackSize == 0) {
							// Do nothing, we're adding the object to the world	
						} else {
							EntityItem dropped = data.item.toEntityItem(world, data.orientation);

							if (dropped != null) {
								onDropped(dropped);
							}
						}
					}
				} else {
					EntityItem dropped = data.item.toEntityItem(world, data.orientation);

					if (dropped != null) {
						onDropped(dropped);
					}
				}
			}
		}	
	}

	protected void onDropped(EntityItem item) {
		container.getPipe().onDropped(item);
	}

	protected boolean middleReached(EntityData entity) {
		float middleLimit = entity.item.speed * 1.01F;
		return (Math.abs(xCoord + 0.5 - entity.item.posX) < middleLimit
				&& Math.abs(yCoord + Utils.getPipeHeight(entity.item.item) - entity.item.posY) < middleLimit
				&& Math.abs(zCoord + 0.5 - entity.item.posZ) < middleLimit);
	}

	protected boolean endReached(EntityData entity) {
		return entity.item.posX > xCoord + 1
			|| entity.item.posX < xCoord
			|| entity.item.posY > yCoord + 1
			|| entity.item.posY < yCoord
			|| entity.item.posZ > zCoord + 1
			|| entity.item.posZ < zCoord;
	}

	protected boolean outOfBounds(EntityData entity) {
		return entity.item.posX > xCoord + 2
			|| entity.item.posX < xCoord - 1
			|| entity.item.posY > yCoord + 2
			|| entity.item.posY < yCoord - 1
			|| entity.item.posZ > zCoord + 2
			|| entity.item.posZ < zCoord - 1;
	}

	public void destroy() {
		for (EntityData data : getTravellingEntities()) {
			data.item.toEntityItem(world, data.orientation);
		}

		travellingEntities.clear();
	}
}