package com.lorentzos.flingswipe.internal;

import android.animation.AnimatorListenerAdapter;
import android.view.View;

/**
 *
 */

public class SwipeEvent {

	private final float rotationFactor;
	private final FrameData frameData;
	private final View frame;

	public SwipeEvent(float rotationFactor, View frame) {
		this.rotationFactor = rotationFactor;
		frameData = FrameData.fromView(frame);
		this.frame = frame;
	}

	public void exit(@Direction int direction, AnimatorListenerAdapter onAnimationEnd) {
		PointF f = new PointF(frame.getX(), frame.getY());

		frameData.getExitPosition(f, direction, rotationFactor)
				.exit(frame, onAnimationEnd);
	}

}
