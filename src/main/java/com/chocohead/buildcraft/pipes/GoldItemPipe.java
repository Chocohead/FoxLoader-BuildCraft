package com.chocohead.buildcraft.pipes;

import net.minecraft.src.client.renderer.block.icon.Icon;
import net.minecraft.src.client.renderer.block.icon.IconRegister;
import net.minecraft.src.game.Direction.EnumDirection;
import net.minecraft.src.game.block.tileentity.TileEntity;

import com.chocohead.buildcraft.Utils;
import com.chocohead.buildcraft.api.EntityPassiveItem;
import com.chocohead.buildcraft.blocks.PipeTileEntity;
import com.chocohead.buildcraft.pipes.logic.GoldPipeLogic;
import com.chocohead.buildcraft.pipes.transport.ItemPipeTransport;
import com.chocohead.buildcraft.pipes.transport.SpecialItemTransportPipe;

public class GoldItemPipe extends Pipe<ItemPipeTransport> implements SpecialItemTransportPipe {
	public static final String ID = "goldItem";
	private static Icon normalTexture, poweredTexture;
	private static Icon activeTexture;

	public GoldItemPipe() {
		super(new ItemPipeTransport(), new GoldPipeLogic(), ID);
	}

	@Override
	public void registerIcons(IconRegister register) {
		activeTexture = normalTexture = register.registerIcon("buildcraft/goldPipe");
		poweredTexture = register.registerIcon("buildcraft/poweredGoldPipe");
	}

	@Override
	public void prepareTextureFor(EnumDirection connection) {
		if (world != null && world.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) {
			activeTexture = poweredTexture;
		} else {
			activeTexture = normalTexture;
		}
	}

	@Override
	public Icon getBlockTexture() {
		return activeTexture;
	}

	@Override
	public String getName() {
		return "item.buildcraft.itemPipe.gold";
	}

	@Override
	public boolean isPipeConnected(TileEntity tile) {
		if (!super.isPipeConnected(tile)) return false;
		if (tile instanceof PipeTileEntity<?>) {
			return !(((PipeTileEntity<?>) tile).getPipe().logic instanceof GoldPipeLogic);
		}
		return true;
	}

	@Override
	public void entityEntered(EntityPassiveItem item, EnumDirection orientation) {
		if (world.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) {
			item.speed = Utils.normalPipeSpeed * 20F;
		}
	}
}