package com.chocohead.buildcraft;

import com.chocohead.buildcraft.entities.BlockEntity.Texture;

public enum LaserKind {
	Red(Texture.Red),
	Blue(Texture.Blue),
	Stripes(Texture.Stripes);
	public final Texture texture;

	private LaserKind(Texture texture) {
		this.texture = texture;
	}
}