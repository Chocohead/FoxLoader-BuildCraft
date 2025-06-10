package com.chocohead.buildcraft.blocks.gui;

import net.minecraft.common.block.container.Container;
import net.minecraft.common.block.container.Slot;
import net.minecraft.common.block.tileentity.TileEntityFurnace;
import net.minecraft.common.entity.player.EntityPlayer;
import net.minecraft.common.entity.player.InventoryPlayer;
import net.minecraft.common.item.ItemStack;
import net.minecraft.common.recipe.ICrafting;

import com.chocohead.buildcraft.blocks.EngineTileEntity;
import com.chocohead.buildcraft.engines.SteamEngine;

public class EngineContainer extends Container {
	private final EntityPlayer player;
	protected final EngineTileEntity engine;
	protected int scaledBurnTime;

	public EngineContainer(InventoryPlayer player, EngineTileEntity tile) {
		this.player = player.player;
		engine = tile;

		if (tile.getEngine() instanceof SteamEngine) {
			addSlot(new Slot(tile, 0, 80, 41)); 
		} else {
			addSlot(new Slot(tile, 0, 52, 41));
		}

		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				addSlot(new Slot(player, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
			}
		}

		for (int col = 0; col < 9; col++) {
			addSlot(new Slot(player, col, 8 + col * 18, 142));
		} 
	}

	@Override
	public void updateInventory() {
		super.updateInventory();

		int targetBurnTime = engine.getEngine().getScaledBurnTime(12);
		if (scaledBurnTime != targetBurnTime) {
			for (ICrafting watcher : crafters) {
				watcher.updateCraftingInventoryInfo(this, 0, targetBurnTime);
			}

			scaledBurnTime = targetBurnTime;
		}
	}

	@Override
	public void func_20112_a(int bar, int progress) {
		if (bar == 0) {
			scaledBurnTime = progress;
		}
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return engine.canInteractWith(player);
	}

	@Override
	public ItemStack quickMove(int index) {
		Slot slot = slots.get(index);
		ItemStack returnStack = null;

		if (slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			returnStack = slotStack.copy();

			if (index == 0) {
				mergeItemStack(slotStack, 0 + 1, 36 + 1, false);
			} else {
				if (TileEntityFurnace.getItemBurnTime(slotStack) > 0) {
					mergeItemStack(slotStack, 0, 1, false);
				} else if (index < 30) {
					mergeItemStack(slotStack, 27 + 1, 36 + 1, false);
				} else if (index < 39) {
					mergeItemStack(slotStack, 0 + 1, 27 + 1, false);
				}
			}

			if (slotStack.stackSize == 0) {
				slot.putStack(null);
			} else {
				slot.onSlotChanged();
			}

			if (slotStack.stackSize == returnStack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(player, slotStack);
		}

		return returnStack;
	}
}