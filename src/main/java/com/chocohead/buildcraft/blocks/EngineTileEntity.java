package com.chocohead.buildcraft.blocks;

import net.minecraft.common.entity.inventory.IInventory;
import net.minecraft.common.util.Direction.EnumDirection;
import net.minecraft.common.block.tileentity.TileEntity;
import net.minecraft.common.entity.player.EntityPlayer;
import net.minecraft.common.item.ItemStack;
import com.mojang.nbt.CompoundTag;

import com.chocohead.buildcraft.api.IPowerReceptor;
import com.chocohead.buildcraft.api.Position;
import com.chocohead.buildcraft.api.PowerProvider;
import com.chocohead.buildcraft.engines.Engine;
import com.chocohead.buildcraft.engines.GearedEngine;
import com.chocohead.buildcraft.engines.SteamEngine;

public class EngineTileEntity extends TileEntity implements IPowerReceptor, IInventory {
	protected final PowerProvider power = new PowerProvider();
	protected Engine engine;
	protected ItemStack inventory;
	protected boolean wasPowered = false;
	private boolean initialized = false;
	//Server => Client syncing
	protected int progressPart = 0;
	protected float serverPistonSpeed = 0;

	public EngineTileEntity() {
	}

	public EngineTileEntity(int meta) {
		switch (meta) {
		case 0:
			engine = new GearedEngine(this);
			break;

		case 1:
			engine = new SteamEngine(this);
			break;
		}
	}

	@Override
	public void writeToNBT(CompoundTag nbt) {
		super.writeToNBT(nbt);

		nbt.setInteger("kind", worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
		engine.writeToNBT(nbt);

		if (inventory != null) {
			CompoundTag stack = new CompoundTag();
			inventory.writeToNBT(stack);
			nbt.setTag("inventory", stack);
		}
	}

	@Override
	public void readFromNBT(CompoundTag nbt) {
		super.readFromNBT(nbt);

		int kind = nbt.getInteger("kind");
		switch (kind) {
		case 0:
			engine = new GearedEngine(this);
			break;

		case 1:
			engine = new SteamEngine(this);
			break;

		default:
			return;
		}
		engine.readFromNBT(nbt);

		if (nbt.hasKey("inventory")) {
			CompoundTag stack = nbt.getCompoundTag("inventory");
			inventory = ItemStack.loadItemStackFromNBT(stack);
		}
	}

	private static boolean isPoweredTile(TileEntity tile) {
		return tile instanceof IPowerReceptor && ((IPowerReceptor) tile).getPowerProvider() != null;
	}

	@Override
	public void updateEntity() {
		if (!initialized) {
			if (!worldObj.isRemote) {
				power.configure(1, engine.maxEnergyReceived, 1, engine.maxEnergy);
			}
			initialized = true;
		}

		if (engine == null) {
			return;
		}

		if (worldObj.isRemote) {
			if (progressPart != 0) {
				engine.progress += serverPistonSpeed;
				
				if (engine.progress > 1) {
					progressPart = 0;
				}
			}
			
			return;
		}

		power.update(this);
		engine.update();

		boolean isPowered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);

		if (progressPart != 0) {
			engine.progress += engine.getPistonSpeed();

			if (engine.progress > 0.5 && progressPart == 1) {
				progressPart = 2;

				Position pos = new Position(xCoord, yCoord, zCoord, engine.orientation);
				pos.moveForwards(1);
				TileEntity tile = worldObj.getBlockTileEntity((int) pos.x, (int) pos.y, (int) pos.z);

				if (isPoweredTile(tile)) {					
					IPowerReceptor receptor = (IPowerReceptor) tile;

					int extracted = engine.extractEnergy(
							receptor.getPowerProvider().minEnergyReceived,
							receptor.getPowerProvider().maxEnergyReceived, true);

					if (extracted > 0) {
						receptor.getPowerProvider().receiveEnergy(extracted);
					}
				}
			} else if (engine.progress >= 1) {
				engine.progress = 0;
				progressPart = 0;
			}
		} else if (isPowered) {
			Position pos = new Position(xCoord, yCoord, zCoord, engine.orientation);
			pos.moveForwards(1);
			TileEntity tile = worldObj.getBlockTileEntity((int) pos.x, (int) pos.y, (int) pos.z);

			if (isPoweredTile(tile)) {
				IPowerReceptor receptor = (IPowerReceptor) tile;

				if (engine.extractEnergy(
						receptor.getPowerProvider().minEnergyReceived,
						receptor.getPowerProvider().maxEnergyReceived, false) > 0) {
					progressPart = 1;

					//sendNetworkUpdate();
				}
			}
		} else {
			/*/ If we're not in an active movement process, update the client
			// from time to time in order to e.g. display proper color.
			
			if (worldObj.getWorldTime() % 20 * 10 == 0) {
				sendNetworkUpdate();
			}*/
		}

		engine.burn();
	}

	@Override
	public void doWork() {
		if (!worldObj.isRemote) {
			engine.addEnergy((int) (power.useEnergy(1, engine.maxEnergyReceived, true) * 0.95F));
		}
	}

	public Engine getEngine() {
		return engine;
	}

	public void switchOrientation() {
		for (int current = engine.orientation.ordinal(), i = current + 1; i <= current + 6; i++) {
			EnumDirection facing = EnumDirection.VALID_DIRECTIONS[i % 6];
			Position pos = new Position(xCoord, yCoord, zCoord, facing);
			pos.moveForwards(1);

			TileEntity tile = worldObj.getBlockTileEntity((int) pos.x, (int) pos.y, (int) pos.z);
			if (isPoweredTile(tile)) {
				engine.orientation = facing;
				break;
			}
		}

		worldObj.markBlockNeedsUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void onInventoryChanged() {		
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return slot == 0 ? inventory : null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		if (slot != 0 || inventory == null) return null;

		ItemStack stack;
		if (inventory.stackSize <= amount) {
    		stack = inventory;
    		inventory = null;
    	} else {
    		stack = inventory.splitStack(amount);

    		if (inventory.stackSize == 0) {
    			inventory = null;
    		}	
    	} 

		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if (slot == 0) inventory = stack;		
	}

	@Override
	public String getInvName() {
		switch (worldObj.getBlockMetadata(xCoord, yCoord, zCoord)) {
		case 0:
			return "item.buildcraft.engine.geared.name";
		case 1:
			return "item.buildcraft.engine.steam.name";
		default:
			return "tile.buildcraft.engine.name";
		}
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) == this && player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) <= 64;
	}

	@Override
	public PowerProvider getPowerProvider() {
		return power;
	}
}