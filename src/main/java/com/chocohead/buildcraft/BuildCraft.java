package com.chocohead.buildcraft;

import net.minecraft.src.game.item.Item;

import com.fox2code.foxloader.loader.Mod;
import com.fox2code.foxloader.registry.BlockBuilder;
import com.fox2code.foxloader.registry.ItemBuilder;
import com.fox2code.foxloader.registry.RegisteredBlock;
import com.fox2code.foxloader.registry.RegisteredItem;

import com.chocohead.buildcraft.blocks.EngineBlock;
import com.chocohead.buildcraft.blocks.EngineTileEntity;
import com.chocohead.buildcraft.blocks.FrameBlock;
import com.chocohead.buildcraft.blocks.QuarryBlock;
import com.chocohead.buildcraft.blocks.QuarryTileEntity;
import com.chocohead.buildcraft.blocks.TransportPipeBlock;
import com.chocohead.buildcraft.blocks.TransportPipeTileEntity;
import com.chocohead.buildcraft.items.BuildCraftItem;
import com.chocohead.buildcraft.items.EngineItem;
import com.chocohead.buildcraft.items.PipeItem;
import com.chocohead.buildcraft.items.WrenchItem;
import com.chocohead.buildcraft.mixins.TileEntityAccess;

public class BuildCraft extends Mod {
    public static RegisteredItem wrench;
    public static RegisteredItem woodenGear;
    public static RegisteredItem stoneGear;
    public static RegisteredItem ironGear;
    public static RegisteredItem goldGear;
    public static RegisteredItem diamondGear;
    public static RegisteredItem pipeWaterproof;
    public static RegisteredBlock transportPipe;
    public static RegisteredBlock engine;
    public static RegisteredBlock frame;
    public static RegisteredBlock quarry;

    @Override
    public void onPreInit() {
        wrench = registerNewItem("wrench", new ItemBuilder().setGameItemSource(WrenchItem.class));
        woodenGear = registerNewItem("woodenGear", new ItemBuilder().setGameItemSource(BuildCraftItem.class).setBurnTime(400));
        stoneGear = registerNewItem("stoneGear", new ItemBuilder().setGameItemSource(BuildCraftItem.class));
        ironGear = registerNewItem("ironGear", new ItemBuilder().setGameItemSource(BuildCraftItem.class));
        goldGear = registerNewItem("goldGear", new ItemBuilder().setGameItemSource(BuildCraftItem.class).setTooltipColor(Item.COLOR_YELLOW));
        diamondGear = registerNewItem("diamondGear", new ItemBuilder().setGameItemSource(BuildCraftItem.class).setTooltipColor(Item.COLOR_CYAN));

        pipeWaterproof = registerNewItem("pipeWaterproof", new ItemBuilder().setGameItemSource(BuildCraftItem.class));
        transportPipe = registerNewBlock("itemPipe", new BlockBuilder().setGameBlockSource(TransportPipeBlock.class).setItemBlock(new ItemBuilder().setGameItemSource(PipeItem.class)));
        TileEntityAccess.callAddMapping(TransportPipeTileEntity.class, "buildcraft:transportPipe");

        engine = registerNewBlock("engine", new BlockBuilder().setGameBlockSource(EngineBlock.class).setItemBlock(new ItemBuilder().setGameItemSource(EngineItem.class)));
        TileEntityAccess.callAddMapping(EngineTileEntity.class, "buildcraft:engine");

        frame = registerNewBlock("frame", new BlockBuilder().setGameBlockSource(FrameBlock.class).setItemBlock(new ItemBuilder().hideFromCreativeInventory()));
        quarry = registerNewBlock("quarry", new BlockBuilder().setGameBlockSource(QuarryBlock.class));
        TileEntityAccess.callAddMapping(QuarryTileEntity.class, "buildcraft:quary");
    }

    @Override
    public void onInit() {
    	Recipes.register(this);
    }
}