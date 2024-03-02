package com.chocohead.buildcraft.mixins.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.src.client.renderer.RenderBlocks;
import net.minecraft.src.game.block.Block;
import net.minecraft.src.game.level.IBlockAccess;

import com.chocohead.buildcraft.client.BlockEntityRenderer.BlockEntityRenderBlocks;
import com.chocohead.buildcraft.client.RenderTypeRegistry;
import com.chocohead.buildcraft.mixins.Plugin;

@Mixin(RenderBlocks.class)
abstract class RenderBlocksMixin implements BlockEntityRenderBlocks {
	@Shadow
	private IBlockAccess blockAccess;
	@Unique
	private boolean blockEntityRenderer;

	@Inject(method = "renderBlockByRenderType", at = @At(value = "CONSTANT", args = "stringValue=" + Plugin.RENDER_BLOCKS_MAGIC), cancellable = true)
	private void hookBuildCraftRenderTypes(Block block, int x, int y, int z, CallbackInfoReturnable<Boolean> call, @Local(ordinal = 3) int type) {
		if (RenderTypeRegistry.render((RenderBlocks) (Object) this, blockAccess, x, y, z, block, type)) call.setReturnValue(true);
	}

	@Inject(method = "renderItemIn3d", at = @At(value = "CONSTANT", args = "intValue=16"), cancellable = true)
	private static void hook3DBuildCraftRenderTypes(int type, CallbackInfoReturnable<Boolean> call) {
		if (RenderTypeRegistry.renderItemIn3D(type)) call.setReturnValue(true);
	}

	@Inject(method = "renderBlockOnInventory", at = @At(value = "JUMP", ordinal = 0), slice = @Slice(from = @At(value = "INVOKE:LAST", target = "Lnet/minecraft/src/game/block/Block;setBlockBounds(FFFFFF)V")), cancellable = true)
	private void hookBuildCraftRenderTypes(Block block, int damage, float light, CallbackInfo call, @Local(ordinal = 1) int type) {
		RenderTypeRegistry.render((RenderBlocks) (Object) this, block, damage, type);
	}

	@WrapWithCondition(method = "renderBlockFallingSand", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/block/Block;setBlockBounds(FFFFFF)V", ordinal = 1))
	private boolean pretendToBeSand(Block block, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		return !blockEntityRenderer;
	}

	@Override
	public void setBlockEntityRenderer(boolean flag) {
		blockEntityRenderer = flag;
	}
}