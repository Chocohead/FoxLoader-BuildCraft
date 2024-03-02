package com.chocohead.buildcraft.pipes.transport;

import java.util.List;

import net.minecraft.src.game.Direction.EnumDirection;

import com.chocohead.buildcraft.Utils;
import com.chocohead.buildcraft.api.EntityPassiveItem;
import com.chocohead.buildcraft.api.Position;

public interface SpecialItemTransportPipe {
	default List<EnumDirection> filterPossibleMovements(List<EnumDirection> movements, Position pos, EntityPassiveItem item) {
		return movements;
	}

	default void entityEntered(EntityPassiveItem item, EnumDirection orientation) {
	}

	default void adjustSpeed(EntityPassiveItem item) {
		if (item.speed > Utils.normalPipeSpeed) {
			item.speed = item.speed - Utils.normalPipeSpeed;
		}

		if (item.speed < Utils.normalPipeSpeed) {
			item.speed = Utils.normalPipeSpeed;
		}
	}
}