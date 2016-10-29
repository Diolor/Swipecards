package com.lorentzos.flingswipe.internal;

/**
 *
 */
public class UpdatePosition {
	private final float translationX;
	private final float translationY;
	private final float rotation;
	private final float scrollProgress;

	UpdatePosition(float translationX, float translationY, float rotation, float scrollProgress) {
		this.translationX = translationX;
		this.translationY = translationY;
		this.rotation = rotation;
		this.scrollProgress = scrollProgress;
	}

	public float getTranslationX() {
		return translationX;
	}

	public float getTranslationY() {
		return translationY;
	}

	public float getRotation() {
		return rotation;
	}

	public float getScrollProgress() {
		return scrollProgress;
	}
}
