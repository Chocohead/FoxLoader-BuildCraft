package com.chocohead.buildcraft.events;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.common.entity.Entity;

import com.fox2code.foxevents.Event;
import com.fox2code.foxevents.EventHolder;

public final class EntityRendererRegistrationEvent extends Event {
	public static final EventHolder<EntityRendererRegistrationEvent> REGISTER_EVENT = EventHolder.getHolderFromEvent(EntityRendererRegistrationEvent.class);
	private final EntityRendererManager manager;

	public EntityRendererRegistrationEvent(EntityRendererManager manager) {
		this.manager = manager;
	}

	public EntityRendererManager getManager() {
		return manager;
	}

	public <T extends Entity> void registerEntityRenderer(Class<T> entity, EntityRenderer<? super T> renderer) {
		manager.addEntityRenderer(entity, renderer);
	}
}