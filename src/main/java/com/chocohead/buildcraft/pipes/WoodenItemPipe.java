package com.chocohead.buildcraft.pipes;

import net.minecraft.src.client.inventory.IInventory;
import net.minecraft.src.client.renderer.block.icon.Icon;
import net.minecraft.src.client.renderer.block.icon.IconRegister;
import net.minecraft.src.game.Direction.EnumDirection;
import net.minecraft.src.game.block.tileentity.TileEntity;
import net.minecraft.src.game.item.ItemStack;

import com.chocohead.buildcraft.Utils;
import com.chocohead.buildcraft.api.EntityPassiveItem;
import com.chocohead.buildcraft.api.IPowerReceptor;
import com.chocohead.buildcraft.api.ISpecialInventory;
import com.chocohead.buildcraft.api.Position;
import com.chocohead.buildcraft.api.PowerProvider;
import com.chocohead.buildcraft.pipes.logic.WoodenPipeLogic;
import com.chocohead.buildcraft.pipes.transport.ItemPipeTransport;

public class WoodenItemPipe extends Pipe<ItemPipeTransport> implements IPowerReceptor {
	public static final String ID = "woodenItem";
	private static Icon normalTexture, connectedTexture;
	private static Icon activeTexture;
	protected final PowerProvider power = new PowerProvider();

	public WoodenItemPipe() {
		super(new ItemPipeTransport(), new WoodenPipeLogic(), ID);

		power.configure(1, 64, 1, 64);
		power.configurePowerPerdition(64, 1);
	}

	@Override
	public void registerIcons(IconRegister register) {
		activeTexture = normalTexture = register.registerIcon("buildcraft/woodenPipe");
		connectedTexture = register.registerIcon("buildcraft/connectedWoodenPipe");
	}

	@Override
	public void prepareTextureFor(EnumDirection connection) {
		if (connection != EnumDirection.UNKNOWN && ((WoodenPipeLogic) logic).getSource() == connection) {
			activeTexture = connectedTexture;
		} else {
			activeTexture = normalTexture;
		}
	}

	@Override
	public Icon getBlockTexture() {
		return activeTexture;
	}

	@Override
	public String getName() {
		return "item.buildcraft.itemPipe.wooden";
	}

	@Override
	public PowerProvider getPowerProvider() {
		return power;
	}

	@Override
	public void doWork() {
		if (power.energyStored <= 0 || ((WoodenPipeLogic) logic).getSource() == EnumDirection.UNKNOWN) {
			return;
		}

		Position pos = new Position(xCoord, yCoord, zCoord, ((WoodenPipeLogic) logic).getSource());
		pos.moveForwards(1);
		TileEntity tile = world.getBlockTileEntity((int) pos.x, (int) pos.y, (int) pos.z);
		if (tile == null || !(tile instanceof IInventory /*|| tile instanceof ILiquidContainer*/)) {
			return;
		}

		if (tile instanceof IInventory) {
			IInventory inventory = (IInventory) tile;

			ItemStack stack = checkExtract(inventory, true, Utils.reverseDirection(pos.orientation));
			if (stack == null || stack.stackSize == 0) {
				power.useEnergy(1, 1, false);
				return;
			}

			Position entityPos = new Position(pos.x + 0.5, pos.y + Utils.getPipeHeight(stack), pos.z + 0.5, Utils.reverseDirection(pos.orientation));
			entityPos.moveForwards(0.5);
			EntityPassiveItem entity = new EntityPassiveItem(world, entityPos.x, entityPos.y, entityPos.z, stack);
			transport.entityEntering(entity, entityPos.orientation);
		}
	}

	protected ItemStack checkExtract(IInventory inventory, boolean doRemove, EnumDirection from) {
		if (inventory instanceof ISpecialInventory) {
			return ((ISpecialInventory) inventory).extractItem(doRemove, from);
		}

		int slot;
		out: switch (inventory.getSizeInventory()) {
		case 2: //This is an input / output inventory
			if (from == EnumDirection.DOWN || from == EnumDirection.UP) {
				slot = 0;
			} else {
				slot = 1;
			}
			if (isSlotEmpty(inventory, slot)) return null;
			break;
		case 3: //This is a furnace-like inventory
			if (from == EnumDirection.UP) {
				slot = 0;
			} else if (from == EnumDirection.DOWN) {
				slot = 1;
			} else {
				slot = 2;
			}
			if (isSlotEmpty(inventory, slot)) return null;
			break;
		default: //This is a generic inventory
			IInventory inv = Utils.getInventory(inventory);

			for (slot = 0; slot < inv.getSizeInventory(); slot++) {
				if (!isSlotEmpty(inventory, slot)) break out;
			}

			return null;
		}

		ItemStack stack = inventory.getStackInSlot(slot);
		if (doRemove) {
			return inventory.decrStackSize(slot, power.useEnergy(1, stack.stackSize, true));
		} else {
			return stack;
		}
	}

	private static boolean isSlotEmpty(IInventory inventory, int slot) {
		ItemStack stack = inventory.getStackInSlot(slot);
		return stack == null || stack.stackSize <= 0;
	}
}