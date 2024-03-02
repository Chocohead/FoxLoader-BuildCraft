package com.chocohead.buildcraft.pipes.logic;

import net.minecraft.src.client.inventory.IInventory;
import net.minecraft.src.game.Direction.EnumDirection;
import net.minecraft.src.game.block.tileentity.TileEntity;
import net.minecraft.src.game.entity.player.EntityPlayer;
import net.minecraft.src.game.nbt.NBTTagCompound;

import com.chocohead.buildcraft.BuildCraft;
import com.chocohead.buildcraft.Utils;
import com.chocohead.buildcraft.api.IPipeEntry;
import com.chocohead.buildcraft.api.Position;
import com.chocohead.buildcraft.blocks.PipeTileEntity;

public class IronPipeLogic extends PipeLogic {
	protected EnumDirection exit = EnumDirection.UNKNOWN;
	protected boolean wasPowered;

	public EnumDirection getExit() {
		return exit;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("source", exit.ordinal());
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		exit = Utils.ALL_DIRECTIONS[nbt.getInteger("source")];
	}

	@Override
	public void initialize() {
		super.initialize();

		wasPowered = world.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
		switchExit();
	}

	public void switchPowered() {
		boolean isPowered = world.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);

		if (isPowered != wasPowered) {
			switchExit();

			wasPowered = isPowered;
		}
	}

	public void switchExit() {
		for (int current = exit.ordinal(), i = current + 1; i <= current + 6; i++) {
			EnumDirection facing = EnumDirection.VALID_DIRECTIONS[i % 6];
			Position pos = new Position(xCoord, yCoord, zCoord, facing);
			pos.moveForwards(1);

			TileEntity tile = world.getBlockTileEntity((int) pos.x, (int) pos.y, (int) pos.z);
			if (tile instanceof PipeTileEntity<?> && ((PipeTileEntity<?>) tile).getPipe().logic instanceof WoodenPipeLogic) {
				continue;
			}

			if (tile instanceof IPipeEntry || tile instanceof IInventory /*|| tile instanceof ILiquidContainer*/ || tile instanceof PipeTileEntity<?>) {
				exit = facing;
				break;
			}
		}

		System.out.printf("Iron pipe at %d, %d, %d changed to %s%n", xCoord, yCoord, zCoord, exit);
		world.markBlockNeedsUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void onBlockPlaced() {
		switchExit();
	}

	@Override
	public void onNeighborBlockChange() {
		super.onNeighborBlockChange();

		switchPowered();
	}

	@Override
	public boolean blockActivated(EntityPlayer player) {
		if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == BuildCraft.wrench) {
			switchExit();

			return true;
		}

		return false;
	}

	@Override
	public boolean outputOpen(EnumDirection to) {
		return to == exit;
	}
}