package com.chocohead.buildcraft.client;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.client.model.Piece;
import net.minecraft.src.client.renderer.RenderBlocks;
import net.minecraft.src.game.Direction.EnumDirection;
import net.minecraft.src.game.MathHelper;
import net.minecraft.src.game.block.Block;
import net.minecraft.src.game.block.tileentity.TileEntity;
import net.minecraft.src.game.block.tileentity.TileEntitySpecialRenderer;
import net.minecraft.src.game.level.IBlockAccess;

import com.chocohead.buildcraft.blocks.EngineTileEntity;
import com.chocohead.buildcraft.client.RenderTypeRegistry.RendererPlus;
import com.chocohead.buildcraft.engines.Engine;
import com.chocohead.buildcraft.engines.Engine.EnergyStage;
import com.chocohead.buildcraft.engines.GearedEngine;
import com.chocohead.buildcraft.engines.SteamEngine;

public class EngineRenderer extends TileEntitySpecialRenderer implements RendererPlus {
	public static int renderID;
	private final Piece box;
	private final Piece trunk;
	private final Piece movingBox;
	private final Piece chamber;

	public EngineRenderer() {
		box = new Piece(0, 0);
		box.addBox(-8F, -8F, -8F, 16, 4, 16);		
		box.rotationPointX = 8;
		box.rotationPointY = 8;
		box.rotationPointZ = 8;

		trunk = new Piece(0, 0);
		trunk.addBox(-4F, -4F, -4F, 8, 12, 8);
		trunk.rotationPointX = 8F;
		trunk.rotationPointY = 8F;
		trunk.rotationPointZ = 8F;

		movingBox = new Piece(0, 0);
		movingBox.addBox(-8F, -4, -8F, 16, 4, 16);
		movingBox.rotationPointX = 8F;
		movingBox.rotationPointY = 8F;
		movingBox.rotationPointZ = 8F;

		chamber = new Piece(0, 0);
		chamber.addBox(-5F, -4, -5F, 10, 2, 10);
		chamber.rotationPointX = 8F;
		chamber.rotationPointY = 8F;
		chamber.rotationPointZ = 8F;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks, int progress) {
		Engine engine = ((EngineTileEntity) tile).getEngine();

		if (engine != null) {
			render(engine.getEnergyStage(), engine.progress, engine.orientation, engine.getTextureFile(), (float) x, (float) y, (float) z);
		}
	}

	@Override
	public void render(RenderBlocks rb, Block block, int damage) {
		String baseTexture;
		switch (damage) {
		case 0:
			baseTexture = GearedEngine.TEXTURE;
			break;
		case 1:
			baseTexture = SteamEngine.TEXTURE;
			break;
		default:
			return; //Not sure what to draw
		}
		render(EnergyStage.Blue, 0.25F, EnumDirection.UP, baseTexture, -0.5F, -0.5F, -0.5F);
	}

	private void render(EnergyStage energy, float progress, EnumDirection orientation, String baseTexture, float x, float y, float z) {
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glTranslatef(x, y, z);

		float step;
		if (progress > 0.5) {
			step = 7.99F - (progress - 0.5F) * 2F * 7.99F;
		} else {
			step = progress * 2F * 7.99F;
		}

		float[] angle = {0, 0, 0};
		float[] translate = {0, 0, 0};
		switch (orientation) {
		case EAST:
			angle[2] = -MathHelper.PI_HALF_F;
			translate[0] = 1;
			break;
		case WEST:
			angle[2] = MathHelper.PI_HALF_F;
			translate[0] = -1;
			break;
		case UP:
			translate[1] = 1;
			break;
		case DOWN:
			angle[2] = (float) Math.PI;
			translate[1] = -1;
			break;
		case SOUTH:
			angle[0] = MathHelper.PI_HALF_F;
			translate[2] = 1;
			break;
		case NORTH:
			angle[0] = -MathHelper.PI_HALF_F;
			translate[2] = -1;
			break;
		case UNKNOWN:
			break;
		}

		box.rotateAngleX = angle[0];
		box.rotateAngleY = angle[1];
		box.rotateAngleZ = angle[2];

		trunk.rotateAngleX = angle[0];
		trunk.rotateAngleY = angle[1];
		trunk.rotateAngleZ = angle[2];

		movingBox.rotateAngleX = angle[0];
		movingBox.rotateAngleY = angle[1];
		movingBox.rotateAngleZ = angle[2];

		chamber.rotateAngleX = angle[0];
		chamber.rotateAngleY = angle[1];
		chamber.rotateAngleZ = angle[2];

		float scale = 1F / 16;
		float stepScale = step / 16;
		bindTextureByName(baseTexture);
		box.render(scale);

		GL11.glTranslatef(translate[0] * stepScale, translate[1] * stepScale, translate[2] * stepScale);
		movingBox.render(scale);
		GL11.glTranslatef(-translate[0] * stepScale, -translate[1] * stepScale, -translate[2] * stepScale);

		bindTextureByName("/textures/blocks/buildcraft/models/engineAccordion.png");
		float chamberStep = 2F / 16F;
		for (int i = 0; i <= step + 2; i += 2) {
			chamber.render(scale);
			GL11.glTranslatef(translate[0] * chamberStep, translate[1] * chamberStep, translate[2] * chamberStep);
		}
		for (int i = 0; i <= step + 2; i += 2) {
			GL11.glTranslatef(-translate[0] * chamberStep, -translate[1] * chamberStep, -translate[2] * chamberStep);
		}

		bindTextureByName(energy.texture);
		trunk.render(scale);

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

	@Override
	public void render(RenderBlocks rb, IBlockAccess world, int x, int y, int z, Block block) {
	}
}