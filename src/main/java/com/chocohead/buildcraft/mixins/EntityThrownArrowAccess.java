package com.chocohead.buildcraft.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.common.entity.projectile.EntityThrownArrow;
import net.minecraft.common.item.ItemStack;

@Mixin(EntityThrownArrow.class)
public interface EntityThrownArrowAccess {
	@Invoker
	ItemStack callGetArrowItemstack();
}