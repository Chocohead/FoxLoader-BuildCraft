package com.chocohead.buildcraft.pipes;

import net.minecraft.common.block.icon.Icon;
import net.minecraft.common.block.icon.IconRegister;

import com.chocohead.buildcraft.pipes.logic.SandstonePipeLogic;
import com.chocohead.buildcraft.pipes.transport.ItemPipeTransport;

public class SandstoneItemPipe extends Pipe<ItemPipeTransport> {
	public static final String ID = "sandstoneItem";
	private static Icon texture;

	public SandstoneItemPipe() {
		super(new ItemPipeTransport(), new SandstonePipeLogic(), ID);
	}

	@Override
	public void registerIcons(IconRegister register) {
		texture = register.registerIcon("buildcraft/sandstonePipe");
	}

	@Override
	public Icon getBlockTexture() {
		return texture;
	}

	@Override
	public String getName() {
		return "item.buildcraft.itemPipe.sandstone";
	}
}