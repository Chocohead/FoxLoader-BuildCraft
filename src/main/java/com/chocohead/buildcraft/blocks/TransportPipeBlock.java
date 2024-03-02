package com.chocohead.buildcraft.blocks;

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
import com.chocohead.buildcraft.pipes.transport.PipeTransport;

import net.minecraft.src.game.level.World;

public class TransportPipeBlock extends PipeBlock {
	public TransportPipeBlock(int id) {
		super(id);

		maxMetadata = 8;
	}

	@Override
	protected PipeTileEntity<?> getTileEntity(int meta) {
		return new TransportPipeTileEntity(getPipe(meta));
	}

	@Override
	protected Pipe<ItemPipeTransport> getPipe(int meta) {
		switch (meta) {
		case 0:
			return new WoodenItemPipe();
		case 1:
			return new CobbleItemPipe();
		case 2:
			return new StoneItemPipe();
		case 3:
			return new SandstoneItemPipe();
		case 4:
			return new ClayItemPipe();
		case 5:
			return new IronItemPipe();
		case 6:
			return new GoldItemPipe();
		case 7:
			return new DiamondItemPipe();
		case 8:
			return new ObsidianItemPipe();
		default:
			return null;			
		}
	}

	@Override
	public void onBlockRemoval(World world, int x, int y, int z) {
		PipeTransport transport = getPipe(world, x, y, z).transport;
		if (transport instanceof ItemPipeTransport) ((ItemPipeTransport) transport).destroy();

		super.onBlockRemoval(world, x, y, z);
	}
}