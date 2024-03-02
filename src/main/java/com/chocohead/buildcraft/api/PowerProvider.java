package com.chocohead.buildcraft.api;

import net.minecraft.src.game.block.tileentity.TileEntity;
import net.minecraft.src.game.nbt.NBTTagCompound;

public class PowerProvider {
	private final SafeTimeTracker energyLossTracker = new SafeTimeTracker();
	public int minEnergyReceived;
	public int maxEnergyReceived;
	public int maxEnergyStored;
	public int minActivationEnergy;	
	public int energyStored = 0;

	protected int powerLoss = 1;
	protected int powerLossRegularity = 100;

	public void configure(int minEnergyReceived, int maxEnergyReceived, int minActivationEnergy, int maxStoredEnergy) {
		this.minEnergyReceived = minEnergyReceived;
		this.maxEnergyReceived = maxEnergyReceived;
		this.maxEnergyStored = maxStoredEnergy;
		this.minActivationEnergy = minActivationEnergy;
	}

	public void configurePowerPerdition(int powerLoss, int powerLossRegularity) {
		this.powerLoss = powerLoss;
		this.powerLossRegularity = powerLossRegularity;
	}

	public void readFromNBT(NBTTagCompound nbt) {
		minEnergyReceived = nbt.getInteger("minEnergyReceived");
		maxEnergyReceived = nbt.getInteger("maxEnergyReceived");
		maxEnergyStored = nbt.getInteger("maxStoreEnergy");
		minActivationEnergy = nbt.getInteger("minActivationEnergy");	
		energyStored = nbt.getInteger("storedEnergy");
	}

	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("minEnergyReceived", minEnergyReceived);
		nbt.setInteger("maxEnergyReceived", maxEnergyReceived);
		nbt.setInteger("maxStoreEnergy", maxEnergyStored);
		nbt.setInteger("minActivationEnergy", minActivationEnergy);
		nbt.setInteger("storedEnergy", energyStored);
	}

	public final boolean update(IPowerReceptor receptor) {
		if (!preConditions(receptor)) {
			return false;
		}

		TileEntity tile = (TileEntity) receptor;
		boolean didWork = false;

		if (energyStored >= minActivationEnergy) {
			receptor.doWork();
			didWork = true;
		}

		if (powerLoss > 0 && energyLossTracker.markTimeIfDelay(tile.worldObj, powerLossRegularity)) {
			energyStored -= powerLoss;
			if (energyStored < 0) {
				energyStored = 0;
			}
		}

		return didWork;		
	}

	public boolean preConditions(IPowerReceptor receptor) {
		return true;
	}

	public int useEnergy(int min, int max, boolean doUse) {
		int result = 0;

		if (energyStored >= min) {
			if (energyStored <= max) {
				result = energyStored;
				if (doUse) {
					energyStored = 0;
				}
			} else {
				result = max;
				if (doUse) {
					energyStored -= max;
				}
			}
		}

		return result;
	}

	public void receiveEnergy(int quantity) {
		energyStored += quantity;

		if (energyStored > maxEnergyStored) {
			energyStored = maxEnergyStored;
		}
	}
}