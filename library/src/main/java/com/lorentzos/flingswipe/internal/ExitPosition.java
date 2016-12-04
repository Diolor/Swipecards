package com.lorentzos.flingswipe.internal;

import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * The exit position of the frame.
 */
class ExitPosition {

	private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
	private static final int DURATION = 100*10;
	private final float exitX;
	private final float exitY;
	private final float exitRotation;

	ExitPosition(float exitX, float exitY, float exitRotation) {
		this.exitX = exitX;
		this.exitY = exitY;
		this.exitRotation = exitRotation;
	}

	void exit(View frame, AnimatorListenerAdapter onAnimationEnd) {
		frame.animate()
				.setInterpolator(ACCELERATE_INTERPOLATOR)
				.setDuration(DURATION)
				.x(exitX)
				.y(exitY)
				.rotation(exitRotation)
				.setListener(onAnimationEnd);
	}
}
