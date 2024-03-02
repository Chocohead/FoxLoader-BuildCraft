package com.chocohead.buildcraft.blocks;

import net.minecraft.src.game.block.BlockContainer;
import net.minecraft.src.game.block.Material;
import net.minecraft.src.game.block.tileentity.TileEntity;
import net.minecraft.src.game.level.World;

public abstract class MetaBlockContainer extends BlockContainer {
	private int teMeta = -1;

	protected MetaBlockContainer(int id, Material material) {
		super(id, material);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		try {
			teMeta = world.getBlockMetadata(x, y, z);
			super.onBlockAdded(world, x, y, z);
		} finally {
			teMeta = -1;
		}
	}

	@Override
	protected final TileEntity getBlockEntity() {
		if (teMeta < 0) throw new UnsupportedOperationException("Didn't call onBlockAdded before getBlockEntity");
		return getTileEntity(teMeta);
	}

	protected abstract TileEntity getTileEntity(int meta);
}