package com.lorentzos.flingswipe.internal;

/**
 * The updating position of the frame.
 */
class UpdatePosition {
	private final float updateX;
	private final float updateY;
	private final float rotation;
	private final float scrollProgress;

	UpdatePosition(float updateX, float updateY, float rotation, float scrollProgress) {
		this.updateX = updateX;
		this.updateY = updateY;
		this.rotation = rotation;
		this.scrollProgress = scrollProgress;
	}

	float getUpdateX() {
		return updateX;
	}

	float getUpdateY() {
		return updateY;
	}

	float getRotation() {
		return rotation;
	}

	float getScrollProgress() {
		return scrollProgress;
	}

}
