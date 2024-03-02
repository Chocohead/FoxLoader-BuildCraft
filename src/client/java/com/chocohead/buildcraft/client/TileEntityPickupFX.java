package com.chocohead.buildcraft.client;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.client.particle.EntityFX;
import net.minecraft.src.client.renderer.Tessellator;
import net.minecraft.src.client.renderer.entity.RenderManager;
import net.minecraft.src.game.MathHelper;
import net.minecraft.src.game.block.tileentity.TileEntity;
import net.minecraft.src.game.entity.Entity;
import net.minecraft.src.game.entity.other.EntityItem;
import net.minecraft.src.game.level.World;

import com.chocohead.buildcraft.Utils;

public class TileEntityPickupFX extends EntityFX {
	private final Entity entity;
    private final TileEntity tile;
    private final float yOffset;

	public TileEntityPickupFX(World world, EntityItem item, TileEntity tile) {
        super(world, item.posX, item.posY, item.posZ, item.motionX, item.motionY, item.motionZ);
        entity = item;
        this.tile = tile;
        particleMaxAge = 3;
        yOffset = Utils.getPipeHeight(item.item);
    }

    @Override
    public void renderParticle(Tessellator instance, float deltaTicks, float xRot, float xzRot, float yRot, float yzRot, float xyRot) {
        float age = (particleAge + deltaTicks) / particleMaxAge;
        age *= age;
        double ex = entity.posX;
        double ey = entity.posY;
        double ez = entity.posZ;
        double ox = tile.xCoord + 0.5;
        double oy = tile.yCoord + yOffset;
        double oz = tile.zCoord + 0.5;
        double rndrX = ex + (ox - ex) * age;
        double rndrY = ey + (oy - ey) * age;
        double rndrZ = ez + (oz - ez) * age;
        int xi = MathHelper.floor_double(rndrX);
        int yi = MathHelper.floor_double(rndrY + yOffset / 2F);
        int zi = MathHelper.floor_double(rndrZ);
        float light = worldObj.getLightBrightness(xi, yi, zi);
        rndrX -= interpPosX;
        rndrY -= interpPosY;
        rndrZ -= interpPosZ;
        GL11.glColor4f(light, light, light, 1F);
        RenderManager.instance.renderEntityWithPosYaw(entity, rndrX, rndrY, rndrZ, entity.rotationYaw, deltaTicks);
    }

    @Override
    public void onUpdate() {
        if (++particleAge == particleMaxAge) setDead();
    }

    @Override
    public int getFXLayer() {
        return 3;
    }
}