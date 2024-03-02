package com.chocohead.buildcraft.blocks;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.src.client.physics.AxisAlignedBB;
import net.minecraft.src.client.physics.MovingObjectPosition;
import net.minecraft.src.client.renderer.Vec3D;
import net.minecraft.src.client.renderer.block.icon.Icon;
import net.minecraft.src.client.renderer.block.icon.IconRegister;
import net.minecraft.src.game.Direction.EnumDirection;
import net.minecraft.src.game.block.Block;
import net.minecraft.src.game.block.Material;
import net.minecraft.src.game.level.IBlockAccess;
import net.minecraft.src.game.level.World;

import com.chocohead.buildcraft.Utils;
import com.chocohead.buildcraft.api.IPipeConnection;
import com.chocohead.buildcraft.client.IPipeBlock;
import com.chocohead.buildcraft.client.PipeRenderer;

public class FrameBlock extends Block implements IPipeBlock, IPipeConnection {
	private Icon texture;

	public FrameBlock(int id) {
		super(id, Material.glass);
	}

	@Override
	public int getRenderType() {
		return PipeRenderer.renderID;
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
	public void getCollidingBoundingBoxes(World world, int x, int y, int z, AxisAlignedBB box, ArrayList<AxisAlignedBB> collisions) {
		setBlockBounds(Utils.PIPE_MIN_POS, Utils.PIPE_MIN_POS, Utils.PIPE_MIN_POS, Utils.PIPE_MAX_POS, Utils.PIPE_MAX_POS, Utils.PIPE_MAX_POS);
		super.getCollidingBoundingBoxes(world, x, y, z, box, collisions);

		if (Utils.checkPipesConnections(world, x, y, z, x - 1, y, z)) {
			setBlockBounds(0F, Utils.PIPE_MIN_POS, Utils.PIPE_MIN_POS, Utils.PIPE_MAX_POS, Utils.PIPE_MAX_POS, Utils.PIPE_MAX_POS);
			super.getCollidingBoundingBoxes(world, x, y, z, box, collisions);
		}

		if (Utils.checkPipesConnections(world, x, y, z, x + 1, y, z)) {
			setBlockBounds(Utils.PIPE_MIN_POS, Utils.PIPE_MIN_POS, Utils.PIPE_MIN_POS, 1F, Utils.PIPE_MAX_POS, Utils.PIPE_MAX_POS);
			super.getCollidingBoundingBoxes(world, x, y, z, box, collisions);
		}

		if (Utils.checkPipesConnections(world, x, y, z, x, y - 1, z)) {
			setBlockBounds(Utils.PIPE_MIN_POS, 0F, Utils.PIPE_MIN_POS, Utils.PIPE_MAX_POS, Utils.PIPE_MAX_POS, Utils.PIPE_MAX_POS);
			super.getCollidingBoundingBoxes(world, x, y, z, box, collisions);
		}

		if (Utils.checkPipesConnections(world, x, y, z, x, y + 1, z)) {
			setBlockBounds(Utils.PIPE_MIN_POS, Utils.PIPE_MIN_POS, Utils.PIPE_MIN_POS, Utils.PIPE_MAX_POS, 1F, Utils.PIPE_MAX_POS);
			super.getCollidingBoundingBoxes(world, x, y, z, box, collisions);
		}

		if (Utils.checkPipesConnections(world, x, y, z, x, y, z - 1)) {
			setBlockBounds(Utils.PIPE_MIN_POS, Utils.PIPE_MIN_POS, 0F, Utils.PIPE_MAX_POS, Utils.PIPE_MAX_POS, Utils.PIPE_MAX_POS);
			super.getCollidingBoundingBoxes(world, x, y, z, box, collisions);
		}

		if (Utils.checkPipesConnections(world, x, y, z, x, y, z + 1)) {
			setBlockBounds(Utils.PIPE_MIN_POS, Utils.PIPE_MIN_POS, Utils.PIPE_MIN_POS, Utils.PIPE_MAX_POS, Utils.PIPE_MAX_POS, 1F);
			super.getCollidingBoundingBoxes(world, x, y, z, box, collisions);
		}

		setBlockBounds(0F, 0F, 0F, 1F, 1F, 1F);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		float xMin = Utils.checkPipesConnections(world, x, y, z, x - 1, y, z) ? 0F : Utils.PIPE_MIN_POS;
		float xMax = Utils.checkPipesConnections(world, x, y, z, x + 1, y, z) ? 1F : Utils.PIPE_MAX_POS;
		float yMin = Utils.checkPipesConnections(world, x, y, z, x, y - 1, z) ? 0F : Utils.PIPE_MIN_POS;
		float yMax = Utils.checkPipesConnections(world, x, y, z, x, y + 1, z) ? 1F : Utils.PIPE_MAX_POS;
		float zMin = Utils.checkPipesConnections(world, x, y, z, x, y, z - 1) ? 0F : Utils.PIPE_MIN_POS;
		float zMax = Utils.checkPipesConnections(world, x, y, z, x, y, z + 1) ? 1F : Utils.PIPE_MAX_POS;

		return AxisAlignedBB.getBoundingBoxFromPool(x + xMin, y + yMin, z + zMin, x + xMax, y + yMax, z + zMax);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		return getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3D start, Vec3D end) {
		float xMin = Utils.checkPipesConnections(world, x, y, z, x - 1, y, z) ? 0F : Utils.PIPE_MIN_POS;
		float xMax = Utils.checkPipesConnections(world, x, y, z, x + 1, y, z) ? 1F : Utils.PIPE_MAX_POS;
		float yMin = Utils.checkPipesConnections(world, x, y, z, x, y - 1, z) ? 0F : Utils.PIPE_MIN_POS;
		float yMax = Utils.checkPipesConnections(world, x, y, z, x, y + 1, z) ? 1F : Utils.PIPE_MAX_POS;
		float zMin = Utils.checkPipesConnections(world, x, y, z, x, y, z - 1) ? 0F : Utils.PIPE_MIN_POS;
		float zMax = Utils.checkPipesConnections(world, x, y, z, x, y, z + 1) ? 1F : Utils.PIPE_MAX_POS;

		setBlockBounds(xMin, yMin, zMin, xMax, yMax, zMax);

		MovingObjectPosition hit = super.collisionRayTrace(world, x, y, z, start, end);

		setBlockBounds(0F, 0F, 0F, 1F, 1F, 1F);

		return hit;
	}

	@Override
	public int idDropped(int metadata, Random random) {
		return -1; //Don't drop anything
	}

	@Override
	public boolean isPipeConnected(IBlockAccess world, int x1, int y1, int z1, int x2, int y2, int z2) {
		return world.getBlockId(x2, y2, z2) == blockID;
	}

	@Override
	public void registerIcons(IconRegister register) {
		texture = register.registerIcon("buildcraft/frame");
	}

	public void prepareTextureFor(IBlockAccess world, int x, int y, int z, EnumDirection connection) {
	}

	@Override
	public Icon getIcon(int face, int meta) {
		return texture;
	}
}