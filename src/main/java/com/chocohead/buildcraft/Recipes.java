package com.chocohead.buildcraft;

import net.minecraft.src.game.block.Block;
import net.minecraft.src.game.item.Item;
import net.minecraft.src.game.item.ItemStack;

import com.fox2code.foxloader.loader.Mod;
import com.fox2code.foxloader.registry.RegisteredItemStack;

public class Recipes {
	public static void register(Mod mod) {
		mod.registerRecipe(BuildCraft.wrench.newRegisteredItemStack(), "I I", " G ", " I ", 'I', Item.ingotIron, 'G', BuildCraft.stoneGear);
		registerGears(mod);

		mod.registerRecipe(BuildCraft.pipeWaterproof.newRegisteredItemStack(), "D ", " ", 'D', new ItemStack(Item.dyePowder, 1, 4));
		registerPipes(mod);

		registerEngines(mod);

		RegisteredItemStack stonePipe = BuildCraft.transportPipe.newRegisteredItemStack();
		stonePipe.setRegisteredDamage(2);
		mod.registerRecipe(BuildCraft.quarry.newRegisteredItemStack(), "ISI", "GIG", "DPD", 'I', BuildCraft.ironGear, 'S', stonePipe, 'G', BuildCraft.goldGear, 'D', BuildCraft.diamondGear, 'P', Item.pickaxeDiamond);
	}

	private static void registerGears(Mod mod) {
		mod.registerRecipe(BuildCraft.woodenGear.newRegisteredItemStack(), " S ", "S S", " S ", 'S', Item.stick);
		mod.registerRecipe(BuildCraft.stoneGear.newRegisteredItemStack(), " S ", "SGS", " S ", 'S', Block.cobblestone, 'G', BuildCraft.woodenGear);
		mod.registerRecipe(BuildCraft.ironGear.newRegisteredItemStack(), " I ", "IGI", " I ", 'I', Item.ingotIron, 'G', BuildCraft.stoneGear);
		mod.registerRecipe(BuildCraft.goldGear.newRegisteredItemStack(), " I ", "IGI", " I ", 'I', Item.ingotGold, 'G', BuildCraft.ironGear);
		mod.registerRecipe(BuildCraft.diamondGear.newRegisteredItemStack(), " D ", "DGD", " D ", 'D', Item.diamond, 'G', BuildCraft.goldGear);
	}

	private static void registerPipes(Mod mod) {
		Object[] materials = {Block.planks, Block.cobblestone, Block.stone, Block.sandstone, Block.clay, Item.ingotIron, Item.ingotGold, Item.diamond, Block.obsidian};
		for (int i = 0; i < materials.length; i++) {
			RegisteredItemStack stack = BuildCraft.transportPipe.newRegisteredItemStack();
			stack.setRegisteredDamage(i);
			stack.setRegisteredStackSize(8);
			mod.registerRecipe(stack, "MGM", 'G', Block.glass, 'M', materials[i]);
		}
	}

	private static void registerEngines(Mod mod) {
		RegisteredItemStack engine = BuildCraft.engine.newRegisteredItemStack();
		engine.setRegisteredDamage(0);
		mod.registerRecipe(engine, "WWW", " g ", "GPG", 'W', Block.planks, 'g', Block.glass, 'G', BuildCraft.woodenGear, 'P', Block.pistonBase);

		engine = BuildCraft.engine.newRegisteredItemStack();
		engine.setRegisteredDamage(1);
		mod.registerRecipe(engine, "CCC", " g ", "GPG", 'C', Block.cobblestone, 'g', Block.glass, 'G', BuildCraft.stoneGear, 'P', Block.pistonBase);
	}
}