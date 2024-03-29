package com.chocohead.buildcraft.client;

import java.util.EnumMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.client.renderer.RenderBlocks;
import net.minecraft.src.client.renderer.block.icon.Icon;
import net.minecraft.src.client.renderer.block.icon.IconRegister;
import net.minecraft.src.client.renderer.entity.Render;
import net.minecraft.src.game.block.Block;
import net.minecraft.src.game.entity.Entity;

import com.chocohead.buildcraft.entities.BlockEntity;
import com.chocohead.buildcraft.entities.BlockEntity.Texture;

public class BlockEntityRenderer extends Render {
	public interface BlockEntityRenderBlocks {
		void setBlockEntityRenderer(boolean flag);
	}
	private final RenderBlocks renderBlocks = new RenderBlocks();
	private final Map<Texture, Icon> textures = new EnumMap<>(Texture.class);

	public BlockEntityRenderer() {
		((BlockEntityRenderBlocks) renderBlocks).setBlockEntityRenderer(true);
	}

	@Override
	public void updateIcons(IconRegister register) {
		for (Texture laser : Texture.values()) {
			textures.put(laser, register.registerIcon(laser.texture));
		}
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float pitch) {
		doRender((BlockEntity) entity, x, y, z);
	}

	private void doRender(BlockEntity entity, double x, double y, double z) {
		if (entity.isDead) {
			return;
		}

		double originalMinX = Block.sand.minX;
		double originalMinY = Block.sand.minY;
		double originalMinZ = Block.sand.minZ;
		double originalMaxX = Block.sand.maxX;
		double originalMaxY = Block.sand.maxY;
		double originalMaxZ = Block.sand.maxZ;
		try {
			Block.sand.minX = 0;
			Block.sand.minY = 0;
			Block.sand.minZ = 0;

			shadowSize = entity.shadowSize;
			renderBlocks.setOverrideBlockTexture(textures.get(entity.texture));
			loadTexture("/terrain.png");

			for (int xBase = 0; xBase <= entity.xSize; ++xBase) {
				Block.sand.maxX = Math.min(entity.xSize - xBase, 1);

				for (int yBase = 0; yBase <= entity.ySize; ++yBase) {
					Block.sand.maxY = Math.min(entity.ySize - yBase, 1);

					for (int zBase = 0; zBase <= entity.zSize; ++zBase) {
						Block.sand.maxZ = Math.min(entity.zSize - zBase, 1);

						GL11.glPushMatrix();
						GL11.glTranslatef((float)x + xBase + 0.5F, (float)y + yBase + 0.5F, (float)z + zBase + 0.5F);

						int lightX = (int) (Math.floor(entity.posX) + xBase);
						int lightY = (int) (Math.floor(entity.posY) + yBase);
						int lightZ = (int) (Math.floor(entity.posZ) + zBase);
						GL11.glDisable(GL11.GL_LIGHTING);
						renderBlocks.renderBlockFallingSand(Block.sand, entity.worldObj, lightX, lightY, lightZ, 0);
						GL11.glEnable(GL11.GL_LIGHTING);
						GL11.glPopMatrix();
					}
				}
			}
		} finally {
			Block.sand.minX = originalMinX;
			Block.sand.minY = originalMinY;
			Block.sand.minZ = originalMinZ;
			Block.sand.maxX = originalMaxX;
			Block.sand.maxY = originalMaxY;
			Block.sand.maxZ = originalMaxZ;
		}
	}
}