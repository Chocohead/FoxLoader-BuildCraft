package com.chocohead.buildcraft.pipes;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;

import net.minecraft.src.client.renderer.block.icon.Icon;
import net.minecraft.src.client.renderer.block.icon.IconRegister;
import net.minecraft.src.game.Direction.EnumDirection;
import net.minecraft.src.game.item.ItemStack;

import com.chocohead.buildcraft.api.EntityPassiveItem;
import com.chocohead.buildcraft.api.Position;
import com.chocohead.buildcraft.pipes.logic.DiamondPipeLogic;
import com.chocohead.buildcraft.pipes.transport.ItemPipeTransport;
import com.chocohead.buildcraft.pipes.transport.SpecialItemTransportPipe;

public class DiamondItemPipe extends Pipe<ItemPipeTransport> implements SpecialItemTransportPipe {
	public static final String ID = "diamondItem";
	private static final Map<EnumDirection, Icon> TEXTURES = new EnumMap<>(EnumDirection.class);
	private static Icon activeTexture;

	public DiamondItemPipe() {
		super(new ItemPipeTransport(), new DiamondPipeLogic(), ID);
	}

	@Override
	public void registerIcons(IconRegister register) {
		TEXTURES.put(EnumDirection.UNKNOWN, activeTexture = register.registerIcon("buildcraft/diamondPipe"));
		for (EnumDirection direction : EnumDirection.VALID_DIRECTIONS) {
			TEXTURES.put(direction, register.registerIcon("buildcraft/diamondPipe" + WordUtils.capitalize(direction.name())));
		}
	}

	@Override
	public void prepareTextureFor(EnumDirection connection) {
		activeTexture = TEXTURES.get(connection);
	}

	@Override
	public Icon getBlockTexture() {
		return activeTexture;
	}

	@Override
	public String getName() {
		return "item.buildcraft.itemPipe.diamond";
	}

	@Override
	public List<EnumDirection> filterPossibleMovements(List<EnumDirection> movements, Position pos, EntityPassiveItem item) {
		List<EnumDirection> filteredDirections = new ArrayList<>();
		List<EnumDirection> defaultDirections = new ArrayList<>();

		for (EnumDirection dir : movements) {
			boolean foundFilter = false;

			for (int slot = 0; slot < 9; ++slot) {
				ItemStack stack = logic.getStackInSlot(dir.ordinal() * 9 + slot);
				foundFilter |= stack != null;

				if (stack != null && stack.itemID == item.item.itemID) {
					if (item.item.getItem().isDamagable() || stack.getItemDamage() == item.item.getItemDamage()) {
						filteredDirections.add(dir);
					}
				}
			}

			if (!foundFilter) {
				defaultDirections.add(dir);
			}
		}

		return !filteredDirections.isEmpty() ? filteredDirections : defaultDirections;
	}
}