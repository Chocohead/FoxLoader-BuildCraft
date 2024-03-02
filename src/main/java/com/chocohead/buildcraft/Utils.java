package com.chocohead.buildcraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.src.client.inventory.IInventory;
import net.minecraft.src.client.inventory.InventoryLargeChest;
import net.minecraft.src.game.Direction;
import net.minecraft.src.game.Direction.EnumDirection;
import net.minecraft.src.game.block.Block;
import net.minecraft.src.game.block.Material;
import net.minecraft.src.game.block.tileentity.TileEntity;
import net.minecraft.src.game.block.tileentity.TileEntityChest;
import net.minecraft.src.game.entity.other.EntityItem;
import net.minecraft.src.game.item.ItemStack;
import net.minecraft.src.game.level.IBlockAccess;
import net.minecraft.src.game.level.World;

import com.chocohead.buildcraft.api.EntityPassiveItem;
import com.chocohead.buildcraft.api.IPipeConnection;
import com.chocohead.buildcraft.api.IPipeEntry;
import com.chocohead.buildcraft.api.Position;
import com.chocohead.buildcraft.entities.BlockEntity;

public class Utils {
	public static final EnumDirection[] ALL_DIRECTIONS = EnumDirection.values();
	public static final EnumDirection[] HORIZONTALS = Arrays.stream(Direction.directions).map(EnumDirection::valueOf).toArray(EnumDirection[]::new);
	public static final float PIPE_MIN_POS = 0.25F;
	public static final float PIPE_MAX_POS = 0.75F;
	public static float normalPipeSpeed = 0.01F;

	public static float getPipeHeight(ItemStack item) {
		return PIPE_MIN_POS;
	}

	public static boolean checkPipesConnections(IBlockAccess world, int x1, int y1, int z1, int x2, int y2, int z2) {
		Block b1 = Block.blocksList[world.getBlockId(x1, y1, z1)];
		Block b2 = Block.blocksList[world.getBlockId(x2, y2, z2)];

		IPipeConnection pipe1 = b1 instanceof IPipeConnection ? (IPipeConnection) b1 : null;
		IPipeConnection pipe2 = b2 instanceof IPipeConnection ? (IPipeConnection) b2 : null;
		if (pipe1 == null && pipe2 == null) return false;

		if (pipe1 != null && !pipe1.isPipeConnected(world, x1, y1, z1, x2, y2, z2)) {
			return false;
		}

		if (pipe2 != null && !pipe2.isPipeConnected(world, x2, y2, z2, x1, y1, z1)) {
			return false;
		}

		return true;
	}

	public static boolean addToRandomPipe(TileEntity tile, EnumDirection from, ItemStack stack) {
		List<EnumDirection> possiblePipes = new ArrayList<>();

		for (EnumDirection direction : EnumDirection.VALID_DIRECTIONS) {
			if (Utils.reverseDirection(from) == direction) continue;

			Position pos = new Position(tile.xCoord, tile.yCoord, tile.zCoord, direction);
			pos.moveForwards(1);

			TileEntity pipeEntry = tile.worldObj.getBlockTileEntity((int) pos.x, (int) pos.y, (int) pos.z);
			if (pipeEntry instanceof IPipeEntry && ((IPipeEntry) pipeEntry).acceptItems()) {
				possiblePipes.add(direction);
			}
		}

		if (!possiblePipes.isEmpty()) {
			int choice = tile.worldObj.rand.nextInt(possiblePipes.size());

			Position entityPos = new Position(tile.xCoord, tile.yCoord, tile.zCoord, possiblePipes.get(choice));
			Position pipePos = new Position(tile.xCoord, tile.yCoord, tile.zCoord, possiblePipes.get(choice));

			entityPos.x += 0.5;
			entityPos.y += getPipeHeight(stack);
			entityPos.z += 0.5;
			entityPos.moveForwards(0.5);
			pipePos.moveForwards(1);

			IPipeEntry pipeEntry = (IPipeEntry) tile.worldObj.getBlockTileEntity((int) pipePos.x, (int) pipePos.y, (int) pipePos.z);
			EntityPassiveItem entity = new EntityPassiveItem(tile.worldObj, entityPos.x, entityPos.y, entityPos.z, stack);
			pipeEntry.entityEntering(entity, entityPos.orientation);
			stack.stackSize = 0;
			return true;
		} else {
			return false;
		}
	}

	public static IInventory getInventory(IInventory inv) {
		if (inv instanceof TileEntityChest) {
			TileEntityChest chest = (TileEntityChest) inv;

			for (EnumDirection direction : HORIZONTALS) {
				TileEntity tile = chest.worldObj.getBlockTileEntity(chest.xCoord + direction.offsX, chest.yCoord, chest.zCoord + direction.offsZ);

				if (tile instanceof TileEntityChest) {
					return new InventoryLargeChest(inv, (IInventory) tile);
				}
			} 
		}

		return inv;
	}

	public static void preDestroyBlock(World world, int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);

