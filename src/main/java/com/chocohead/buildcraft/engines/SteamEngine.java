package com.chocohead.buildcraft.engines;

import net.minecraft.common.block.tileentity.TileEntityFurnace;
import com.mojang.nbt.CompoundTag;

import com.chocohead.buildcraft.blocks.EngineTileEntity;

public class SteamEngine extends Engine {
	public static final String TEXTURE = "/textures/blocks/buildcraft/models/steamEngine.png";
	protected int burnTime = 0;
	protected int totalBurnTime = 0;

	public SteamEngine(EngineTileEntity tile) {
		super(tile, 10000, 100, 200);
	}

	@Override
	public void writeToNBT(CompoundTag nbt) {
		super.writeToNBT(nbt);

		nbt.setInteger("burnTime", burnTime);
		nbt.setInteger("totalBurnTime", totalBurnTime);
	}

	@Override
	public void readFromNBT(CompoundTag nbt) {
		super.readFromNBT(nbt);

		burnTime = nbt.getInteger("burnTime");
		totalBurnTime = nbt.getInteger("totalBurnTime");
	}

	@Override
	public String getTextureFile() {
		return TEXTURE;
	}

	@Override
	public float getPistonSpeed() {
		switch (getEnergyStage()) {
		case Blue:
			return 0.02F;
		case Green:
			return 0.04F;
		case Yellow:
			return 0.08F;
		case Red:
			return 0.16F;
		default:
			return 0;
		}
	}

	@Override
	public boolean isBurning() {
		return burnTime > 0;
	}

	@Override
	public void burn() {
		if (burnTime > 0) {
			burnTime--;
			addEnergy(1);
		}

		if (burnTime == 0 && tile.worldObj.isBlockIndirectlyGettingPowered(tile.xCoord, tile.yCoord, tile.zCoord)) {
			burnTime = totalBurnTime = TileEntityFurnace.getItemBurnTime(tile.getStackInSlot(0));

			if (burnTime > 0) {
				tile.decrStackSize(0, 1);
			}
		}
	}

	@Override
	public int getScaledBurnTime(int scale) {
		return (int) ((burnTime / (float) totalBurnTime) * scale);
	}

	@Override
	public int explosionRange() {
		return 4;
	}
}