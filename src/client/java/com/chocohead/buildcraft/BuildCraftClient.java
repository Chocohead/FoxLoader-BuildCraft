package com.chocohead.buildcraft;

import net.minecraft.client.renderer.world.RenderBlocks;
import net.minecraft.common.block.Block;
import net.minecraft.common.block.tileentity.TileEntity;
import net.minecraft.client.renderer.block.tileentity.TileEntityRenderManager;
import net.minecraft.client.renderer.block.tileentity.TileEntityRenderer;
import net.minecraft.common.world.BlockAccess;

import com.fox2code.foxevents.EventHandler;

import com.chocohead.buildcraft.blocks.EngineBlock;
import com.chocohead.buildcraft.blocks.EngineTileEntity;
import com.chocohead.buildcraft.blocks.PipeBlock;
import com.chocohead.buildcraft.blocks.TransportPipeTileEntity;
import com.chocohead.buildcraft.client.BlockEntityRenderer;
import com.chocohead.buildcraft.client.BlockRenderPlus;
import com.chocohead.buildcraft.client.EngineRenderer;
import com.chocohead.buildcraft.client.NothingRenderer;
import com.chocohead.buildcraft.client.PipeRenderer;
import com.chocohead.buildcraft.client.TransportPipeRenderer;
import com.chocohead.buildcraft.entities.BlockEntity;
import com.chocohead.buildcraft.entities.QuarryArmEntity;
import com.chocohead.buildcraft.events.EntityRendererRegistrationEvent;

public class BuildCraftClient extends BuildCraft {
	private static <T extends TileEntity> void registerTESR(Class<T> tile, TileEntityRenderer<T> renderer) {
		TileEntityRenderManager.instance.addTileEntityRenderer(tile, renderer);
	}

	@EventHandler
	public void onEntityRendererRefresh(EntityRendererRegistrationEvent event) {
		event.registerEntityRenderer(BlockEntity.class, new BlockEntityRenderer());
		event.registerEntityRenderer(QuarryArmEntity.class, new NothingRenderer());
	}

    @Override
    public void onInit() {
        super.onInit();

        PipeBlock.renderID = new PipeRenderer().getAssignedID();
        registerTESR(TransportPipeTileEntity.class, new TransportPipeRenderer());

        EngineRenderer engineRenderer = new EngineRenderer();
        EngineBlock.renderID = new BlockRenderPlus(true) {
			@Override
			public boolean renderBlock(RenderBlocks renderBlocks, BlockAccess blockAccess, Block block, int x, int y, int z) {
				return true; //All done in the TESR
			}

			@Override
			public void render(RenderBlocks rb, Block block, int damage) {
				engineRenderer.render(rb, block, damage);
			}
		}.getAssignedID();
        registerTESR(EngineTileEntity.class, engineRenderer);
    }
}
