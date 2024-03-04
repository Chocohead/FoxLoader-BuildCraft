package com.chocohead.buildcraft;

import net.minecraft.src.client.inventory.IInventory;
import net.minecraft.src.game.Direction.EnumDirection;
import net.minecraft.src.game.block.tileentity.TileEntity;
import net.minecraft.src.game.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import com.chocohead.buildcraft.api.ISpecialInventory;
import com.chocohead.buildcraft.api.Position;

public class StackUtil {
	public static boolean checkAvailableSlot(IInventory inventory, ItemStack items, boolean add, EnumDirection from) {
		if (inventory instanceof ISpecialInventory) {
			return ((ISpecialInventory) inventory).addItem(items, add, from);
		}

		boolean result = false;
		boolean success;
		do {
			result |= success = checkAvailableSlotOnce(inventory, items, add, from);
		} while (add && items.stackSize > 0 && success);

		return result;
	}

	private static boolean checkAvailableSlotOnce(IInventory inventory, ItemStack items, boolean add, EnumDirection from) {
		for (boolean addEmpty : new boolean[] {false, true}) {
			switch (inventory.getSizeInventory()) {
			case 2: //This is an input / output inventory
				if (from == EnumDirection.DOWN || from == EnumDirection.UP) {
					if (tryAdding(inventory, 0, items, add, addEmpty)) {
						return true;
					}
				} else {
					if (tryAdding(inventory, 1, items, add, addEmpty)) {
						return true;
					}
				}  
				break;
			case 3: //This is a furnace-like inventory
				if (from == EnumDirection.UP) {
					if (tryAdding(inventory, 0, items, add, addEmpty)) {
						return true;
					}
				} else if (from == EnumDirection.DOWN) {
					if (tryAdding(inventory, 1, items, add, addEmpty)) {
						return true;
					}
				}
				break;
			default: //This is a generic inventory
				IInventory inv = Utils.getInventory(inventory);

				for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
					if (tryAdding(inv, slot, items, add, addEmpty)) {
						return true;
					}
				}
				break;
			}
		}

		return false;
	}

	public static boolean tryAdding(IInventory inventory, int slot, ItemStack items, boolean doAdd, boolean addInEmpty) {
		ItemStack stack = inventory.getStackInSlot(slot);

		if (!addInEmpty) {
			if (stack != null) {
				if (stack.getItem() == items.getItem()
						&& stack.getItemDamage() == items.getItemDamage()
						&& stack.stackSize + 1 <= stack.getMaxStackSize()) {

					if (doAdd) {
						stack.stackSize++;
						items.stackSize--;
					}

					return true;
				}
			}
		} else {
			if (stack == null) {
				if (doAdd) {
					stack = new ItemStack(items.itemID, 1, items.getItemDamage());
					items.stackSize--;
					inventory.setInventorySlotContents(slot, stack);
				}

				return true;
			}
		}

		return false;
	}

	public static boolean addToRandomInventory(TileEntity tile, EnumDirection from, ItemStack items) {
		List<EnumDirection> possibleInventories = new ArrayList<>();

		for (EnumDirection direction : EnumDirection.VALID_DIRECTIONS) {
			if (Utils.reverseDirection(from) == direction) continue;

			Position pos = new Position(tile.xCoord, tile.yCoord, tile.zCoord, direction);
			pos.moveForwards(1);

			TileEntity tileInventory = tile.worldObj.getBlockTileEntity((int) pos.x, (int) pos.y, (int) pos.z);

			if (tileInventory instanceof ISpecialInventory) {
				if (((ISpecialInventory) tileInventory).addItem(items, false, from)) {
					possibleInventories.add(pos.orientation);
				}
			}

			if (tileInventory instanceof IInventory) {
				if (Utils.checkPipesConnections(tile.worldObj, tile.xCoord, tile.yCoord, tile.zCoord,
						tileInventory.xCoord, tileInventory.yCoord, tileInventory.zCoord)
						&& checkAvailableSlot((IInventory) tileInventory, items, false, Utils.reverseDirection(pos.orientation))) {
					possibleInventories.add(pos.orientation);
				}
			}
		}

		if (!possibleInventories.isEmpty()) {
			int choice = tile.worldObj.rand.nextInt(possibleInventories.size());

			Position pos = new Position(tile.xCoord, tile.yCoord, tile.zCoord, possibleInventories.get(choice));
			pos.moveForwards(1);

			TileEntity tileInventory = tile.worldObj.getBlockTileEntity((int) pos.x, (int) pos.y, (int) pos.z);
			checkAvailableSlot((IInventory) tileInventory, items, true, Utils.reverseDirection(pos.orientation));

			if (items.stackSize > 0) {
				return addToRandomInventory(tileInventory, from, items);
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
}