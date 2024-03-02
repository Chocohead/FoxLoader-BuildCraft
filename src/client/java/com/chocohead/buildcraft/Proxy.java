package com.chocohead.buildcraft;

import net.minecraft.client.Minecraft;
import net.minecraft.src.game.block.tileentity.TileEntity;
import net.minecraft.src.game.entity.Entity;
import net.minecraft.src.game.entity.other.EntityItem;
import net.minecraft.src.game.entity.player.EntityPlayer;
import net.minecraft.src.game.level.World;
import net.minecraft.src.game.level.WorldClient;

import com.chocohead.buildcraft.blocks.EngineTileEntity;
import com.chocohead.buildcraft.blocks.TransportPipeTileEntity;
import com.chocohead.buildcraft.blocks.gui.DiamondPipeGUI;
import com.chocohead.buildcraft.blocks.gui.SteamEngineGUI;
import com.chocohead.buildcraft.client.TileEntityPickupFX;

public class Proxy {
	public static void removeEntity(Entity entity) {
		entity.setEntityDead();

		if (entity.worldObj instanceof WorldClient) {
			((WorldClient) entity.worldObj).removeEntityFromWorld(entity.entityId);
		}
	}

	public static void pickupItem(World world, EntityItem item, TileEntity tile) {
		Minecraft.getInstance().effectRenderer.addEffect(new TileEntityPickupFX(world, item, tile));
	}

	public static void displayDiamondPipeGUI(EntityPlayer player, TransportPipeTileEntity tile) {
		assert !player.worldObj.multiplayerWorld;
		Minecraft.getInstance().displayGuiScreen(new DiamondPipeGUI(player.inventory, tile));
	}

	public static void displaySteamEngineGUI(EntityPlayer player, EngineTileEntity tile) {
		assert !player.worldObj.multiplayerWorld;
		Minecraft.getInstance().displayGuiScreen(new SteamEngineGUI(player.inventory, tile));
	}
}