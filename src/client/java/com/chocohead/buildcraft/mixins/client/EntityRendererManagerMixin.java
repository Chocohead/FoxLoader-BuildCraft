package com.chocohead.buildcraft.mixins.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.entity.EntityRendererManager;

import com.chocohead.buildcraft.events.EntityRendererRegistrationEvent;

@Mixin(EntityRendererManager.class)
abstract class EntityRendererManagerMixin {
	@Inject(method = "<init>", at = @At("RETURN"))
	private void onRefresh(CallbackInfo call) {
		EntityRendererRegistrationEvent.REGISTER_EVENT.callEvent(new EntityRendererRegistrationEvent((EntityRendererManager) (Object) this));
	}
}