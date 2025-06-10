package com.chocohead.buildcraft;

import net.minecraft.common.block.Block;
import net.minecraft.common.block.Blocks;
import net.minecraft.common.item.Item;
import net.minecraft.common.item.ItemStack;
import net.minecraft.common.item.Items;
import net.minecraft.common.recipe.CraftingManager;

public class Recipes {
	private static void registerRecipe(ItemStack stack, Object... shape) {
		assert shape.length >= 3 && shape[0] instanceof String;
		CraftingManager.getInstance().addRecipe(stack, shape);
	}

	private static void registerRecipe(Item item, Object... shape) {
		registerRecipe(new ItemStack(item), shape);
	}

	private static void registerRecipe(Block block, Object... shape) {
		registerRecipe(new ItemStack(block), shape);
	}

	public static void register() {
		registerRecipe(BuildCraft.wrench, "I I", " G ", " I ", 'I', Items.IRON_INGOT, 'G', BuildCraft.stoneGear);
		registerGears();

		registerRecipe(BuildCraft.pipeWaterproof, "D ", " ", 'D', new ItemStack(Items.DYE_POWDER, 1, 4));
		registerPipes();

		registerEngines();

		ItemStack stonePipe = new ItemStack(BuildCraft.transportPipe, 1, 2);
		registerRecipe(BuildCraft.quarry, "ISI", "GIG", "DPD", 'I', BuildCraft.ironGear, 'S', stonePipe, 'G', BuildCraft.goldGear, 'D', BuildCraft.diamondGear, 'P', Items.DIAMOND_PICKAXE);
	}

	private static void registerGears() {
		registerRecipe(BuildCraft.woodenGear, " S ", "S S", " S ", 'S', Items.STICK);
		registerRecipe(BuildCraft.stoneGear, " S ", "SGS", " S ", 'S', Blocks.COBBLESTONE, 'G', BuildCraft.woodenGear);
		registerRecipe(BuildCraft.ironGear, " I ", "IGI", " I ", 'I', Items.IRON_INGOT, 'G', BuildCraft.stoneGear);
		registerRecipe(BuildCraft.goldGear, " I ", "IGI", " I ", 'I', Items.GOLD_INGOT, 'G', BuildCraft.ironGear);
		registerRecipe(BuildCraft.diamondGear, " D ", "DGD", " D ", 'D', Items.DIAMOND, 'G', BuildCraft.goldGear);
	}

	private static void registerPipes() {
		Object[] materials = {Blocks.PLANKS, Blocks.COBBLESTONE, Blocks.STONE, Blocks.SANDSTONE, Blocks.CLAY, Items.IRON_INGOT, Items.GOLD_INGOT, Items.DIAMOND, Blocks.OBSIDIAN};
		for (int i = 0; i < materials.length; i++) {
			ItemStack stack = new ItemStack(BuildCraft.transportPipe, 8, i);
			registerRecipe(stack, "MGM", 'G', Blocks.GLASS, 'M', materials[i]);
		}
	}

	private static void registerEngines() {
		ItemStack engine = new ItemStack(BuildCraft.engine, 1, 0);
		registerRecipe(engine, "WWW", " g ", "GPG", 'W', Blocks.PLANKS, 'g', Blocks.GLASS, 'G', BuildCraft.woodenGear, 'P', Blocks.PISTON_BASE);

		engine = new ItemStack(BuildCraft.engine, 1, 1);
		registerRecipe(engine, "CCC", " g ", "GPG", 'C', Blocks.COBBLESTONE, 'g', Blocks.GLASS, 'G', BuildCraft.stoneGear, 'P', Blocks.PISTON_BASE);
	}
}