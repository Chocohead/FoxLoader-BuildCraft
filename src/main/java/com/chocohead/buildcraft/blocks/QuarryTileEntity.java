package com.chocohead.buildcraft.blocks;

import net.minecraft.common.util.Direction.EnumDirection;
import net.minecraft.common.block.Block;
import net.minecraft.common.block.Blocks;
import net.minecraft.common.block.tileentity.TileEntity;
import net.minecraft.common.entity.other.EntityItem;
import net.minecraft.common.item.ItemStack;
import com.mojang.nbt.CompoundTag;

import com.chocohead.buildcraft.BuildCraft;
import com.chocohead.buildcraft.LaserKind;
import com.chocohead.buildcraft.StackUtil;
import com.chocohead.buildcraft.Utils;
import com.chocohead.buildcraft.api.IAreaProvider;
import com.chocohead.buildcraft.api.IPowerReceptor;
import com.chocohead.buildcraft.api.PowerProvider;
import com.chocohead.buildcraft.blueprints.BlockContents;
import com.chocohead.buildcraft.blueprints.BluePrint;
import com.chocohead.buildcraft.blueprints.BluePrintBuilder;
import com.chocohead.buildcraft.blueprints.Box;
import com.chocohead.buildcraft.entities.QuarryArmEntity;

public class QuarryTileEntity extends TileEntity implements IPowerReceptor {
	private static final int MAX_ENERGY = 7000;
	public final PowerProvider power = new PowerProvider();

	private BluePrintBuilder bluePrintBuilder;
	private BlockContents nextBlockForBluePrint;
	private boolean initialised;
	private boolean isDigging;

	private final Box box = new Box();
	private boolean inProcess = false;

	private boolean loadArm = false;
	private QuarryArmEntity arm;
	private int targetX, targetY, targetZ;

	public QuarryTileEntity() {
		power.configure(25, 25, 25, MAX_ENERGY);
	}

	@Override
	public void writeToNBT(CompoundTag nbt) {
		super.writeToNBT(nbt);		

		CompoundTag power = new CompoundTag();
		this.power.writeToNBT(power);
		nbt.setCompoundTag("power", power);
		nbt.setInteger("targetX", targetX);
		nbt.setInteger("targetY", targetY);
		nbt.setInteger("targetZ", targetZ);
		nbt.setBoolean("hasArm", arm != null);

		if (arm != null) {
			CompoundTag armStore = new CompoundTag();
			nbt.setTag("arm", armStore);
			arm.writeToNBT(armStore);
		}

		CompoundTag boxTag = new CompoundTag();
		box.writeToNBT(boxTag);
		nbt.setTag("box", boxTag);
	}

	@Override
	public void readFromNBT(CompoundTag nbt) {
		super.readFromNBT(nbt);

		power.readFromNBT(nbt.getCompoundTag("power"));
		box.initialize(nbt.getCompoundTag("box"));

		targetX = nbt.getInteger("targetX");
		targetY = nbt.getInteger("targetY");
		targetZ = nbt.getInteger("targetZ");

		if (nbt.getBoolean("hasArm")) {
			CompoundTag armStore = nbt.getCompoundTag("arm");
			arm = new QuarryArmEntity(worldObj);
			arm.readFromNBT(armStore);
			arm.setListener(this);

			loadArm = true;
		}
	}

	protected void createUtilsIfNeeded() {
		if (!box.isInitialized() && worldObj.isRemote) {
			return;
		}

		if (bluePrintBuilder == null) {
			if (!box.isInitialized()) {
				setBoundaries();
			}

			initializeBluePrintBuilder();
		}		

		nextBlockForBluePrint = bluePrintBuilder.findNextBlock(worldObj);

		if (bluePrintBuilder.isDone()) {
			box.deleteLasers();

			if (arm == null) {
				createArm();
			}

			if (loadArm) {
				arm.joinToWorld(worldObj);
				loadArm = false;

				if (findTarget(false)) {
					isDigging = true;
				}
			}
		} else {
			box.createLasers(worldObj, LaserKind.Stripes);
			isDigging = true;
		}
	}

	private void setBoundaries() {
		IAreaProvider digArea = IAreaProvider.getNearby(worldObj, xCoord, yCoord, zCoord);

		if (digArea == null || digArea.xMax() - digArea.xMin() < 3 || digArea.zMax() - digArea.zMin() < 3) {
			int xMin = 0, zMin = 0;

			EnumDirection o = Utils.reverseDirection(EnumDirection.VALID_DIRECTIONS[worldObj.getBlockMetadata(xCoord, yCoord, zCoord)]);
			switch (o) {
			case EAST:
				xMin = xCoord + 1;
				zMin = zCoord - 4 - 1;
				break;
			case WEST:
				xMin = xCoord - 9 - 2;
				zMin = zCoord - 4 - 1;
				break;
			case SOUTH:
				xMin = xCoord - 4 - 1;
				zMin = zCoord + 1;
				break;
			case NORTH:
				xMin = xCoord - 4 - 1;
				zMin = zCoord - 9 - 2;
				break;
			default:
				System.err.println("Bad quarry direction " + o + " at " + xCoord + '/' + yCoord + '/' + zCoord);
				return;
			}

			box.initialize(xMin, yCoord, zMin, xMin + 10, yCoord + 4, zMin + 10);
		} else {
			box.initialize(digArea);

			if (digArea.yMax() - digArea.yMin() < 4) {
				box.yMax = box.yMin + 4;
			}

			digArea.removeFromWorld();
		}
	}

