package com.lorentzos.flingswipe.internal;

/**
 * The updating position of the frame.
 */
class UpdatePosition {
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

	float getTranslationX() {
		return translationX;
	}

	float getTranslationY() {
		return translationY;
	}

	float getRotation() {
		return rotation;
	}

	float getScrollProgress() {
		return scrollProgress;
	}

	@Override
	public String toString() {
		return "UpdatePosition{" +
				"translationX=" + translationX +
				", translationY=" + translationY +
				", rotation=" + rotation +
				", scrollProgress=" + scrollProgress +
				'}';
	}
}
