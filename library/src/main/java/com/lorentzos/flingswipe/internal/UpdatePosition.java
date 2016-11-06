package com.lorentzos.flingswipe.internal;

import android.view.View;

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

	float getScrollProgress() {
		return scrollProgress;
	}

	void move(View frame) {
		frame.setX(updateX);
		frame.setY(updateY);
		frame.setRotation(rotation);
	}
}
