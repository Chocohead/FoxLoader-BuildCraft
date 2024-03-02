package com.chocohead.buildcraft.mixins.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.src.client.renderer.block.TextureManager;

@Mixin(TextureManager.class)
abstract class TextureManagerMixin {
	@Inject(method = "getBasename", at = @At("HEAD"), cancellable = true)
	private void fixBuildCraftPrefix(String path, CallbackInfoReturnable<String> call) {
		int index = path.indexOf("/buildcraft/");
		if (index > -1) {
			String tail = path.substring(index + 1);
			call.setReturnValue(tail.substring(0, tail.indexOf('.')));
		}
	}
}
