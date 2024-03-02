package com.chocohead.buildcraft.blocks;

import net.minecraft.src.client.renderer.block.icon.Icon;
import net.minecraft.src.client.renderer.block.icon.IconRegister;
import net.minecraft.src.game.MathHelper;
import net.minecraft.src.game.Direction.EnumDirection;
import net.minecraft.src.game.block.BlockContainer;
import net.minecraft.src.game.block.Material;
import net.minecraft.src.game.block.tileentity.TileEntity;
import net.minecraft.src.game.entity.EntityLiving;
import net.minecraft.src.game.level.IBlockAccess;
import net.minecraft.src.game.level.World;

import com.chocohead.buildcraft.Utils;
import com.chocohead.buildcraft.api.IPipeConnection;

public class QuarryBlock extends BlockContainer implements IPipeConnection {
	private Icon frontTexture, sideTexture, topTexture;

	public QuarryBlock(int id) {
		super(id, Material.iron);

		setHardness(1.5F);
		setResistance(10F);
		setSound(soundStone);
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
	protected TileEntity getBlockEntity() {		
		return new QuarryTileEntity();
	}

	@Override
	public boolean isPipeConnected(IBlockAccess world, int x1, int y1, int z1, int x2, int y2, int z2) {
		return true;
	}

	@Override
	public void onBlockRemoval(World world, int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (tile instanceof QuarryTileEntity) ((QuarryTileEntity) tile).destroy();

		super.onBlockRemoval(world, x, y, z);
	}
}