package com.chocohead.buildcraft.client;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import net.minecraft.client.renderer.world.RenderBlocks;
import net.minecraft.common.block.Block;

import com.fox2code.foxloader.client.BlockRender;
import com.fox2code.foxloader.client.BlockRenderManager;

public abstract class BlockRenderPlus extends BlockRender {
	private static final Int2ObjectMap<BlockRenderPlus> REGISTRY = new Int2ObjectArrayMap<>();

	public static boolean render(RenderBlocks rb, Block block, int damage, int type) {
		BlockRenderPlus renderer = REGISTRY.get(type);

		if (renderer != null) {
			renderer.render(rb, block, damage);

			return true;
		} else {
			return false;
		}
	}

	public BlockRenderPlus(boolean renderItemIn3D) {
		super(renderItemIn3D);

		BlockRenderManager.registerBlockRender(this);
		REGISTRY.put(getAssignedID(), this);
	}

	public abstract void render(RenderBlocks rb, Block block, int damage);
}