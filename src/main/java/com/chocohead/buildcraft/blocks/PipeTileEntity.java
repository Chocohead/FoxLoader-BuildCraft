package com.chocohead.buildcraft.blocks;

import net.minecraft.src.game.block.tileentity.TileEntity;
import net.minecraft.src.game.nbt.NBTTagCompound;

import com.chocohead.buildcraft.api.IPowerReceptor;
import com.chocohead.buildcraft.api.PowerProvider;
import com.chocohead.buildcraft.pipes.Pipe;
import com.chocohead.buildcraft.pipes.transport.PipeTransport;

public abstract class PipeTileEntity<T extends PipeTransport> extends TileEntity implements IPowerReceptor {
	protected Pipe<T> pipe;
	private boolean blockNeighborChange = false;
	private boolean initialized = false;

	public PipeTileEntity() {
	}

	public PipeTileEntity(Pipe<T> pipe) {
		this.pipe = pipe;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		//if (pipe != null) {		
			nbt.setString("pipeId", pipe.id);
			pipe.writeToNBT(nbt);
		//}
	}

	protected abstract Pipe<T> loadPipe(String id);

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		pipe = loadPipe(nbt.getString("pipeId"));
		pipe.setTile(this);
		//pipe.setPosition(xCoord, yCoord, zCoord);
		pipe.readFromNBT(nbt);	
	}

	@Override
	public void validate() {
		super.validate();

		/*if (pipe == null) {
			pipe = BlockGenericPipe.pipeBuffer.get(new BlockIndex(xCoord, yCoord, zCoord));			
		}

		if (pipe != null) {*/
			pipe.setTile(this);
			pipe.setPosition(xCoord, yCoord, zCoord);
			pipe.setWorld(worldObj);
		//}
	}

	@Override
	public void updateEntity() {
		if (!initialized) {
			pipe.initialize();
			initialized = true;
		}

		if (blockNeighborChange) {
			pipe.onNeighborBlockChange();
			blockNeighborChange = false;
		}

		PowerProvider provider = getPowerProvider();
		if (provider != null) {			
			provider.update(this);
		}

		pipe.updateEntity();
	
	}

	public Pipe<T> getPipe() {
		return pipe;
	}

	@Override
	public PowerProvider getPowerProvider() {
		if (pipe instanceof IPowerReceptor) {
			return ((IPowerReceptor) pipe).getPowerProvider();
		} else {
			return null;
		}
	}

	@Override
	public void doWork() {
		if (pipe instanceof IPowerReceptor) {
			((IPowerReceptor) pipe).doWork();
		}		
	}

	/*@Override
	public int fill(EnumDirection from, int quantity, int id, boolean doFill) {
		if (pipe.transport instanceof ILiquidContainer) {
			return ((ILiquidContainer) pipe.transport).fill(from, quantity, id, doFill);
		} else {
			return 0;	
		}		
	}

	@Override
	public int empty(int quantityMax, boolean doEmpty) {
		if (pipe.transport instanceof ILiquidContainer) {
			return ((ILiquidContainer) pipe.transport).empty(quantityMax, doEmpty);
		} else {
			return 0;
		}
	}

	@Override
	public int getLiquidQuantity() {
		if (pipe.transport instanceof ILiquidContainer) {
			return ((ILiquidContainer) pipe.transport).getLiquidQuantity();
		} else {
			return 0;	
		}		
	}

	@Override
	public int getCapacity() {
		if (pipe.transport instanceof ILiquidContainer) {
			return ((ILiquidContainer) pipe.transport).getCapacity();
		} else {
			return 0;
		}
	}

	@Override
	public int getLiquidId() {
		if (pipe.transport instanceof ILiquidContainer) {
			return ((ILiquidContainer) pipe.transport).getLiquidId();
		} else {
			return 0;
		}
	}

	public void scheduleNeighborChange() {
		blockNeighborChange = true;
	}*/
}