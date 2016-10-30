package com.lorentzos.flingswipe.internal;

import static java.lang.Math.abs;

/**
 * Utility class related to touch events.
 */
public class TouchUtil {
	private static final double ON_CLICK_PIXEL_SENSITIVITY = 4.0;

	// TODO: 30/10/16
	public static boolean minorMovement(PointF touchPosition, PointF lastTouchPosition) {
		return abs(touchPosition.x - lastTouchPosition.x) < ON_CLICK_PIXEL_SENSITIVITY &&
				abs(touchPosition.y - lastTouchPosition.y) < ON_CLICK_PIXEL_SENSITIVITY;
	}
}
