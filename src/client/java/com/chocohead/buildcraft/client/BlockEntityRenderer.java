package com.chocohead.buildcraft.client;

import java.util.EnumMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.world.RenderBlocks;
import net.minecraft.common.block.icon.Icon;
import net.minecraft.common.block.icon.IconRegister;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.common.block.Blocks;

import com.chocohead.buildcraft.entities.BlockEntity;
import com.chocohead.buildcraft.entities.BlockEntity.Texture;

public class BlockEntityRenderer extends EntityRenderer<BlockEntity> {
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
	public void doRender(BlockEntity entity, double x, double y, double z, float yaw, float pitch) {
		if (entity.isDead) {
			return;
		}

		double originalMinX = Blocks.SAND.minX;
		double originalMinY = Blocks.SAND.minY;
		double originalMinZ = Blocks.SAND.minZ;
		double originalMaxX = Blocks.SAND.maxX;
		double originalMaxY = Blocks.SAND.maxY;
		double originalMaxZ = Blocks.SAND.maxZ;
		try {
			Blocks.SAND.minX = 0;
			Blocks.SAND.minY = 0;
			Blocks.SAND.minZ = 0;

			shadowSize = entity.shadowSize;
			renderBlocks.setOverrideBlockTexture(textures.get(entity.texture));
			loadTexture("/terrain.png");

			for (int xBase = 0; xBase <= entity.xSize; ++xBase) {
				Blocks.SAND.maxX = Math.min(entity.xSize - xBase, 1);

				for (int yBase = 0; yBase <= entity.ySize; ++yBase) {
					Blocks.SAND.maxY = Math.min(entity.ySize - yBase, 1);

					for (int zBase = 0; zBase <= entity.zSize; ++zBase) {
						Blocks.SAND.maxZ = Math.min(entity.zSize - zBase, 1);

						GL11.glPushMatrix();
						GL11.glTranslatef((float)x + xBase + 0.5F, (float)y + yBase + 0.5F, (float)z + zBase + 0.5F);

						int lightX = (int) (Math.floor(entity.posX) + xBase);
						int lightY = (int) (Math.floor(entity.posY) + yBase);
						int lightZ = (int) (Math.floor(entity.posZ) + zBase);
						float light = entity.worldObj.getBlockLightValue(lightX, lightY, lightZ);
						GL11.glDisable(GL11.GL_LIGHTING);
						renderBlocks.renderBlockFallingSand(Blocks.SAND, 0, light);
						GL11.glEnable(GL11.GL_LIGHTING);
						GL11.glPopMatrix();
					}
				}
			}
		} finally {
			Blocks.SAND.minX = originalMinX;
			Blocks.SAND.minY = originalMinY;
			Blocks.SAND.minZ = originalMinZ;
			Blocks.SAND.maxX = originalMaxX;
			Blocks.SAND.maxY = originalMaxY;
			Blocks.SAND.maxZ = originalMaxZ;
		}
	}
}