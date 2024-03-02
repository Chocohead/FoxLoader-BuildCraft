package com.chocohead.buildcraft.mixins.client;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.src.client.renderer.entity.Render;
import net.minecraft.src.client.renderer.entity.RenderManager;
import net.minecraft.src.game.entity.Entity;

@Mixin(RenderManager.class)
public interface RenderManagerAccess {
	@Accessor
	Map<Class<? extends Entity>, Render> getEntityRenderMap();
}