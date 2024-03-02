package com.chocohead.buildcraft.pipes.logic;

import net.minecraft.src.game.entity.player.EntityPlayer;
import net.minecraft.src.game.item.ItemStack;
import net.minecraft.src.game.nbt.NBTTagCompound;
import net.minecraft.src.game.nbt.NBTTagList;

import com.chocohead.buildcraft.Proxy;
import com.chocohead.buildcraft.blocks.TransportPipeTileEntity;
import com.chocohead.buildcraft.items.PipeItem;

public class DiamondPipeLogic extends PipeLogic {
	protected final ItemStack[] items = new ItemStack[54];

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		NBTTagList items = new NBTTagList();

		for (int slot = 0; slot < this.items.length; slot++) {
			if (this.items[slot] != null && this.items[slot].stackSize > 0) {
				NBTTagCompound stack = new NBTTagCompound();
				items.setTag(stack);
				stack.setInteger("index", slot);
				this.items[slot].writeToNBT(stack);	
			}
		}

		nbt.setTag("items", items);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		NBTTagList items = nbt.getTagList("items");

		for (int i = 0; i < items.tagCount(); ++i) {
			NBTTagCompound tag = (NBTTagCompound) items.tagAt(i);
			int index = tag.getInteger("index");
			this.items[index] = new ItemStack(tag);
		}
	}

	@Override
	public boolean blockActivated(EntityPlayer player) {
		if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof PipeItem) {
			return false;
		}

		if (!world.multiplayerWorld) {
			Proxy.displayDiamondPipeGUI(player, (TransportPipeTileEntity) container);
		}

		return true;
	}

	@Override
	public int getSizeInventory() {
		return items.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return items[i];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = items[slot].copy();
		stack.stackSize = amount;

		items[slot].stackSize -= amount;
		if (items[slot].stackSize == 0) {
			items[slot] = null;
		}

		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		items[slot] = stack;
	}

	@Override
	public String getInvName() {
		return "item.buildcraft.itemPipe.diamond.gui";
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return world.getBlockTileEntity(xCoord, yCoord, zCoord) == container && player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) <= 64;
	}
}