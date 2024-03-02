package com.chocohead.buildcraft.blocks;

import net.minecraft.src.game.Direction.EnumDirection;
import net.minecraft.src.game.entity.player.EntityPlayer;
import net.minecraft.src.game.item.ItemStack;

import com.chocohead.buildcraft.api.EntityPassiveItem;
import com.chocohead.buildcraft.api.IPipeEntry;
import com.chocohead.buildcraft.api.ISpecialInventory;
import com.chocohead.buildcraft.pipes.ClayItemPipe;
import com.chocohead.buildcraft.pipes.CobbleItemPipe;
import com.chocohead.buildcraft.pipes.DiamondItemPipe;
import com.chocohead.buildcraft.pipes.GoldItemPipe;
import com.chocohead.buildcraft.pipes.IronItemPipe;
import com.chocohead.buildcraft.pipes.ObsidianItemPipe;
import com.chocohead.buildcraft.pipes.Pipe;
import com.chocohead.buildcraft.pipes.SandstoneItemPipe;
import com.chocohead.buildcraft.pipes.StoneItemPipe;
import com.chocohead.buildcraft.pipes.WoodenItemPipe;
import com.chocohead.buildcraft.pipes.transport.ItemPipeTransport;

public class TransportPipeTileEntity extends PipeTileEntity<ItemPipeTransport> implements ISpecialInventory, IPipeEntry {
	public TransportPipeTileEntity() {
	}

	public TransportPipeTileEntity(Pipe<ItemPipeTransport> pipe) {
		super(pipe);
	}

	@Override
	protected Pipe<ItemPipeTransport> loadPipe(String id) {
		switch (id) {
		case WoodenItemPipe.ID:
			return new WoodenItemPipe();
		case CobbleItemPipe.ID:
			return new CobbleItemPipe();
		case StoneItemPipe.ID:
			return new StoneItemPipe();
		case SandstoneItemPipe.ID:
			return new SandstoneItemPipe();
		case ClayItemPipe.ID:
			return new ClayItemPipe();
		case IronItemPipe.ID:
			return new IronItemPipe();
		case GoldItemPipe.ID:
			return new GoldItemPipe();
		case DiamondItemPipe.ID:
			return new DiamondItemPipe();
		case ObsidianItemPipe.ID:
			return new ObsidianItemPipe();
		default:
			throw new IllegalArgumentException("Unexpected transport pipe ID: " + id);
		}
	}

	@Override
	public void onInventoryChanged() {		
	}

	@Override
	public int getSizeInventory() {
		return pipe.logic.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return pipe.logic.getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return pipe.logic.decrStackSize(slot, amount);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		pipe.logic.setInventorySlotContents(slot, stack);		
	}

	@Override
	public String getInvName() {
		return pipe.logic.getInvName();
	}

	@Override
	public int getInventoryStackLimit() {
		return pipe.logic.getInventoryStackLimit();
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return pipe.logic.canInteractWith(player);
	}

	@Override
	public boolean addItem(ItemStack stack, boolean doAdd, EnumDirection from) {
		return pipe.logic.addItem(stack, doAdd, from);
	}

	@Override
	public ItemStack extractItem(boolean doRemove, EnumDirection from) {
		return pipe.logic.extractItem(doRemove, from);
	}

	@Override
	public void entityEntering(EntityPassiveItem item, EnumDirection orientation) {
		pipe.transport.entityEntering(item, orientation);
	}

	@Override
	public boolean acceptItems() {
		return pipe.transport.acceptItems();
	}
}