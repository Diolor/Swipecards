package com.lorentzos.flingswipe.internal;

import android.support.annotation.FloatRange;

/**
 *
 */
public class ScrollProgress {
	@Direction
	public final int direction;
	@FloatRange(from = 0, to = 1.0)
	public final float progress;

	public ScrollProgress(int direction, float progress) {
		this.direction = direction;
		this.progress = progress;
	}
}
