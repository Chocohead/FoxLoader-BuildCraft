package com.chocohead.buildcraft.pipes;

import net.minecraft.src.client.renderer.block.icon.Icon;
import net.minecraft.src.client.renderer.block.icon.IconRegister;

import com.chocohead.buildcraft.pipes.logic.CobblePipeLogic;
import com.chocohead.buildcraft.pipes.transport.ItemPipeTransport;

public class CobbleItemPipe extends Pipe<ItemPipeTransport> {
	public static final String ID = "cobbleItem";
	private static Icon texture;

	public CobbleItemPipe() {
		super(new ItemPipeTransport(), new CobblePipeLogic(), ID);
	}

	@Override
	public void registerIcons(IconRegister register) {
		texture = register.registerIcon("buildcraft/cobblePipe");
	}

	@Override
	public Icon getBlockTexture() {
		return texture;
	}

	@Override
	public String getName() {
		return "item.buildcraft.itemPipe.cobble";
	}
}