package com.chocohead.buildcraft.blueprints;

import net.minecraft.src.game.level.World;

import com.chocohead.buildcraft.Utils;
import com.chocohead.buildcraft.api.IAreaProvider;

public class BluePrintBuilder implements IAreaProvider {
	public final BluePrint bluePrint;
	public final int x, y, z;
	protected boolean done;

	public BluePrintBuilder(BluePrint bluePrint, int x, int y, int z) {
		this.bluePrint = bluePrint;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public BlockContents findNextBlock(World world) {
		for (int y = 0; y < bluePrint.sizeY; y++) {
			int yCoord = y + this.y - bluePrint.anchorY;
			if (yCoord <= 0) continue;

			for (int x = 0; x < bluePrint.sizeX; x++) {
				int xCoord = x + this.x - bluePrint.anchorX;

				for (int z = 0; z < bluePrint.sizeZ; z++) {
					int zCoord = z + this.z - bluePrint.anchorZ;

					BlockContents content = bluePrint.getBlock(x, y, z);
					if (content == null) {
						continue;
					}

					content = content.clone();
					content.x = xCoord;
					content.y = yCoord;
					content.z = zCoord;
					int blockId = world.getBlockId(xCoord, yCoord, zCoord);

					if (Utils.isSoftBlock(content.blockId)) {
						if (Utils.isSoftBlock(blockId)) {
							//Also a soft block, that'll do
						} else if (!Utils.isUnbreakableBlock(blockId)) {
							return content;
						}
					} else {
						if (blockId == content.blockId) {
							//This block is correct, move on
						} else if (!Utils.isUnbreakableBlock(blockId)) {
							return content;
						}
					}
				}
			}
		}

		done = true;
		return null;
	}

	public void reset() {
		this.done = false;
	}

	public boolean isDone() {
		return done;
	}

	@Override
	public int xMin() {
		return x - bluePrint.anchorX;
	}

	@Override
	public int yMin() {
		return y - bluePrint.anchorY;
	}

	@Override
	public int zMin() {
		return z - bluePrint.anchorZ;
	}

	@Override
	public int xMax() {
		return x + bluePrint.sizeX - bluePrint.anchorX - 1;
	}

	@Override
	public int yMax() {
		return y + bluePrint.sizeY - bluePrint.anchorY - 1;
	}

	@Override
	public int zMax() {
		return z + bluePrint.sizeZ - bluePrint.anchorZ - 1;
	}
}