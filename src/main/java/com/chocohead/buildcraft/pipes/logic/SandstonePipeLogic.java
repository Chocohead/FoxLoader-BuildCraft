package com.chocohead.buildcraft.pipes.logic;

import net.minecraft.src.game.block.tileentity.TileEntity;

import com.chocohead.buildcraft.blocks.PipeTileEntity;

public class SandstonePipeLogic extends PipeLogic {
	@Override
	public boolean isPipeConnected(TileEntity tile) {
		return super.isPipeConnected(tile) && tile instanceof PipeTileEntity<?>;
	}
}