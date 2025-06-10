package com.chocohead.buildcraft.client;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.world.RenderBlocks;
import net.minecraft.client.renderer.world.Tessellator;
import net.minecraft.common.block.icon.Icon;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.common.block.Block;
import net.minecraft.common.block.Blocks;
import net.minecraft.client.renderer.block.tileentity.TileEntityRenderer;
import net.minecraft.common.item.block.ItemBlock;
import net.minecraft.common.item.ItemStack;

import com.chocohead.buildcraft.api.EntityPassiveItem;
import com.chocohead.buildcraft.blocks.TransportPipeTileEntity;
import com.chocohead.buildcraft.pipes.transport.ItemPipeTransport.EntityData;

public class TransportPipeRenderer extends TileEntityRenderer<TransportPipeTileEntity> {
	private final RenderBlocks renderBlocks = new RenderBlocks();
	private final Random random = new Random();

	@Override
	public void renderTileEntityAt(TransportPipeTileEntity pipe, double x, double y, double z, float partialTicks, int progress) {
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);

		for (EntityData data : pipe.getPipe().transport.cloneTravellingEntities()) {
			doRenderItem(data.item,
					x + data.item.posX - pipe.xCoord,
					y + data.item.posY - pipe.yCoord,
					z + data.item.posZ - pipe.zCoord,
					pipe.worldObj.getLightBrightness(pipe.xCoord, pipe.yCoord, pipe.zCoord));
		}

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

	private void doRenderItem(EntityPassiveItem entity, double x, double y, double z, double brightness) {
		if (entity == null || entity.item == null) {
			return;
		}
		random.setSeed(187L);

		ItemStack stack = entity.item;
		byte copies;
		if (entity.item.stackSize > 20) {
			copies = 4;
		} else if (entity.item.stackSize > 5) {
			copies = 3;
		} else if (entity.item.stackSize > 1) {
			copies = 2;
		} else {
			copies = 1;
		}

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y, (float) z);
		GL11.glEnable(32826 /* GL_RESCALE_NORMAL_EXT */);

		if (stack.getItemSpriteNumber() == 0 && stack.getItem().isItemBlock()
				&& RenderBlocks.renderItemIn3d(Blocks.BLOCKS_LIST[((ItemBlock) stack.getItem()).blockID].getRenderType())) {
			GL11.glTranslatef(0, 0.25F, 0);
			bindTextureByName("/terrain.png");

			Block block = Blocks.BLOCKS_LIST[((ItemBlock) stack.getItem()).blockID];
			float scale = 0.25F;
			/*if (!Block.blocksList[stack.itemID].renderAsNormalBlock()
					&& stack.itemID != Block.slabSingleRock.blockID) {
				f4 = 0.5F;
			}*/
			int renderType = block.getRenderType();
			if (renderType == 1 || renderType == 19 || renderType == 12 || renderType == 2) {
				scale = 0.5F;
			}
			GL11.glScalef(scale, scale, scale);
			for (int copy = 0; copy < copies; copy++) {
				GL11.glPushMatrix();
				if (copy > 0) {
					float jitterX = ((random.nextFloat() * 2F - 1F) * 0.2F) / scale;
					float jitterY = ((random.nextFloat() * 2F - 1F) * 0.2F) / scale;
					float jitterZ = ((random.nextFloat() * 2F - 1F) * 0.2F) / scale;
					GL11.glTranslatef(jitterX, jitterY, jitterZ);
				}
				renderBlocks.renderBlockOnInventory(block, stack.getItemDamage(), (float) brightness);
				GL11.glPopMatrix();
			}
		} else {
			GL11.glTranslatef(0, 0.10F, 0);
			GL11.glScalef(0.5F, 0.5F, 0.5F);
			Icon icon = stack.getIconIndex();
			bindTextureByName(stack.getItemSpriteNumber() == 0 ? "/terrain.png" : "/gui/items.png");
			Tessellator tessellator = Tessellator.instance;
			float minX = icon.getMinU();
            float maxX = icon.getMaxU();
            float minY = icon.getMinV();
            float maxY = icon.getMaxV();
			float f12 = 1.0F;
			float f13 = 0.5F;
			float f14 = 0.25F;
			for (int copy = 0; copy < copies; copy++) {
				GL11.glPushMatrix();
				if (copy > 0) {
					float jitterX = (random.nextFloat() * 2F - 1F) * 0.3F;
					float jitterY = (random.nextFloat() * 2F - 1F) * 0.3F;
					float jitterZ = (random.nextFloat() * 2F - 1F) * 0.3F;
					GL11.glTranslatef(jitterX, jitterY, jitterZ);
				}
				GL11.glRotatef(180F - EntityRendererManager.instance.playerViewY, 0F, 1F, 0F);
				tessellator.startDrawingQuads();
				tessellator.setNormal(0F, 1F, 0F);
				tessellator.addVertexWithUV(0F - f13, 0F - f14, 0D, minX, maxY);
				tessellator.addVertexWithUV(f12 - f13, 0F - f14, 0D, maxX, maxY);
				tessellator.addVertexWithUV(f12 - f13, 1F - f14, 0D, maxX, minY);
				tessellator.addVertexWithUV(0F - f13, 1F - f14, 0D, minX, minY);
				tessellator.draw();
				GL11.glPopMatrix();
			}
		}
		GL11.glDisable(32826 /* GL_RESCALE_NORMAL_EXT */);
		GL11.glPopMatrix();
	}
}