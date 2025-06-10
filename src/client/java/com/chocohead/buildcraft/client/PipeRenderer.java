package com.chocohead.buildcraft.client;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.world.RenderBlocks;
import net.minecraft.client.renderer.world.Tessellator;
import net.minecraft.common.util.Direction.EnumDirection;
import net.minecraft.common.block.Block;
import net.minecraft.common.world.BlockAccess;

import com.chocohead.buildcraft.Utils;

public class PipeRenderer extends BlockRenderPlus {
	public PipeRenderer() {
		super(true);
	}

	@Override
	public boolean renderBlock(RenderBlocks rb, BlockAccess world, Block block, int x, int y, int z) {
		IPipeBlock pipe = (IPipeBlock) block;
		float minSize = Utils.PIPE_MIN_POS;
		float maxSize = Utils.PIPE_MAX_POS;

		pipe.prepareTextureFor(world, x, y, z, EnumDirection.UNKNOWN);
		block.setBlockBounds(minSize, minSize, minSize, maxSize, maxSize, maxSize);
		rb.renderStandardBlock(block, x, y, z);

		if (Utils.checkPipesConnections(world, x, y, z, x - 1, y, z)) {
			pipe.prepareTextureFor(world, x, y, z, EnumDirection.WEST);
			block.setBlockBounds(0F, minSize, minSize, minSize, maxSize, maxSize);
			rb.renderStandardBlock(block, x, y, z);
		}

		if (Utils.checkPipesConnections(world, x, y, z, x + 1, y, z)) {
			pipe.prepareTextureFor(world, x, y, z, EnumDirection.EAST);
			block.setBlockBounds(maxSize, minSize, minSize, 1F, maxSize, maxSize);
			rb.renderStandardBlock(block, x, y, z);
		}

		if (Utils.checkPipesConnections(world, x, y, z, x, y - 1, z)) {
			pipe.prepareTextureFor(world, x, y, z, EnumDirection.DOWN);
			block.setBlockBounds(minSize, 0F, minSize, maxSize, minSize, maxSize);
			rb.renderStandardBlock(block, x, y, z);
		}

		if (Utils.checkPipesConnections(world, x, y, z, x, y + 1, z)) {
			pipe.prepareTextureFor(world, x, y, z, EnumDirection.UP);
			block.setBlockBounds(minSize, maxSize, minSize, maxSize, 1F, maxSize);
			rb.renderStandardBlock(block, x, y, z);
		}

		if (Utils.checkPipesConnections(world, x, y, z, x, y, z - 1)) {
			pipe.prepareTextureFor(world, x, y, z, EnumDirection.NORTH);
			block.setBlockBounds(minSize, minSize, 0F, maxSize, maxSize, minSize);
			rb.renderStandardBlock(block, x, y, z);
		}

		if (Utils.checkPipesConnections(world, x, y, z, x, y, z + 1)) {
			pipe.prepareTextureFor(world, x, y, z, EnumDirection.SOUTH);
			block.setBlockBounds(minSize, minSize, maxSize, maxSize, maxSize, 1F);
			rb.renderStandardBlock(block, x, y, z);
		}

		block.setBlockBounds(0F, 0F, 0F, 1F, 1F, 1F);
		pipe.prepareTextureFor(world, x, y, z, EnumDirection.UNKNOWN); //Reset the active texture
		return true;
	}

	@Override
	public void render(RenderBlocks rb, Block block, int damage) {
		Tessellator tessellator = Tessellator.instance;

		block.setBlockBounds(Utils.PIPE_MIN_POS, 0F, Utils.PIPE_MIN_POS, Utils.PIPE_MAX_POS, 1F, Utils.PIPE_MAX_POS);
		block.setBlockBoundsForItemRender();
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0F, -1F, 0F);
		rb.renderBottomFace(block, 0D, 0D, 0D, block.getIcon(0, damage));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0F, 1F, 0F);
		rb.renderTopFace(block, 0D, 0D, 0D, block.getIcon(1, damage));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0F, 0F, -1F);
		rb.renderEastFace(block, 0D, 0D, 0D, block.getIcon(2, damage));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0F, 0F, 1F);
		rb.renderWestFace(block, 0D, 0D, 0D, block.getIcon(3, damage));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1F, 0F, 0F);
		rb.renderNorthFace(block, 0D, 0D, 0D, block.getIcon(4, damage));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1F, 0F, 0F);
		rb.renderSouthFace(block, 0D, 0D, 0D, block.getIcon(5, damage));
		tessellator.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		block.setBlockBounds(0F, 0F, 0F, 1F, 1F, 1F);
	}
}