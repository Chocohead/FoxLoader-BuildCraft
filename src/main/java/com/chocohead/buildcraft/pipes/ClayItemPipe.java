package com.chocohead.buildcraft.pipes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.common.block.icon.Icon;
import net.minecraft.common.block.icon.IconRegister;
import net.minecraft.common.util.Direction.EnumDirection;
import net.minecraft.common.block.tileentity.TileEntity;

import com.chocohead.buildcraft.api.EntityPassiveItem;
import com.chocohead.buildcraft.api.IPipeEntry;
import com.chocohead.buildcraft.api.Position;
import com.chocohead.buildcraft.pipes.logic.ClayPipeLogic;
import com.chocohead.buildcraft.pipes.transport.ItemPipeTransport;
import com.chocohead.buildcraft.pipes.transport.SpecialItemTransportPipe;

public class ClayItemPipe extends Pipe<ItemPipeTransport> implements SpecialItemTransportPipe {
	public static final String ID = "clayItem";
	private static Icon texture;

	public ClayItemPipe() {
		super(new ItemPipeTransport(), new ClayPipeLogic(), ID);
	}

	@Override
	public void registerIcons(IconRegister register) {
		texture = register.registerIcon("buildcraft/clayPipe");
	}

	@Override
	public Icon getBlockTexture() {
		return texture;
	}

	@Override
	public String getName() {
		return "item.buildcraft.itemPipe.clay";
	}

	@Override
	public List<EnumDirection> filterPossibleMovements(List<EnumDirection> movements, Position pos, EntityPassiveItem item) {
		List<EnumDirection> nonPipesList = new ArrayList<>();
		List<EnumDirection> pipesList = new ArrayList<>();

		for (EnumDirection direction : movements) {
			Position newPos = new Position(pos);
			newPos.orientation = direction;
			newPos.moveForwards(1);

			TileEntity entity = world.getBlockTileEntity((int) pos.x, (int) pos.y, (int) pos.z);
			if (entity instanceof IPipeEntry) {
				pipesList.add(direction);
			} else {
				nonPipesList.add(direction);
			}
		}

		return nonPipesList.isEmpty() ? pipesList : nonPipesList;
	}
}