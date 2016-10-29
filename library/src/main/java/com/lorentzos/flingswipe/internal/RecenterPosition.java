package com.lorentzos.flingswipe.internal;

import android.graphics.PointF;
import android.view.View;
import android.view.animation.OvershootInterpolator;

/**
 *
 */
public class RecenterPosition extends PointF {

	private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(1.5f);

	public RecenterPosition(float startX, float startY) {
		super(startX, startY);
	}

	public void recenter(View frame) {
		frame.animate()
				.setDuration(200)
				.setInterpolator(OVERSHOOT_INTERPOLATOR)
				.x(x)
				.y(y)
				.rotation(0);
	}

}
