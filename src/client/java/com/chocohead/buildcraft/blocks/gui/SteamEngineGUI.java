package com.chocohead.buildcraft.blocks.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.client.gui.GuiContainer;
import net.minecraft.src.client.gui.StringTranslate;
import net.minecraft.src.client.inventory.IInventory;
import net.minecraft.src.game.entity.player.InventoryPlayer;

import com.chocohead.buildcraft.blocks.EngineTileEntity;

public class SteamEngineGUI extends GuiContainer {
	private final IInventory player;
	private final EngineTileEntity tile;

	public SteamEngineGUI(InventoryPlayer player, EngineTileEntity tile) {
		super(new EngineContainer(player, tile));

		this.player = player;
		this.tile = tile;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f) {
		GL11.glColor4f(1F, 1F, 1F, 1F);
		mc.renderEngine.bindTexture(mc.renderEngine.getTexture("/textures/gui/buildcraft/steamEngine.png"));

		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

		int burnTime = tile.getEngine().getScaledBurnTime(12);
		if (burnTime > 0) {
			drawTexturedModalRect(x + 80, (y + 24 + 12) - burnTime, 176, 12 - burnTime, 14, burnTime + 2);
		}		
	}

	@Override
	protected void drawGuiContainerForegroundLayer() {
		fontRenderer.drawString(StringTranslate.getInstance().translateKey(tile.getInvName()), 60, 6, 0x404040);
		fontRenderer.drawString(StringTranslate.getInstance().translateKey(player.getInvName()), 8, (ySize - 96) + 2, 0x404040);
	}
}