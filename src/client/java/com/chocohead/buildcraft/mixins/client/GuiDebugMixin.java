package com.chocohead.buildcraft.mixins.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiDebug;
import net.minecraft.common.block.tileentity.TileEntity;
import net.minecraft.common.util.ChatColors;

import com.chocohead.buildcraft.api.IPowerReceptor;
import com.chocohead.buildcraft.api.PowerProvider;
import com.chocohead.buildcraft.blocks.EngineTileEntity;
import com.chocohead.buildcraft.engines.Engine;

@Mixin(GuiDebug.class)
abstract class GuiDebugMixin extends Gui {
	@Shadow
	private Minecraft mc;

	@Inject(method = "drawScreen", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=block: " + ChatColors.GRAY)),
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiDebug;drawStringWithBg(Lnet/minecraft/client/gui/FontRenderer;Ljava/lang/String;FFI)V", shift = Shift.AFTER, ordinal = 0))
	private void addPowerInfo(CallbackInfo call, @Local FontRenderer font) {
		TileEntity tile = mc.theWorld.getBlockTileEntity(mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ);

		if (tile instanceof EngineTileEntity) {
			Engine engine = ((EngineTileEntity) tile).getEngine();

			String text = String.format("power stored: " + GRAY + "%d MJ (%.2f%%)", engine.energy, (engine.energy / (double) engine.maxEnergy) * 100);
	        drawStringWithBg(font, text, 2, 214, 0xFFFFFF);
		} else if (tile instanceof IPowerReceptor) {
			PowerProvider power = ((IPowerReceptor) tile).getPowerProvider();

			if (power != null) {
				String text = String.format("power stored: " + GRAY + "%d MJ (%.2f%%)", power.energyStored, (power.energyStored / (double) power.maxEnergyStored) * 100);
		        drawStringWithBg(font, text, 2, 214, 0xFFFFFF);
			}
		}
	}
}