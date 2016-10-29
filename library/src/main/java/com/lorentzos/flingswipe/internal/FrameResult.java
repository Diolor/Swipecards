package com.lorentzos.flingswipe.internal;

import static com.lorentzos.flingswipe.internal.Direction.LEFT;
import static com.lorentzos.flingswipe.internal.Direction.CENTER;
import static com.lorentzos.flingswipe.internal.Direction.RIGHT;
import static com.lorentzos.flingswipe.internal.EndType.EXIT;
import static com.lorentzos.flingswipe.internal.EndType.RECENTER;
import static java.lang.Math.abs;

/**
 * The resulting details for the view when the user moves up or cancels
 * the drag events.
 */
class FrameResult {

	private final int type;
	private final int direction;

	/**
	 * Creates a new instance for the given scrolling progress
	 *
	 * @param scrollProgress the progress of scrolling for the view.
	 * @return a new instance of {@link FrameResult}
	 */
	static FrameResult fromScrollProgress(float scrollProgress) {

		if (abs(scrollProgress + 1) == 0) {
			return new FrameResult(EXIT, LEFT);

		}
		if (abs(scrollProgress - 1) == 0) {
			return new FrameResult(EXIT, RIGHT);
		}

		return new FrameResult(RECENTER, CENTER);
	}

	private FrameResult(@EndType int type, @Direction int direction) {
		this.type = type;
		this.direction = direction;
	}

	@EndType
	int getType() {
		return type;
	}

	@Direction
	int getDirection() {
		return direction;
	}

}
