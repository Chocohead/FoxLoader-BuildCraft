package com.chocohead.buildcraft.blocks;

import net.minecraft.common.block.icon.Icon;
import net.minecraft.common.block.icon.IconRegister;
import net.minecraft.common.util.math.MathHelper;
import net.minecraft.common.util.Direction.EnumDirection;
import net.minecraft.common.block.children.BlockContainer;
import net.minecraft.common.block.data.Materials;
import net.minecraft.common.block.sound.StepSounds;
import net.minecraft.common.block.tileentity.TileEntity;
import net.minecraft.common.entity.EntityLiving;
import net.minecraft.common.world.BlockAccess;
import net.minecraft.common.world.World;

import com.chocohead.buildcraft.Utils;
import com.chocohead.buildcraft.api.IPipeConnection;

public class QuarryBlock extends BlockContainer implements IPipeConnection {
	private Icon frontTexture, sideTexture, topTexture;

	public QuarryBlock(String id) {
		super(id, Materials.METAL);

		setHardness(1.5F);
		setResistance(10F);
		setSound(StepSounds.SOUND_STONE);
	}

	@Override
	public void registerIcons(IconRegister register) {
		frontTexture = register.registerIcon("buildcraft/quarryFront");
		sideTexture = register.registerIcon("buildcraft/quarrySide");
		topTexture = register.registerIcon("buildcraft/quarryTop");
	}

	@Override
	public Icon getIcon(int face, int meta) {
		if (meta == 0 ? face == 3 : face == meta) {
			return frontTexture;
		} else if (face == 1) {
			return topTexture;
		} else {
			return sideTexture;
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving placer) {
		super.onBlockPlacedBy(world, x, y, z, placer);

		//EnumDirection orientation = Utils.getHortizontalDirection(new Position(placer.posX, placer.posY, placer.posZ), new Position(x, y, z));
		EnumDirection orientation;
        switch(MathHelper.floor_double((placer.rotationYaw * 4F / 360F) + 0.5) & 3) {
            case 0:
                orientation = EnumDirection.SOUTH;
                break;
            case 1:
            	orientation = EnumDirection.WEST;
                break;
            case 2:
            	orientation = EnumDirection.NORTH;
                break;
            case 3:
            	orientation = EnumDirection.EAST;
            	break;
            default:
            	throw new IllegalStateException();
        }
		world.setBlockMetadataWithNotify(x, y, z, Utils.reverseDirection(orientation).ordinal());
	}

	@Override
	public TileEntity getBlockEntity() {		
		return new QuarryTileEntity();
	}

	@Override
	public boolean isPipeConnected(BlockAccess world, int x1, int y1, int z1, int x2, int y2, int z2) {
		return true;
	}

	@Override
	public void onBlockRemoval(World world, int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (tile instanceof QuarryTileEntity) ((QuarryTileEntity) tile).destroy();

		super.onBlockRemoval(world, x, y, z);
	}
}