package com.chocohead.buildcraft.engines;

import java.util.Locale;

import net.minecraft.common.util.Direction.EnumDirection;
import com.mojang.nbt.CompoundTag;

import com.chocohead.buildcraft.blocks.EngineTileEntity;

public abstract class Engine {
	public enum EnergyStage {
		Blue,
		Green,
		Yellow,
		Red,
		Explosion(Red.texture);
		public final String texture;

		private EnergyStage() {
			texture = "/textures/blocks/buildcraft/models/" + name().toLowerCase(Locale.ENGLISH) + "EngineTrunk.png";
		}

		private EnergyStage(String texture) {
			this.texture = texture;
		}
	}
	protected final EngineTileEntity tile;
	public final int maxEnergy;
	public final int maxEnergyExtracted;
	public final int maxEnergyReceived;
	public EnumDirection orientation = EnumDirection.UP;
	public float progress;
	public int energy;

	public Engine(EngineTileEntity tile, int maxEnergy, int maxEnergyExtracted, int maxEnergyReceived) {
		this.tile = tile;
		this.maxEnergy = maxEnergy;
		this.maxEnergyExtracted = maxEnergyExtracted;
		this.maxEnergyReceived = maxEnergyReceived;
	}

	public void writeToNBT(CompoundTag nbt) {
		nbt.setInteger("orientation", orientation.ordinal());
		nbt.setFloat("progress", progress);
		nbt.setInteger("energy", energy);
	}

	public void readFromNBT(CompoundTag nbt) {
		orientation = EnumDirection.VALID_DIRECTIONS[nbt.getInteger("orientation")];
		progress = nbt.getFloat("progress");
    	energy = nbt.getInteger("energy");
	}

	public abstract String getTextureFile();

	public EnergyStage getEnergyStage() {
		double ratio = (energy / (double) maxEnergy) * 100;
		if (ratio <= 25) {
			return EnergyStage.Blue;
		} else if (ratio <= 50) {
		 	return EnergyStage.Green;
		} else if (ratio <= 75) {
			return EnergyStage.Yellow;
		} else if (ratio <= 100) {
			return EnergyStage.Red;
		} else {
			return EnergyStage.Explosion;
		}
	}

	public void update() {
		if (!tile.worldObj.isBlockIndirectlyGettingPowered(tile.xCoord, tile.yCoord, tile.zCoord)) {
			if (energy > 1) {
				energy--;
			}
		}
	}

	public void addEnergy(int addition) {
		energy += addition;

		if (getEnergyStage() == EnergyStage.Explosion) {
			tile.worldObj.createExplosion(null, tile.xCoord, tile.yCoord, tile.zCoord, explosionRange());
		}

		if (energy > maxEnergy) {
			energy = maxEnergy;
		}
	}
	
	public int extractEnergy(int min, int max, boolean doExtract) {
		if (energy < min) {
			return 0;
		}

		if (max > maxEnergyExtracted) {
			max = maxEnergyExtracted;
		}

		int extracted;
		if (energy >= max) {
			extracted = max;
			if (doExtract) {
				energy -= max; 
			}
		} else {
			extracted = energy;
			if (doExtract) {
				energy = 0; 
			}
		}

		return extracted;
	}

	public abstract float getPistonSpeed();

	public abstract boolean isBurning();

	public abstract void burn();

	public abstract int getScaledBurnTime(int scale);

	public abstract int explosionRange();
}