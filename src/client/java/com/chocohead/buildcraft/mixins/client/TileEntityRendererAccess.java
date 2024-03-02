package com.chocohead.buildcraft.mixins.client;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.src.game.block.tileentity.TileEntity;
import net.minecraft.src.game.block.tileentity.TileEntityRenderer;
import net.minecraft.src.game.block.tileentity.TileEntitySpecialRenderer;

@Mixin(TileEntityRenderer.class)
public interface TileEntityRendererAccess {
	@Accessor
	Map<Class<? extends TileEntity>, TileEntitySpecialRenderer> getSpecialRendererMap();
}