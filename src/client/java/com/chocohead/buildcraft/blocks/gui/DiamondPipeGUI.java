package com.chocohead.buildcraft.blocks.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiContainer;
import net.minecraft.common.util.i18n.StringTranslate;
import net.minecraft.common.entity.inventory.IInventory;
import net.minecraft.common.entity.player.InventoryPlayer;

public class DiamondPipeGUI extends GuiContainer {
	private final IInventory playerInventory;
	private final IInventory filterInventory;

	public DiamondPipeGUI(InventoryPlayer playerInventory, IInventory filterInventory) {
		super(new DiamondPipeContainer(playerInventory, filterInventory));

		this.playerInventory = playerInventory;
		this.filterInventory = filterInventory;
		xSize = 175;
		ySize = 225;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f) {
		GL11.glColor4f(1F, 1F, 1F, 1F);
		mc.renderEngine.bindTexture(mc.renderEngine.getTexture("/textures/gui/buildcraft/diamondPipe.png"));

		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer() {
		fontRenderer.drawString(StringTranslate.getInstance().translateKey(filterInventory.getInvName()), 8, 6, 0x404040);
		fontRenderer.drawString(StringTranslate.getInstance().translateKey(playerInventory.getInvName()), 8, ySize - 97, 0x404040);
	}
}