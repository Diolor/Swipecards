package com.lorentzos.flingswipe.internal;

import android.animation.AnimatorListenerAdapter;
import android.view.View;

/**
 *
 */
class SwipeEvent {

	private final float rotationFactor;
	private final FrameData frameData;
	private final View frame;

	SwipeEvent(float rotationFactor, View frame) {
		this.rotationFactor = rotationFactor;
		frameData = FrameData.fromView(frame);
		this.frame = frame;
	}

	void resultView(@Direction int direction, AnimatorListenerAdapter onAnimationEnd) {
		PointF f = new PointF(frame.getX(), frame.getY());

		float adjustedFactor = 0;

		if (Direction.LEFT == direction || Direction.RIGHT == direction) {
			adjustedFactor = rotationFactor;
			if (direction == Direction.LEFT) {
				adjustedFactor *= -1;
			}
		}

		frameData.getExitPosition(f, direction, adjustedFactor)
				.exit(frame, onAnimationEnd);
	}

}
