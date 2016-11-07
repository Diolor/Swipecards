package com.lorentzos.flingswipe.internal;

import android.graphics.PointF;
import android.view.View;
import android.view.animation.OvershootInterpolator;

/**
 * The recentering position of the frame.
 */
class RecenterPosition extends PointF {

	private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(1.5f);
	private static final int DURATION = 200;

	RecenterPosition(float startX, float startY) {
		super(startX, startY);
	}

	void recenter(View frame) {
		frame.animate()
				.setDuration(DURATION)
				.setInterpolator(OVERSHOOT_INTERPOLATOR)
				.x(x)
				.y(y)
				.rotation(0);
	}

}
