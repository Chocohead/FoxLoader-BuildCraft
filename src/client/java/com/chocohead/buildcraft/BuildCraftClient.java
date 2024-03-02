package com.chocohead.buildcraft;

import net.minecraft.src.client.renderer.entity.Render;
import net.minecraft.src.client.renderer.entity.RenderManager;
import net.minecraft.src.game.block.tileentity.TileEntity;
import net.minecraft.src.game.block.tileentity.TileEntityRenderer;
import net.minecraft.src.game.block.tileentity.TileEntitySpecialRenderer;
import net.minecraft.src.game.entity.Entity;
import net.minecraft.src.game.item.ItemStack;

import com.fox2code.foxloader.client.CreativeItems;
import com.fox2code.foxloader.loader.ClientMod;
import com.fox2code.foxloader.registry.RegisteredItemStack;

import com.chocohead.buildcraft.blocks.EngineTileEntity;
import com.chocohead.buildcraft.blocks.TransportPipeTileEntity;
import com.chocohead.buildcraft.client.BlockEntityRenderer;
import com.chocohead.buildcraft.client.EngineRenderer;
import com.chocohead.buildcraft.client.NothingRenderer;
import com.chocohead.buildcraft.client.TransportPipeRenderer;
import com.chocohead.buildcraft.entities.BlockEntity;
import com.chocohead.buildcraft.entities.QuarryArmEntity;
import com.chocohead.buildcraft.client.PipeRenderer;
import com.chocohead.buildcraft.client.RenderTypeRegistry;
import com.chocohead.buildcraft.mixins.client.RenderManagerAccess;
import com.chocohead.buildcraft.mixins.client.TileEntityRendererAccess;

public class BuildCraftClient extends BuildCraft implements ClientMod {
	private static void registerEntityRenderer(Class<? extends Entity> entity, Render renderer) {
		((RenderManagerAccess) RenderManager.instance).getEntityRenderMap().put(entity, renderer);
		renderer.setRenderManager(RenderManager.instance);
	}

	private static void registerTESR(Class<? extends TileEntity> tile, TileEntitySpecialRenderer renderer) {
		((TileEntityRendererAccess) TileEntityRenderer.instance).getSpecialRendererMap().put(tile, renderer);
    	renderer.setTileEntityRenderer(TileEntityRenderer.instance);
	}

	private static ItemStack toStack(RegisteredItemStack stack, int damage) {
		stack.setRegisteredDamage(damage);
		return (ItemStack) stack;
	}

	@Override
	public void onPreInit() {
		super.onPreInit();

		for (int i = 1; i <= 8; i++) {
			CreativeItems.addToCreativeInventory(toStack(transportPipe.newRegisteredItemStack(), i));
		}
		CreativeItems.addToCreativeInventory(toStack(engine.newRegisteredItemStack(), 1)); 
	}

    @Override
    public void onInit() {
        super.onInit();

        registerEntityRenderer(BlockEntity.class, new BlockEntityRenderer());
        registerEntityRenderer(QuarryArmEntity.class, new NothingRenderer());

        PipeRenderer.renderID = RenderTypeRegistry.register(new PipeRenderer());
        registerTESR(TransportPipeTileEntity.class, new TransportPipeRenderer());

        EngineRenderer engineRenderer = new EngineRenderer();
        EngineRenderer.renderID = RenderTypeRegistry.register(engineRenderer);
        registerTESR(EngineTileEntity.class, engineRenderer);
    }
}
