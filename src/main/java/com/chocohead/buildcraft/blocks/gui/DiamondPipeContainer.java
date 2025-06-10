package com.chocohead.buildcraft.blocks.gui;

import net.minecraft.common.block.container.Container;
import net.minecraft.common.block.container.Slot;
import net.minecraft.common.entity.inventory.IInventory;
import net.minecraft.common.entity.player.EntityPlayer;
import net.minecraft.common.entity.player.InventoryPlayer;
import net.minecraft.common.item.ItemStack;

public class DiamondPipeContainer extends Container {
	private final EntityPlayer player;
	private final IInventory pipe;

	public DiamondPipeContainer(InventoryPlayer player, IInventory pipe) {
		this.player = player.player;
		this.pipe = pipe;

		for (int row = 0; row < 6; row++) {
			for (int col = 0; col < 9; col++) {
				addSlot(new Slot(pipe, col + row * 9, 8 + col * 18, 18 + row * 18));
			}
		}

		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				addSlot(new Slot(player, col + row * 9 + 9, 8 + col * 18, 140 + row * 18));
			}
		}

		for (int col = 0; col < 9; col++) {
			addSlot(new Slot(player, col, 8 + col * 18, 198));
		}
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return pipe.canInteractWith(player);
	}

	@Override
	public ItemStack quickMove(int index) {
		Slot slot = slots.get(index);
		ItemStack returnStack = null;

		if (slot != null && slot.getHasStack()) {
			ItemStack stack = slot.getStack();
			returnStack = stack.copy();

			if (index < 6 * 9) {
				mergeItemStack(stack, 6 * 9, slots.size(), true);
			} else {
				mergeItemStack(stack, 0, 6 * 9, false);
			}

			if (stack.stackSize == 0) {
				slot.putStack(null);
			} else {
				slot.onSlotChanged();
			}

			if (stack.stackSize == returnStack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(player, stack);
		}

		return returnStack;
	}
}