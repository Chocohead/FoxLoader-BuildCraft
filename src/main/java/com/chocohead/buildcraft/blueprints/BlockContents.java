package com.chocohead.buildcraft.blueprints;

public class BlockContents {
	public int blockId;
	public int x;
	public int y;
	public int z;

	@Override
	public BlockContents clone () {
		BlockContents out = new BlockContents();

		out.x = x;
		out.y = y;
		out.z = z;
		out.blockId = blockId;

		return out;
	}
}