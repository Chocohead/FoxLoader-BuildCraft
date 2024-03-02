package com.chocohead.buildcraft;

import java.util.Iterator;

import net.minecraft.src.game.IntHashMap;
import net.minecraft.src.game.IntHashMap.IntHashMapEntry;

public interface IntHashMapExtras<T> {
	interface IntHashMapEntryExtras<T> {
		@SuppressWarnings("unchecked")
		static <T, E extends IntHashMapEntry<T> & IntHashMapEntryExtras<T>> E wrap(IntHashMapEntry<T> entry) {
			return (E) entry;
		}

		T getValue();
	}

	@SuppressWarnings("unchecked")
	static <T> IntHashMapExtras<T> wrap(IntHashMap<T> map) {
		return (IntHashMapExtras<T>) (Object) map;
	}

	int size();

	<E extends IntHashMapEntry<T> & IntHashMapEntryExtras<T>> Iterator<E> entrySetIterator();

	Iterable<T> values();
}