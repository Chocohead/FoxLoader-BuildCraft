package com.chocohead.buildcraft.blocks;

import java.util.Random;

import net.minecraft.common.block.icon.IconRegister;
import net.minecraft.common.block.data.Materials;
import net.minecraft.common.block.tileentity.TileEntity;
import net.minecraft.common.entity.EntityLiving;
import net.minecraft.common.entity.player.EntityPlayer;
import net.minecraft.common.item.block.ItemBlock;
import net.minecraft.common.world.BlockAccess;
import net.minecraft.common.world.World;

import com.chocohead.buildcraft.BuildCraft;
import com.chocohead.buildcraft.Proxy;
import com.chocohead.buildcraft.Utils;
import com.chocohead.buildcraft.api.IPipeConnection;
import com.chocohead.buildcraft.engines.GearedEngine;
import com.chocohead.buildcraft.engines.SteamEngine;
import com.chocohead.buildcraft.items.EngineItem;

public class EngineBlock extends MetaBlockContainer implements IPipeConnection {
	public static int renderID;

	public EngineBlock(String id) {
		super(id, Materials.METAL);

		setHardness(0.5F);
	}

	@Override
	protected ItemBlock initializeItemBlock() {
		return new EngineItem(this);
	}

	@Override
	public int getRenderType() {
		return renderID;
	}

	@Override
	public void registerIcons(IconRegister register) {//BlockBeacon does this, everything is fine
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	//Server side
	public boolean isACube() {
		return false;
	}

	@Override
	protected TileEntity getTileEntity(int meta) {
		return new EngineTileEntity(meta);
	}

	@Override
	protected int damageDropped(int meta) {
		return meta;
	}

	@Override
	public void onBlockPlaced(World world, int x, int y, int z, int face) {
		EngineTileEntity tile = (EngineTileEntity) world.getBlockTileEntity(x, y, z);
		tile.switchOrientation();
	}

	@Override
	public boolean isPipeConnected(BlockAccess world, int x1, int y1, int z1, int x2, int y2, int z2) {
		EngineTileEntity tile = (EngineTileEntity) world.getBlockTileEntity(x1, y1, z1);
		if (tile == null || tile.engine instanceof GearedEngine) {
			return false;
		}

		switch (tile.getEngine().orientation) {
		case UP:
			return y1 - y2 != -1;
		case DOWN:
			return y1 - y2 != 1;
		case SOUTH:
			return z1 - z2 != -1;
		case NORTH:
			return z1 - z2 != 1;
		case EAST:
			return x1 - x2 != -1;
		case WEST:
			return x1 - x2 != 1;
		default:
			return false;
		}
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		EngineTileEntity tile = (EngineTileEntity) world.getBlockTileEntity(x, y, z);
		if (!tile.engine.isBurning()) {
			return;
		}

		float particleX = (float) x + 0.5F;
		float particleY = (float) y + (random.nextFloat() * 6F) / 16F;
		float particleZ = (float) z + 0.5F;
		float offset = 0.52F;
		float wobble = random.nextFloat() * 0.6F - 0.3F;

		String[] particleTypes = {"reddust", "smalldust", "reddust", "smalldust"};
        for (int i = 4; i > 1; i--) {
        	int swap = random.nextInt(i);
        	String tmp = particleTypes[i - 1];
        	particleTypes[i - 1] = particleTypes[swap];
        	particleTypes[swap] = tmp;
        }
		world.spawnParticle(particleTypes[0], particleX - offset, particleY, particleZ + wobble, 0D, 0D, 0D);
		world.spawnParticle(particleTypes[1], particleX + offset, particleY, particleZ + wobble, 0D, 0D, 0D);
		world.spawnParticle(particleTypes[2], particleX + wobble, particleY, particleZ - offset, 0D, 0D, 0D);
		world.spawnParticle(particleTypes[3], particleX + wobble, particleY, particleZ + offset, 0D, 0D, 0D);
	}

	@Override
	public boolean blockActivated(World world, int x, int y, int z, EntityPlayer player) {
		EngineTileEntity tile = (EngineTileEntity) world.getBlockTileEntity(x, y, z);

		if ((player.getHeldItem() == null || player.getHeldItem().getItem() != BuildCraft.wrench) && tile.getEngine() instanceof SteamEngine) {
			if (!world.isRemote) Proxy.displaySteamEngineGUI(player, tile);

			return true;
		}

		return false;
	}

	@Override
	public boolean doWrenchRotation(World world, int x, int y, int z, int meta, int facing, EntityLiving player) {
		EngineTileEntity tile = (EngineTileEntity) world.getBlockTileEntity(x, y, z);
		tile.switchOrientation();

		return true;
	}

	@Override
	public void onBlockRemoval(World world, int x, int y, int z) {
		Utils.preDestroyBlock(world, x, y, z);

		super.onBlockRemoval(world, x, y, z);
	}
}