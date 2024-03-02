package com.chocohead.buildcraft.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.src.game.block.tileentity.TileEntity;

@Mixin(TileEntity.class)
public interface TileEntityAccess {
	@Invoker
	static void callAddMapping(Class<? extends TileEntity> type, String name)  {
		throw new AssertionError();
	}
}