		if (tile instanceof IInventory && !world.multiplayerWorld) {
			dropItems(world, x, y, z, (IInventory) tile);
		}
	}

	private static void dropItems(World world, int x, int y, int z, IInventory inventory) {
		for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
			ItemStack stack = inventory.getStackInSlot(slot);

			if (stack != null && stack.stackSize > 0) {
				dropItems(world, x, y, z, stack.copy());
			}
		}
	}

	private static void dropItems(World world, int x, int y, int z, ItemStack stack) {
		float amp = 0.7F;
		double motX = (world.rand.nextFloat() * amp) + (1F - amp) * 0.5;
		double motY = (world.rand.nextFloat() * amp) + (1F - amp) * 0.5;
		double motZ = (world.rand.nextFloat() * amp) + (1F - amp) * 0.5;
		EntityItem item = new EntityItem(world, x + motX, y + motY, z + motZ, stack);
		item.delayBeforeCanPickup = 10;
		world.entityJoinedWorld(item);
	}

	public static EnumDirection reverseDirection(EnumDirection direction) {
		switch (direction) {
		case NORTH:
			return EnumDirection.SOUTH;
		case SOUTH:
			return EnumDirection.NORTH;
		case UP:
			return EnumDirection.DOWN;
		case DOWN:
			return EnumDirection.UP;
		case EAST:
			return EnumDirection.WEST;
		case WEST:
			return EnumDirection.EAST;
		default:
			return EnumDirection.UNKNOWN;
		}
	}

	public static EnumDirection getHortizontalDirection(Position pos1, Position pos2) {
		double Dx = pos1.x - pos2.x;
		double Dz = pos1.z - pos2.z;
		double angle = Math.toDegrees(Math.atan2(Dz, Dx)) + 180;

		if (angle < 45 || angle > 315) {
			return EnumDirection.EAST;
		} else if (angle < 135) {
			return EnumDirection.SOUTH;
		} else if (angle < 225) {
			return EnumDirection.WEST;
		} else {
			return EnumDirection.NORTH;
		}
	}

	public static boolean isSoftBlock(int blockID) {
		Block block = Block.blocksList[blockID];
		return block == null || block.blockMaterial == Material.air || blockID == Block.waterStill.blockID || blockID == Block.waterMoving.blockID;
	}

	public static boolean isUnbreakableBlock(int blockID) {
		Block block = Block.blocksList[blockID];
		return (block != null && block.getHardness() < 0) || blockID == Block.lavaStill.blockID || blockID == Block.lavaMoving.blockID;
	}

	public static BlockEntity[] createLaserBox(World world, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax, LaserKind kind) {
		BlockEntity lasers[] = new BlockEntity[12];
		Position[] corners = new Position [8];

		corners[0] = new Position(xMin, yMin, zMin);
		corners[1] = new Position(xMax, yMin, zMin);
		corners[2] = new Position(xMin, yMax, zMin);
		corners[3] = new Position(xMax, yMax, zMin);
		corners[4] = new Position(xMin, yMin, zMax);
		corners[5] = new Position(xMax, yMin, zMax);
		corners[6] = new Position(xMin, yMax, zMax);
		corners[7] = new Position(xMax, yMax, zMax);

		lasers[0] = Utils.createLaser(world, corners[0], corners[1], kind);
		lasers[1] = Utils.createLaser(world, corners[0], corners[2], kind);
		lasers[2] = Utils.createLaser(world, corners[2], corners[3], kind);
		lasers[3] = Utils.createLaser(world, corners[1], corners[3], kind);
		lasers[4] = Utils.createLaser(world, corners[4], corners[5], kind);
		lasers[5] = Utils.createLaser(world, corners[4], corners[6], kind);
		lasers[6] = Utils.createLaser(world, corners[5], corners[7], kind);
		lasers[7] = Utils.createLaser(world, corners[6], corners[7], kind);
		lasers[8] = Utils.createLaser(world, corners[0], corners[4], kind);
		lasers[9] = Utils.createLaser(world, corners[1], corners[5], kind);
		lasers[10] = Utils.createLaser(world, corners[2], corners[6], kind);
		lasers[11] = Utils.createLaser(world, corners[3], corners[7], kind);

		return lasers;
	}

	private static BlockEntity createLaser(World world, Position p1, Position p2, LaserKind kind) {
		if (p1.equals(p2)) {
			return null; //Can't make a laser onto itself
		}

		double xSize = p2.x - p1.x;
		double ySize = p2.y - p1.y;
		double zSize = p2.z - p1.z;

		double x = p1.x;
		double y = p1.y;
		double z = p1.z;

		if (xSize != 0) {
			x += 0.5;
			y += 0.45;
			z += 0.45;

			ySize = 0.10;
			zSize = 0.10;
		} else if (ySize != 0) {
			x += 0.45;
			y += 0.5;
			z += 0.45;

			xSize = 0.10;
			zSize = 0.10;
		} else if (zSize != 0) {
			x += 0.45;
			y += 0.45;
			z += 0.5;

			xSize = 0.10;
			ySize = 0.10;
		}

		BlockEntity block = new BlockEntity(world, x, y, z, xSize, ySize, zSize, kind.texture);
		world.entityJoinedWorld(block);
		return block;
	}
}