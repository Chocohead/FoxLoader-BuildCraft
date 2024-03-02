package com.chocohead.buildcraft.pipes.logic;

import net.minecraft.src.game.block.tileentity.TileEntity;

import com.chocohead.buildcraft.blocks.PipeTileEntity;

public class ObsidianPipeLogic extends PipeLogic {
	@Override
	public boolean isPipeConnected(TileEntity tile) {
		if (!super.isPipeConnected(tile)) return false;
		if (tile instanceof PipeTileEntity<?>) {
			return !(((PipeTileEntity<?>) tile).getPipe().logic instanceof ObsidianPipeLogic);
		}
		return true;
	}
}