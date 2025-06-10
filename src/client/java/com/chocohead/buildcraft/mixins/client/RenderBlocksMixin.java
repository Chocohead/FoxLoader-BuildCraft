package com.chocohead.buildcraft.mixins.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.renderer.world.RenderBlocks;
import net.minecraft.common.block.Block;

import com.chocohead.buildcraft.client.BlockEntityRenderer.BlockEntityRenderBlocks;
import com.chocohead.buildcraft.client.BlockRenderPlus;

@Mixin(RenderBlocks.class)
abstract class RenderBlocksMixin implements BlockEntityRenderBlocks {
	@Unique
	private boolean blockEntityRenderer;

	@Inject(method = "renderBlockOnInventory", at = @At(value = "CONSTANT", args = "intValue=1", ordinal = 0), cancellable = true)
	private void hookBuildCraftRenderTypes(Block block, int damage, float light, CallbackInfo call, @Local(ordinal = 1) int type) {
		BlockRenderPlus.render((RenderBlocks) (Object) this, block, damage, type);
	}

	@WrapWithCondition(method = "renderBlockFallingSand", at = @At(value = "INVOKE", target = "Lnet/minecraft/common/block/Block;setBlockBounds(FFFFFF)V", ordinal = 1))
	private boolean pretendToBeSand(Block block, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		return !blockEntityRenderer;
	}

	@Override
	public void setBlockEntityRenderer(boolean flag) {
		blockEntityRenderer = flag;
	}
}