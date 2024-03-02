package com.chocohead.buildcraft.mixins;

import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Streams;

import org.objectweb.asm.Opcodes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.src.game.IntHashMap;
import net.minecraft.src.game.IntHashMap.IntHashMapEntry;

import com.chocohead.buildcraft.IntHashMapExtras;

@Mixin(IntHashMap.class)
abstract class IntHashMapTinkering {
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "keySet:Ljava/util/Set;", opcode = Opcodes.PUTFIELD))
	private void skipCreation(IntHashMap<?> self, Set<Integer> keys) {
	}

	@Redirect(method = {"addKey", "removeObject"}, at = @At(value = "INVOKE", target = "Ljava/lang/Integer;valueOf(I)Ljava/lang/Integer;"))
	private Integer skipBoxing(int value) {
		return null;
	}

	@Redirect(method = "addKey", at = @At(value = "INVOKE", target = "Ljava/util/Set;add(Ljava/lang/Object;)Z"))
	private boolean skipAddingKey(Set<Integer> keys, Object key) {
		return true; //Ignored anyway
	}

	@Redirect(method = "removeObject", at = @At(value = "INVOKE", target = "Ljava/util/Set;remove(Ljava/lang/Object;)Z"))
	private boolean skipRemovingKey(Set<Integer> keys, Object key) {
		return true; //Also ignored
	}

	@Inject(method = "addKey", cancellable = true,
			at = @At(value = "FIELD", target = "Lnet/minecraft/src/game/IntHashMap$IntHashMapEntry;valueEntry:Ljava/lang/Object;", opcode = Opcodes.PUTFIELD, shift = Shift.AFTER))
	private void skipDuplicateEntries(int key, Object value, CallbackInfo call) {
		call.cancel();
	}

	@Inject(method = "getKeySet", at = @At("RETURN"), cancellable = true)
	private void getKeys(CallbackInfoReturnable<Set<Integer>> call) {
		if (call.getReturnValue() == null) {
			call.setReturnValue(Streams.stream(IntHashMapExtras.wrap((IntHashMap<?>) (Object) this).entrySetIterator()).map(IntHashMapEntry::getHash).collect(Collectors.toSet()));
		}
	}
}