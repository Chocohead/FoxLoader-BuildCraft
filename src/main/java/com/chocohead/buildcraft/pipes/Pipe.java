package com.chocohead.buildcraft.pipes;

import net.minecraft.src.client.renderer.block.icon.Icon;
import net.minecraft.src.client.renderer.block.icon.IconRegister;
import net.minecraft.src.game.Direction.EnumDirection;
import net.minecraft.src.game.block.tileentity.TileEntity;
import net.minecraft.src.game.entity.Entity;
import net.minecraft.src.game.entity.other.EntityItem;
import net.minecraft.src.game.entity.player.EntityPlayer;
import net.minecraft.src.game.level.World;
import net.minecraft.src.game.nbt.NBTTagCompound;

import com.chocohead.buildcraft.blocks.PipeTileEntity;
import com.chocohead.buildcraft.pipes.logic.PipeLogic;
import com.chocohead.buildcraft.pipes.transport.PipeTransport;

public abstract class Pipe<T extends PipeTransport> extends PipeLike {
	public final T transport;
	public final PipeLogic logic;
	public final String id;

	/*private TilePacketWrapper networkPacket;

	@SuppressWarnings("rawtypes")
	private static Map<Class, TilePacketWrapper> networkWrappers = new HashMap<Class, TilePacketWrapper>();*/

	public Pipe(T transport, PipeLogic logic, String id) {
		this.transport = transport;
		this.logic = logic;
		this.id = id;

		/*if (!networkWrappers.containsKey(this.getClass())) {
			networkWrappers.put(
					this.getClass(),
					new TilePacketWrapper(new Class[] {
							this.transport.getClass(), this.logic.getClass() },
							PacketIds.TileUpdate));
		}

		this.networkPacket = networkWrappers.get(this.getClass());*/
	}

	@Override
	public void setPosition(int xCoord, int yCoord, int zCoord) {
		super.setPosition(xCoord, yCoord, zCoord);
		transport.setPosition(xCoord, yCoord, zCoord);
		logic.setPosition(xCoord, yCoord, zCoord);
	}

	@Override
	public void setWorld(World world) {
		super.setWorld(world);
		transport.setWorld(world);
		logic.setWorld(world);
	}

	@Override
	public void setTile(PipeTileEntity<?> tile) {
		super.setTile(tile);
		transport.setTile(tile);
		logic.setTile(tile);
	}

	public boolean blockActivated(World world, int x, int y, int z, EntityPlayer player) {
		return logic.blockActivated(player);
	}

	@Override
	public void onBlockPlaced() {
		logic.onBlockPlaced();
		transport.onBlockPlaced();
	}

	@Override
	public void onNeighborBlockChange() {
		logic.onNeighborBlockChange();
		transport.onNeighborBlockChange();
	}

	@Override
	public boolean isPipeConnected(TileEntity tile) {
		return logic.isPipeConnected(tile) && transport.isPipeConnected(tile);
	}

	public void registerIcons(IconRegister register) {
	}

	public void prepareTextureFor(EnumDirection connection) {
	}

	public abstract Icon getBlockTexture();

	public abstract String getName();

	public void updateEntity() {
		transport.updateEntity();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		transport.writeToNBT(nbt);
		logic.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		transport.readFromNBT(nbt);
		logic.readFromNBT(nbt);
	}

	@Override
	public void initialize() {
		transport.initialize();
		logic.initialize();
	}

	@Override
	public boolean inputOpen(EnumDirection from) {
		return transport.inputOpen(from) && logic.inputOpen(from);
	}

	@Override
	public boolean outputOpen(EnumDirection to) {
		return transport.outputOpen(to) && logic.outputOpen(to);
	}

	public void onEntityCollidedWithBlock(Entity entity) {
	}

	public void onDropped(EntityItem item) {
	}
}