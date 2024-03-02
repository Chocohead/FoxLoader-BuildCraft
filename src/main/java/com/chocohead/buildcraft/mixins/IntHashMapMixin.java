package com.chocohead.buildcraft.mixins;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.google.common.collect.Iterators;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.src.game.IntHashMap;
import net.minecraft.src.game.IntHashMap.IntHashMapEntry;

import com.chocohead.buildcraft.IntHashMapExtras;

@Mixin(IntHashMap.class)
abstract class IntHashMapMixin<T> implements IntHashMapExtras<T> {
	@Shadow
	private IntHashMapEntry<T>[] slots;
	@Shadow
	private int count;
	@Shadow
	private int versionStamp;

	@Shadow
	public abstract Object removeObject(int key);

	@Shadow
	public abstract IntHashMapEntry<T> removeEntry(int key);

	@Override
	public int size() {
		return count;
	}

	@Override
	public <E extends IntHashMapEntry<T> & IntHashMapEntryExtras<T>> Iterator<E> entrySetIterator() {
		return new Iterator<E>() {
			/** Next index of {@link #slots} to try in {@link #next} */
			private int slot = 0;
			/** The number of times {@link #next} has successfully returned something */
			private int returned = 0;
			/** The next entry to return from {@link #next}, given multiple in slot {@link #slot} */
			private E next = null;
			/** The last {@link #slot} value returned by {@link #next} (if <code>-1</code> no such value exists) */
			private int lastSlot = -1;
			/** The last value returned by {@link #next} ({@link #lastSlot} == <code>-1</code> no such value exists) */
			private int lastReturned;
			/** The expected value of {@link #versionStamp} given no map mutation is expected */
			private int expectedVersion = versionStamp;

			@Override
			public boolean hasNext() {
				return count > returned;
			}

			@Override
			public E next() {
				checkForComodification();

				try {
					E out;
					if (next == null) {
						int i = slot;
						do {
							out = IntHashMapEntryExtras.wrap(slots[i++]);
						} while (out == null);
						lastSlot = i - 1;
					} else {
						out = next;
						lastSlot = slot;
					}
					lastReturned = out.getHash();
					next = IntHashMapEntryExtras.wrap(out.nextEntry);
					slot = next == null ? lastSlot + 1 : lastSlot;
					returned++;
					return out;
				} catch (IndexOutOfBoundsException e) {
					checkForComodification();
					throw new NoSuchElementException();
				}
			}

			@Override
			public void remove() {
				if (lastSlot < 0) throw new IllegalStateException();
				checkForComodification();

				try {
					if (slots[lastSlot].getHash() == lastReturned && next == null) {
						removeObject(lastReturned); //Only entry in the slot
					} else {
						removeEntry(lastReturned); //One of the entries in the slot
					}
					returned--;
					lastSlot = -1;
					lastReturned = 0;
					expectedVersion = versionStamp;
				} catch (IndexOutOfBoundsException e) {
					throw new ConcurrentModificationException();
				}
			}

			private void checkForComodification() {
				if (versionStamp != expectedVersion) {
					throw new ConcurrentModificationException();
				}
			}
		};
	}

	@Override
	public Iterable<T> values() {
		return () -> Iterators.transform(entrySetIterator(), IntHashMapEntryExtras::getValue);
	}

	@Mixin(IntHashMapEntry.class)
	abstract static class IntHashMapEntryMixin<T> implements IntHashMapEntryExtras<T> {
		/*@Shadow
		T valueEntry;

		@Override
		public T getValue() {
			return valueEntry;
		}*/
	}
}