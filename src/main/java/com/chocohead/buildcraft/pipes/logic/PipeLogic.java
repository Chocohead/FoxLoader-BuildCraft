package com.chocohead.buildcraft.pipes.logic;

import net.minecraft.src.game.Direction.EnumDirection;
import net.minecraft.src.game.entity.player.EntityPlayer;
import net.minecraft.src.game.item.ItemStack;

import com.chocohead.buildcraft.api.ISpecialInventory;
import com.chocohead.buildcraft.pipes.PipeLike;

public abstract class PipeLogic extends PipeLike implements ISpecialInventory {
	public boolean blockActivated(EntityPlayer player) {
		return false;
	}

	@Override
	public void onInventoryChanged() {	
	}

	@Override
	public ItemStack extractItem(boolean doRemove, EnumDirection from) {
		return null;
	}

	@Override
	public int getSizeInventory() { 
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {		
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
	}

	@Override
	public String getInvName() {
		return null;
	}

	@Override
	public int getInventoryStackLimit() {
		return 0;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return false;
	}

	@Override
	public boolean addItem(ItemStack stack, boolean doAdd, EnumDirection from) {
		return false;
	}
}