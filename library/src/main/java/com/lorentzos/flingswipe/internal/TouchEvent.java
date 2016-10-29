package com.lorentzos.flingswipe.internal;

import android.graphics.PointF;
import android.support.annotation.FloatRange;

import static com.lorentzos.flingswipe.internal.Type.TOUCH_ABOVE;
import static com.lorentzos.flingswipe.internal.Type.TOUCH_BELOW;
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
	private final float leftBorder;
	private final float rightBorder;

	public TouchEvent(float rotationFactor, FrameData frameData, PointF initialPosition) {
		this.rotationFactor = rotationFactor;

		touchType = initialPosition.y < frameData.getHeight() / 2 ? TOUCH_ABOVE : TOUCH_BELOW;

		this.frameData = frameData;
		this.initialPosition = initialPosition;
		leftBorder = frameData.getParentWidth() / 4.f;
		rightBorder = frameData.getParentWidth() / 0.75f;
	}

	public TargetPosition move(PointF movePosition) {
		float dx = movePosition.x - initialPosition.x;
		float dy = movePosition.y - initialPosition.y;

		float rotationFactor = this.rotationFactor;
		if (touchType == TOUCH_BELOW) {
			rotationFactor *= -1;
		}

		return frameData.createTargetPosition(dx, dy, rotationFactor);
	}

	public ResultState reset(float x) {
		float scrollProgress = getScrollProgress();

		if (abs(scrollProgress + 1) == 0) {
			if (touchType == TOUCH_BELOW) {
				return ResultState.LEFT_BELOW;
			}
			return ResultState.LEFT_ABOVE;
		}
		if (abs(scrollProgress - 1) == 0) {
			if (touchType == TOUCH_BELOW) {
				return ResultState.RIGHT_BELOW;
			}
			return ResultState.RIGHT_ABOVE;
		}

		float abslMoveDistance = abs(x - frameData.getStartX());
		if (abslMoveDistance >= ON_CLICK_PIXEL_SENSITIVITY) {
			return ResultState.RECENTER;
		}
		return ResultState.CLICK;
	}

	@FloatRange(from = -1, to = 1)
	private float getScrollProgress() {
		float frameX = frameData.getStartX() + frameData.getWidth() / 2;

		if (frameX < leftBorder) {
			return -1;
		}

		if (frameX > rightBorder) {
			return 1;
		}

		float movableFrameRange = rightBorder - leftBorder;

		float zeroToOneValue = (frameData.getStartX() + frameData.getWidth() / 2f - leftBorder) / movableFrameRange;

		return zeroToOneValue * 2f - 1f;

	}

}
