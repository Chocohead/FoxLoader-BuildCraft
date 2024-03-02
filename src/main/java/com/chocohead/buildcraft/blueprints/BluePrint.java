package com.chocohead.buildcraft.blueprints;

public class BluePrint {
	private final BlockContents contents[][][];
	public final int sizeX, sizeY, sizeZ;
	public int anchorX, anchorY, anchorZ;

	public BluePrint(BluePrint src) {
		anchorX = src.anchorX;
		anchorY = src.anchorY;
		anchorZ = src.anchorZ;

		sizeX = src.sizeX;
		sizeY = src.sizeY;
		sizeZ = src.sizeZ;

		contents = new BlockContents[sizeX][sizeY][sizeZ];

		for (int x = 0; x < sizeX; ++x) {
			for (int y = 0; y < sizeY; ++y) {
				for (int z = 0; z < sizeZ; ++z) {
					contents[x][y][z] = src.contents[x][y][z];
				}
			}
		}
	}

	public BluePrint(int sizeX, int sizeY, int sizeZ) {
		contents = new BlockContents[sizeX][sizeY][sizeZ];

		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;

		anchorX = 0;
		anchorY = 0;
		anchorZ = 0;
	}

	private BluePrint(BlockContents[][][] contents, int anchorX, int anchorY, int anchorZ) {
		this.contents = contents;

		sizeX = contents.length;
		sizeY = sizeX == 0 ? 0 : contents[0].length;
		sizeZ = sizeY == 0 ? 0 : contents[0][0].length;

		this.anchorX = anchorX;
		this.anchorY = anchorY;
		this.anchorZ = anchorZ;
	}

	public BlockContents getBlock(int x, int y, int z) {
		return contents[x][y][z];
	}

	public void setBlockId(int x, int y, int z, int blockId) {
		if (contents[x][y][z] == null) {
			contents[x][y][z] = new BlockContents();
			contents[x][y][z].x = x;
			contents[x][y][z].y = y;
			contents[x][y][z].z = z;
		}

		contents[x][y][z].blockId = blockId;
	}

	public BluePrint rotateLeft() {
		BlockContents newContents[][][] = new BlockContents[sizeZ][sizeY][sizeX];

		for (int x = 0; x < sizeZ; ++x) {
			for (int y = 0; y < sizeY; ++y) {
				for (int z = 0; z < sizeX; ++z) {
					newContents[x][y][z] = contents[z][y][(sizeZ - 1) - x];
				}
			}
		}

		return new BluePrint(newContents, (sizeZ - 1) - anchorZ, anchorY, anchorX);
	}
}