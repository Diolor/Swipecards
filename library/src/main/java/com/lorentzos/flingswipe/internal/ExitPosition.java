package com.lorentzos.flingswipe.internal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 *
 */

public class ExitPosition {

	private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
	private final float exitX;
	private final float exitY;
	private final float exitRotation;

	public ExitPosition(float exitX, float exitY, float exitRotation) {

		this.exitX = exitX;
		this.exitY = exitY;
		this.exitRotation = exitRotation;
	}

	public void exit(View frame, final OnCardExited onCardExited) {
		frame.animate()
				.setInterpolator(ACCELERATE_INTERPOLATOR)
				.setDuration(100)
				.x(exitX)
				.y(exitY)
				.rotation(exitRotation)
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						onCardExited.call();
					}
				});
	}
}
