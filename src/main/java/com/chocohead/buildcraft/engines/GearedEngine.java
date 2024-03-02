package com.chocohead.buildcraft.engines;

import com.chocohead.buildcraft.blocks.EngineTileEntity;

public class GearedEngine extends Engine {
	public static final String TEXTURE = "/textures/blocks/buildcraft/models/gearedEngine.png";

	public GearedEngine(EngineTileEntity tile) {
		super(tile, 1000, 1, 50);
	}

	@Override
	public String getTextureFile() {
		return TEXTURE;
	}

	@Override
	public float getPistonSpeed() {
		switch (getEnergyStage()) {
		case Blue:
			return 0.01F;
		case Green:
			return 0.02F;
		case Yellow:
			return 0.04F;
		case Red:
			return 0.08F;
		default:
			return 0;
		}
	}

	@Override
	public void update() {
		super.update();

		if (isBurning() && (tile.worldObj.getWorldTime() % 20) == 0) {
			energy++;
		}
	}

	@Override
	public boolean isBurning() {
		return tile.worldObj.isBlockIndirectlyGettingPowered(tile.xCoord, tile.yCoord, tile.zCoord);
	}

	@Override
	public void burn() {
	}

	@Override
	public int getScaledBurnTime(int scale) {
		return 0; //No GUI
	}

	@Override
	public int explosionRange() {
		return 1;
	}
}