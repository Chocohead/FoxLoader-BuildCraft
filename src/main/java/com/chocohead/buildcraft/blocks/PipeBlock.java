package com.chocohead.buildcraft.blocks;

import java.util.List;

import net.minecraft.common.util.math.AxisAlignedBB;
import net.minecraft.common.util.physics.MovingObjectPosition;
import net.minecraft.common.util.math.Vec3D;
import net.minecraft.common.block.icon.Icon;
import net.minecraft.common.block.icon.IconRegister;
import net.minecraft.common.util.Direction.EnumDirection;
import net.minecraft.common.block.data.Materials;
import net.minecraft.common.block.tileentity.TileEntity;
import net.minecraft.common.entity.Entity;
import net.minecraft.common.entity.player.EntityPlayer;
import net.minecraft.common.world.BlockAccess;
import net.minecraft.common.world.World;

import com.chocohead.buildcraft.Utils;
import com.chocohead.buildcraft.api.IPipeConnection;
import com.chocohead.buildcraft.client.IPipeBlock;
import com.chocohead.buildcraft.items.PipeItem;
import com.chocohead.buildcraft.pipes.Pipe;

public abstract class PipeBlock extends MetaBlockContainer implements IPipeBlock, IPipeConnection {
	public static int renderID;

	public PipeBlock(String id) {
		super(id, Materials.GLASS);

		bypassMaximumMetadataLimit = true;
	}

	@Override
	protected abstract PipeItem initializeItemBlock();

	@Override
	public int getRenderType() {
		return renderID;
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
	public void getCollidingBoundingBoxes(World world, int x, int y, int z, AxisAlignedBB box, List<AxisAlignedBB> collisions) {
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

		return AxisAlignedBB.getAABBPool().getAABB(x + xMin, y + yMin, z + zMin, x + xMax, y + yMax, z + zMax);
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
	public void onBlockRemoval(World world, int x, int y, int z) {
		Utils.preDestroyBlock(world, x, y, z);

		super.onBlockRemoval(world, x, y, z);
	}

	@Override
	protected abstract PipeTileEntity<?> getTileEntity(int meta);

	protected abstract Pipe<?> getPipe(int meta);

	/*@Override
	public int idDropped(int metadata, Random random) {
		throw new UnsupportedOperationException(); //Need a position
	}*/

	@Override
	protected int damageDropped(int meta) {
		return meta;
	}

	/*@Override
	public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance) {
		if (!world.multiplayerWorld) {
			int quantity = quantityDropped(world.rand);

			for (int i = 0; i < quantity; i++) {
				if (world.rand.nextFloat() <= chance) {
					int id = getPipe(world, x, y, z).itemID;
					if (id > 0) {
						dropBlockAsItem_do(world, x, y, z, new ItemStack(id, 1, damageDropped(meta)));
					}
				}
			}
		}
	}*/

	@Override
	public boolean isPipeConnected(BlockAccess world, int x1, int y1, int z1, int x2, int y2, int z2) {
		Pipe<?> pipe1 = getPipe(world, x1, y1, z1);
		Pipe<?> pipe2 = getPipe(world, x2, y2, z2);

		if (pipe2 != null && !pipe1.transport.getClass().equals(pipe2.transport.getClass())) {
			return false;
		}

		TileEntity tile = world.getBlockTileEntity(x2, y2, z2);
		return getPipe(world, x1, y1, z1).isPipeConnected(tile);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int meta) {
		super.onNeighborBlockChange(world, x, y, z, meta);

		getPipe(world, x, y, z).onNeighborBlockChange();
	}

	@Override
	public void onBlockPlaced(World world, int x, int y, int z, int meta) {
		super.onBlockPlaced(world, x, y, z, meta);

		getPipe(world, x, y, z).onBlockPlaced();
	}

	@Override
	public boolean blockActivated(World world, int x, int y, int z, EntityPlayer player) {
		super.blockActivated(world, x, y, z, player);

		return getPipe(world, x, y, z).blockActivated(world, x, y, z, player);
	}


	@Override
	public void registerIcons(IconRegister register) {
		for (int meta = 0; meta <= maxMetadata; meta++) {
			getPipe(meta).registerIcons(register);
		}
	}

	@Override //FIXME: Pull IPipeBlock across
	public void prepareTextureFor(BlockAccess world, int x, int y, int z, EnumDirection connection) {
		getPipe(world, x, y, z).prepareTextureFor(connection);
	}

	@Override
	public Icon getBlockTexture(BlockAccess world, int x, int y, int z, int face, int meta) {
		return getPipe(world, x, y, z).getBlockTexture();
	}

	@Override
	public Icon getIcon(int face, int meta) {
		Pipe<?> pipe = getPipe(meta);
		pipe.prepareTextureFor(EnumDirection.UNKNOWN);
		return pipe.getBlockTexture();
	}

	public String getBlockName(int meta) {
		return getPipe(meta).getName();
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		super.onEntityCollidedWithBlock(world, x, y, z, entity);

		getPipe(world, x, y, z).onEntityCollidedWithBlock(entity);
	}


	public static Pipe<?> getPipe(BlockAccess world, int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);

		Pipe<?> pipe = null;

		if (tile instanceof PipeTileEntity) {
			pipe = ((PipeTileEntity<?>) tile).pipe;
		}

		/*if (pipe == null) {
			pipe = pipeBuffer.get(new BlockIndex(x, y, z));
		}*/

		return pipe;
	}
}