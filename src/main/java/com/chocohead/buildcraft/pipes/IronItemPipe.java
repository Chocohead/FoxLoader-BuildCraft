package com.chocohead.buildcraft.pipes;

import net.minecraft.src.client.renderer.block.icon.Icon;
import net.minecraft.src.client.renderer.block.icon.IconRegister;
import net.minecraft.src.game.Direction.EnumDirection;

import com.chocohead.buildcraft.pipes.logic.IronPipeLogic;
import com.chocohead.buildcraft.pipes.transport.ItemPipeTransport;

public class IronItemPipe extends Pipe<ItemPipeTransport> {
	public static final String ID = "ironItem";
	private static Icon openTexture, closedTexture;
	private static Icon activeTexture;

	public IronItemPipe() {
		super(new ItemPipeTransport(), new IronPipeLogic(), ID);
	}

	@Override
	public void registerIcons(IconRegister register) {
		activeTexture = openTexture = register.registerIcon("buildcraft/ironPipe");
		closedTexture = register.registerIcon("buildcraft/closedIronPipe");
	}

	@Override
	public void prepareTextureFor(EnumDirection connection) {
		if (connection == EnumDirection.UNKNOWN || ((IronPipeLogic) logic).getExit() == connection) {
			activeTexture = openTexture;
		} else {
			activeTexture = closedTexture;
		}
	}

	@Override
	public Icon getBlockTexture() {
		return activeTexture;
	}

	@Override
	public String getName() {
		return "item.buildcraft.itemPipe.iron";
	}
}