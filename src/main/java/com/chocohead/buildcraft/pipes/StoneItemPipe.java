package com.chocohead.buildcraft.pipes;

import net.minecraft.src.client.renderer.block.icon.Icon;
import net.minecraft.src.client.renderer.block.icon.IconRegister;

import com.chocohead.buildcraft.Utils;
import com.chocohead.buildcraft.api.EntityPassiveItem;
import com.chocohead.buildcraft.pipes.logic.StonePipeLogic;
import com.chocohead.buildcraft.pipes.transport.ItemPipeTransport;
import com.chocohead.buildcraft.pipes.transport.SpecialItemTransportPipe;

public class StoneItemPipe extends Pipe<ItemPipeTransport> implements SpecialItemTransportPipe {
	public static final String ID = "stoneItem";
	private static Icon texture;

	public StoneItemPipe() {
		super(new ItemPipeTransport(), new StonePipeLogic(), ID);
	}

	@Override
	public void registerIcons(IconRegister register) {
		texture = register.registerIcon("buildcraft/stonePipe");
	}

	@Override
	public Icon getBlockTexture() {
		return texture;
	}

	@Override
	public String getName() {
		return "item.buildcraft.itemPipe.stone";
	}

	@Override
	public void adjustSpeed(EntityPassiveItem item) {
		if (item.speed > Utils.normalPipeSpeed) {
			item.speed = item.speed - Utils.normalPipeSpeed / 2F;
		}

		if (item.speed < Utils.normalPipeSpeed) {
			item.speed = Utils.normalPipeSpeed;
		}
	}
}