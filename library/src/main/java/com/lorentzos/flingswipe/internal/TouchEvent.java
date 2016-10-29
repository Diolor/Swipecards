package com.lorentzos.flingswipe.internal;

import android.graphics.PointF;

import static com.lorentzos.flingswipe.internal.Type.TOUCH_BOTTOM;
import static java.lang.Math.abs;

/**
 *
 */
public class TouchEvent {

	private static final double ON_CLICK_PIXEL_SENSITIVITY = 4.0;

	private final float rotationFactor;
	@Type
	private final int touchType;
	private final FrameData frameData;
	private final PointF initialPosition;

	public TouchEvent(float rotationFactor, FrameData frameData, PointF initialPosition) {
		this.rotationFactor = rotationFactor;
		this.frameData = frameData;

		touchType = frameData.getTouchType(initialPosition.y);

		this.initialPosition = initialPosition;
	}

	public UpdatePosition move(PointF movePosition) {
		float dx = movePosition.x - initialPosition.x;
		float dy = movePosition.y - initialPosition.y;

		float rotationFactor = this.rotationFactor;
		if (touchType == TOUCH_BOTTOM) {
			rotationFactor *= -1;
		}

		return frameData.createUpdatePosition(dx, dy, rotationFactor);
	}

	public ResultPosition result(PointF endPosition) {
		float scrollProgress = frameData.getScrollProgress();

		if (abs(scrollProgress + 1) == 0) {
			if (touchType == TOUCH_BOTTOM) {
				return new ResultPosition(ResultState.LEFT_BOTTOM, scrollProgress);
			}
			return new ResultPosition(ResultState.LEFT_TOP, scrollProgress);
		}
		if (abs(scrollProgress - 1) == 0) {
			if (touchType == TOUCH_BOTTOM) {
				return new ResultPosition(ResultState.RIGHT_BOTTOM, scrollProgress);
			}
			return new ResultPosition(ResultState.RIGHT_TOP, scrollProgress);
		}

		return new ResultPosition(ResultState.RECENTER, 0);
	}

}