	private void initializeBluePrintBuilder() {
		BluePrint bluePrint = new BluePrint(box.sizeX(), box.sizeY(), box.sizeZ());	

		for (int x = 0; x < bluePrint.sizeX; x++) {
			for (int y = 0; y < bluePrint.sizeY; y++) {
				for (int z = 0; z < bluePrint.sizeZ; z++) {
					bluePrint.setBlockId(x, y, z, 0); //Set to air
				}
			}
		}

		for (int i = 0; i < 2; i++) {
			for (int x = 0; x < bluePrint.sizeX; x++) {
				bluePrint.setBlockId(x, i * (box.sizeY() - 1), 0, BuildCraft.frame.blockID);
				bluePrint.setBlockId(x, i * (box.sizeY() - 1), bluePrint.sizeZ - 1, BuildCraft.frame.blockID);
			}

			for (int z = 0; z < bluePrint.sizeZ; z++) {
				bluePrint.setBlockId(0, i * (box.sizeY() - 1), z, BuildCraft.frame.blockID);
				bluePrint.setBlockId(bluePrint.sizeX - 1, i * (box.sizeY() - 1), z, BuildCraft.frame.blockID);

			}
		}

		for (int y = 1; y < box.sizeY(); y++) {
			bluePrint.setBlockId(0, y, 0, BuildCraft.frame.blockID);
			bluePrint.setBlockId(0, y, bluePrint.sizeZ - 1, BuildCraft.frame.blockID);
			bluePrint.setBlockId(bluePrint.sizeX - 1, y, 0, BuildCraft.frame.blockID);
			bluePrint.setBlockId(bluePrint.sizeX - 1, y, bluePrint.sizeZ - 1, BuildCraft.frame.blockID);
		}

		bluePrintBuilder = new BluePrintBuilder(bluePrint, box.xMin, yCoord, box.zMin);
	}

	private void createArm() {
		arm = new QuarryArmEntity(worldObj, box.xMin + Utils.PIPE_MAX_POS,
				yCoord + bluePrintBuilder.bluePrint.sizeY - 1
						+ Utils.PIPE_MIN_POS, box.zMin + Utils.PIPE_MAX_POS,
				bluePrintBuilder.bluePrint.sizeX - 2 + Utils.PIPE_MIN_POS * 2,
				bluePrintBuilder.bluePrint.sizeZ - 2 + Utils.PIPE_MIN_POS * 2);

		arm.setListener(this);
		loadArm = true;
	}

	@Override
	public void updateEntity() {
		if (!initialised) {
			if (!worldObj.isRemote) {
				createUtilsIfNeeded();
			}

			//sendNetworkUpdate();
			initialised = true;
		}

		power.update(this);

		if (inProcess && arm != null) {
			arm.speed = 0;

			int energyToUse = 2 + power.energyStored / 1000;
			int energy = power.useEnergy(energyToUse, energyToUse, true);
			if (energy > 0) {
				arm.doMove(0.015 + (float) energy / 200F);
			}
		}
	}
	
	@Override
	public void doWork() {
		if (worldObj.isRemote) return;

		if (inProcess) {
			return;
		}

		if (!isDigging) {
			return;
		}

		createUtilsIfNeeded();
		
		if (bluePrintBuilder == null) {
			return;
		}

		if (bluePrintBuilder.isDone() && nextBlockForBluePrint != null) {
			// In this case, the Quarry has been broken. Repair it.
			bluePrintBuilder.reset();

			box.createLasers(worldObj, LaserKind.Stripes);
		}

		if (!bluePrintBuilder.isDone()) {
			// configuration for building phase
			power.configure(25, 25, 25, MAX_ENERGY);

			if (power.useEnergy(25, 25, true) != 25) {
				return;
			}

			//powerProvider.timeTracker.markTime(worldObj);
			BlockContents contents = bluePrintBuilder.findNextBlock(worldObj);
			int blockId = worldObj.getBlockId(contents.x, contents.y, contents.z);

			if (contents != null) {
				if (!Utils.isSoftBlock(blockId)) {
					// Do not drop items here, too power consuming
					worldObj.setBlockWithNotify(contents.x, contents.y, contents.z, 0);
				} else if (contents.blockId != 0) {
					worldObj.setBlockWithNotify(contents.x, contents.y, contents.z, contents.blockId);
				}
			}

			return;
		}

		// configuration for digging phase
		power.configure(30, 200, 50, MAX_ENERGY);

		if (!findTarget(true)) {
			arm.setTarget(box.xMin + arm.getSizeX() / 2, yCoord + 2, box.zMin + arm.getSizeX() / 2);

			isDigging = false;
		}

		inProcess = true;

		/*if (!worldObj.multiplayerWorld) {
			sendNetworkUpdate();
		}*/
	}

