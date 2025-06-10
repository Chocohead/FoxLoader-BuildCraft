package com.chocohead.buildcraft;

import net.minecraft.common.block.Block;
import net.minecraft.common.block.data.EnumColors;
import net.minecraft.common.block.tileentity.TileEntity;
import net.minecraft.common.item.Item;

import com.fox2code.foxloader.loader.Mod;
import com.fox2code.foxloader.registry.EntityRegistry;

import com.chocohead.buildcraft.blocks.EngineBlock;
import com.chocohead.buildcraft.blocks.EngineTileEntity;
import com.chocohead.buildcraft.blocks.FrameBlock;
import com.chocohead.buildcraft.blocks.QuarryBlock;
import com.chocohead.buildcraft.blocks.QuarryTileEntity;
import com.chocohead.buildcraft.blocks.TransportPipeBlock;
import com.chocohead.buildcraft.blocks.TransportPipeTileEntity;
import com.chocohead.buildcraft.entities.BlockEntity;
import com.chocohead.buildcraft.entities.QuarryArmEntity;
import com.chocohead.buildcraft.items.BuildCraftItem;
import com.chocohead.buildcraft.items.WrenchItem;

public class BuildCraft extends Mod {
    public static Item wrench;
    public static Item woodenGear;
    public static Item stoneGear;
    public static Item ironGear;
    public static Item goldGear;
    public static Item diamondGear;
    public static Item pipeWaterproof;
    public static Block transportPipe;
    public static Block engine;
    public static Block frame;
    public static Block quarry;

    @Override
    public void onPreInit() {
        wrench = new WrenchItem("wrench");
        woodenGear = new BuildCraftItem("woodenGear").setBurnTime(400, 1);
        stoneGear = new BuildCraftItem("stoneGear");
        ironGear = new BuildCraftItem("ironGear");
        goldGear = new BuildCraftItem("goldGear").setTooltipColor(EnumColors.COLOR_YELLOW);
        diamondGear = new BuildCraftItem("diamondGear").setTooltipColor(EnumColors.COLOR_CYAN);

        pipeWaterproof = new BuildCraftItem("pipeWaterproof");
        transportPipe = new TransportPipeBlock("itemPipe");
        TileEntity.addMapping(TransportPipeTileEntity.class, "buildcraft:transportPipe");

        engine = new EngineBlock("engine");
        TileEntity.addMapping(EngineTileEntity.class, "buildcraft:engine");

        frame = new FrameBlock("frame");
        quarry = new QuarryBlock("quarry");
        TileEntity.addMapping(QuarryTileEntity.class, "buildcraft:quary");

        //EntityRegistry.registerEntityClass(BlockEntity.class, "block");
        //EntityRegistry.registerEntityClass(QuarryArmEntity.class, "quarryArm");
    }

    @Override
    public void onInit() {
    	Recipes.register();
    }
}