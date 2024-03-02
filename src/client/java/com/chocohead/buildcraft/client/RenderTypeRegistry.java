package com.chocohead.buildcraft.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.client.renderer.RenderBlocks;
import net.minecraft.src.game.block.Block;
import net.minecraft.src.game.level.IBlockAccess;

import com.chocohead.buildcraft.mixins.Plugin;

public class RenderTypeRegistry {
	public interface Renderer {
		void render(RenderBlocks rb, IBlockAccess world, int x, int y, int z, Block block);
	}
	public interface RendererPlus extends Renderer {
		void render(RenderBlocks rb, Block block, int damage);
	}
	private static final List<Renderer> RENDER_TYPES = new ArrayList<>();

	public static int register(Renderer renderer) {
		int type = Plugin.AVAILABLE_RENDER_TYPES.nextSetBit(Plugin.RENDER_BLOCKS_TYPE_START + RENDER_TYPES.size());
		if (type < 0) throw new IllegalStateException("No more free render block types"); //Oh dear
		while (RENDER_TYPES.size() < type - Plugin.RENDER_BLOCKS_TYPE_START) RENDER_TYPES.add(null); //Plug the gaps
		RENDER_TYPES.add(renderer);
		Plugin.AVAILABLE_RENDER_TYPES.set(type);
		return type;
	}

	public static boolean render(RenderBlocks rb, IBlockAccess world, int x, int y, int z, Block block, int type) {
		type -= Plugin.RENDER_BLOCKS_TYPE_START; 
		if (type < 0 || type >= RENDER_TYPES.size()) return false;
		Renderer renderer = RENDER_TYPES.get(type);
		if (renderer != null) {
			renderer.render(rb, world, x, y, z, block);
			return true;
		}
		return false;
	}

	public static boolean renderItemIn3D(int type) {
		type -= Plugin.RENDER_BLOCKS_TYPE_START;
		return type >= 0 && type < RENDER_TYPES.size() && RENDER_TYPES.get(type) instanceof RendererPlus;
	}

	public static boolean render(RenderBlocks rb, Block block, int damage, int type) {
		type -= Plugin.RENDER_BLOCKS_TYPE_START;
		if (type < 0 || type >= RENDER_TYPES.size()) return false;
		Renderer renderer = RENDER_TYPES.get(type);
		if (renderer instanceof RendererPlus) {
			((RendererPlus) renderer).render(rb, block, damage);
			return true;
		}
		return false;
	}
}