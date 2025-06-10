package com.chocohead.buildcraft.pipes.logic;

import net.minecraft.common.entity.inventory.IInventory;
import net.minecraft.common.util.Direction.EnumDirection;
import net.minecraft.common.block.tileentity.TileEntity;
import net.minecraft.common.entity.player.EntityPlayer;
import com.mojang.nbt.CompoundTag;

import com.chocohead.buildcraft.BuildCraft;
import com.chocohead.buildcraft.Utils;
import com.chocohead.buildcraft.api.Position;
import com.chocohead.buildcraft.blocks.PipeTileEntity;
import com.chocohead.buildcraft.pipes.Pipe;

public class WoodenPipeLogic extends PipeLogic {
	protected EnumDirection source = EnumDirection.UNKNOWN;

	public EnumDirection getSource() {
		return source;
	}

	@Override
	public void writeToNBT(CompoundTag nbt) {
		nbt.setInteger("source", source.ordinal());
	}

	@Override
	public void readFromNBT(CompoundTag nbt) {
		source = Utils.ALL_DIRECTIONS[nbt.getInteger("source")];
	}

	@Override
	public void initialize() {
		super.initialize();

		switchSource();
	}

	@Override
	public boolean isPipeConnected(TileEntity tile) {
		Pipe<?> pipe = null;

		if (tile instanceof PipeTileEntity<?>) {
			pipe = ((PipeTileEntity<?>) tile).getPipe();
		}

		return (pipe == null || !(pipe.logic instanceof WoodenPipeLogic)) && super.isPipeConnected(tile);
	}

	public void switchSource() {
		for (int current = source.ordinal(), i = current + 1; i <= current + 6; i++) {
			EnumDirection facing = EnumDirection.VALID_DIRECTIONS[i % 6];
			Position pos = new Position(xCoord, yCoord, zCoord, facing);
			pos.moveForwards(1);

			TileEntity tile = world.getBlockTileEntity((int) pos.x, (int) pos.y, (int) pos.z);
			if (isInput(tile)) {
				source = facing;
				break;
			}
		}

		world.markBlockNeedsUpdate(xCoord, yCoord, zCoord);
	}

	protected boolean isInput(TileEntity tile) {
		return !(tile instanceof PipeTileEntity<?>)
				&& (tile instanceof IInventory/* || tile instanceof ILiquidContainer*/)
				&&  Utils.checkPipesConnections(world, xCoord, yCoord, zCoord, tile.xCoord, tile.yCoord, tile.zCoord);
	}

	@Override
	public void onNeighborBlockChange() {
		super.onNeighborBlockChange();

		if (source == EnumDirection.UNKNOWN) {
			switchSource();
		} else {
			Position pos = new Position(xCoord, yCoord, zCoord, source);		
			pos.moveForwards(1);
			TileEntity tile = world.getBlockTileEntity((int) pos.x, (int) pos.y, (int) pos.z);

			if (!isInput(tile)) {
				switchSource();
			}
		}	
	}

	@Override
	public boolean blockActivated(EntityPlayer player) {
		if (player.getHeldItem() != null && player.getHeldItem().getItem() == BuildCraft.wrench) {
			switchSource();

			return true;
		}

		return false;
	}
}