package com.lorentzos.flingswipe.internal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

import static com.lorentzos.flingswipe.internal.Direction.RIGHT;
import static com.lorentzos.flingswipe.internal.EndEvent.CLICK;
import static com.lorentzos.flingswipe.internal.EndEvent.EXIT;
import static com.lorentzos.flingswipe.internal.EndEvent.RECENTER;
import static com.lorentzos.flingswipe.internal.FrameData.fromView;
import static com.lorentzos.flingswipe.internal.TouchType.TOUCH_BOTTOM;
import static com.lorentzos.flingswipe.internal.TouchType.TOUCH_TOP;

/**
 * A touch event with its "lifecycle".
 */
class TouchEvent {

	private final float rotationFactor;
	private final View frame;
	@TouchType
	private final int touchType;
	private final FrameData frameData;
	private final PointF initialPosition;

	private static float adjustRotationFactor(float baseRotation, int touchType, int direction) {
		float targetRotation = baseRotation;
		if (touchType == TOUCH_TOP) {
			targetRotation *= -1;
		}
		if (direction == RIGHT) {
			targetRotation *= -1;
		}
		return targetRotation;
	}

	TouchEvent(float rotationFactor, View frame, PointF initialPosition, float relativeY) {
		this.rotationFactor = rotationFactor;
		frameData = fromView(frame);
		this.frame = frame;

		touchType = frameData.getTouchType(relativeY);

		this.initialPosition = initialPosition;
	}

	/**
	 * Notifies to move the view based on the given touch {@link android.view.MotionEvent#ACTION_MOVE}
	 * event.
	 *
	 * @param movePosition the position of the pointer at the given time.
	 * @return the scroll progress of the view
	 */
	ScrollProgress moveView(PointF movePosition) {
		float dx = movePosition.x - initialPosition.x;
		float dy = movePosition.y - initialPosition.y;

		float rotationFactor = this.rotationFactor;
		if (touchType == TOUCH_BOTTOM) {
			rotationFactor *= -1;
		}

		UpdatePosition updatePosition = frameData.createUpdatePosition(dx, dy, rotationFactor);
		updatePosition.move(frame);

		return updatePosition.scrollProgress;
	}

	/**
	 * Notifies the view that a result should happen.
	 *
	 * @param endPosition  the position of the pointer at the given time.
	 * @param onCardResult the callback triggered when the event finishes.
	 * @return the scroll progress of the view
	 */
	ScrollProgress resultView(PointF endPosition, final OnCardResult onCardResult) {

		float dx = endPosition.x - initialPosition.x;
		float dy = endPosition.y - initialPosition.y;
		ScrollProgress scrollProgress = frameData.getScrollProgress(dx, dy);

		final FrameResult frameResult;
		if (TouchUtil.minorMovement(endPosition, initialPosition)) {
			frameResult = FrameResult.click();
		} else {
			frameResult = FrameResult.fromScrollProgress(scrollProgress);
		}

		switch (frameResult.getEndEvent()) {
			case EXIT:
				PointF framePosition = new PointF(frame.getX(), frame.getY());
				float rotation = adjustRotationFactor(rotationFactor, touchType, frameResult.getDirection());

				frameData.getExitPosition(framePosition, frameResult.getDirection(), rotation)
						.exit(frame, new AnimatorListenerAdapter() {
							@Override
							public void onAnimationEnd(Animator animation) {
								onCardResult.onExit(frameResult);
							}
						});

				break;
			case CLICK:
			case RECENTER:
				frameData.getRecenterPosition().recenter(frame);
				onCardResult.onExit(frameResult);
				break;
		}

		return scrollProgress;
	}

}