	public boolean findTarget(boolean doSet) {
		boolean[][] blockedColumns = new boolean[bluePrintBuilder.bluePrint.sizeX - 2][bluePrintBuilder.bluePrint.sizeZ - 2];
		
		for (int searchX = 0; searchX < bluePrintBuilder.bluePrint.sizeX - 2; searchX++) {
			for (int searchZ = 0; searchZ < bluePrintBuilder.bluePrint.sizeZ - 2; searchZ++) {
				blockedColumns[searchX][searchZ] = false;
			}
		}

		for (int searchY = yCoord + 3; searchY >= worldObj.lowestY; searchY--) {
			int startX, endX, incX;
			if (searchY % 2 == 0) {
				startX = 0;
				endX = bluePrintBuilder.bluePrint.sizeX - 2;
				incX = 1;
			} else {
				startX = bluePrintBuilder.bluePrint.sizeX - 3;
				endX = -1;
				incX = -1;
			}

			for (int searchX = startX; searchX != endX; searchX += incX) {
				int startZ, endZ, incZ;
				if (searchX % 2 == searchY % 2) {
					startZ = 0;
					endZ = bluePrintBuilder.bluePrint.sizeZ - 2;
					incZ = 1;
				} else {
					startZ = bluePrintBuilder.bluePrint.sizeZ - 3;
					endZ = -1;
					incZ = -1;
				}

				for (int searchZ = startZ; searchZ != endZ; searchZ += incZ) {
					if (!blockedColumns[searchX][searchZ]) {
						int bx = box.xMin + searchX + 1;
						int by = searchY;
						int bz = box.zMin + searchZ + 1;
						int blockID = worldObj.getBlockId(bx, by, bz);

						if (Utils.isUnbreakableBlock(blockID)) {
							blockedColumns[searchX][searchZ] = true;
						} else if (canDig(blockID)) {
							if (doSet) {
								arm.setTarget(bx, by + 1, bz);

								targetX = (int) arm.targetX;
								targetY = (int) arm.targetY;
								targetZ = (int) arm.targetZ;
							}

							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public void positionReached(QuarryArmEntity arm) {
		inProcess = false;

		if (worldObj.isRemote) return;

		int x = (int) targetX;
		int y = (int) targetY - 1;
		int z = (int) targetZ;
		int blockID = worldObj.getBlockId(x, y, z);

		if (canDig(blockID)) {
			//powerProvider.timeTracker.markTime(worldObj);

			ItemStack stack = getItemStackFromBlock(x, y, z);
			if (stack != null) {
				boolean added = false;

				// First, try to add to a nearby chest
				added = StackUtil.addToRandomInventory(this, EnumDirection.UNKNOWN, stack);

				if (!added || stack.stackSize > 0) {
					added = Utils.addToRandomPipe(this, EnumDirection.UNKNOWN, stack);
				}

				// Last, throw the object away

				if (!added) {
					float itemX = worldObj.rand.nextFloat() * 0.8F + 0.1F;
					float itemY = worldObj.rand.nextFloat() * 0.8F + 0.1F;
					float itemZ = worldObj.rand.nextFloat() * 0.8F + 0.1F;

					EntityItem entity = new EntityItem(worldObj, xCoord + itemX, yCoord + itemY + 0.5F, zCoord + itemZ, stack);
					float jitter = 0.05F;
					entity.motionX = (float) worldObj.rand.nextGaussian() * jitter;
					entity.motionY = (float) worldObj.rand.nextGaussian() * jitter + 1F;
					entity.motionZ = (float) worldObj.rand.nextGaussian() * jitter;
					worldObj.entityJoinedWorld(entity);
				}
			}

			worldObj.setBlockWithNotify((int) x, (int) y, (int) z, 0);
		}
	}

	private static boolean canDig(int blockID) {
		return !Utils.isUnbreakableBlock(blockID) && !Utils.isSoftBlock(blockID) && blockID != Blocks.SNOW_PILE.blockID;
	}

	private ItemStack getItemStackFromBlock(int x, int y, int z) {
		Block block = Blocks.BLOCKS_LIST[worldObj.getBlockId(x, y, z)];
		if (block == null) {
			return null;
		}

		int id = block.idDropped(worldObj.getBlockMetadata(x, y, z), worldObj.rand);
		int qty = block.quantityDropped(worldObj.rand);
		if (id <= 0 || qty == 0) {
			return null;
		} else {
			return new ItemStack(id, qty, block.getDamageValue(worldObj, x, y, z));
		}
	}

	public void destroy() {
		if (arm != null) {
			arm.setEntityDead();
		}

		box.deleteLasers();
	}

	@Override
	public PowerProvider getPowerProvider() {
		return power;
	}
}