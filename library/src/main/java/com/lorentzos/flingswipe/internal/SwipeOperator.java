package com.lorentzos.flingswipe.internal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.MotionEvent;
import android.view.View;

/**
 * Responsible for the swipe operations of the given view.
 */
public class SwipeOperator implements View.OnTouchListener, OnSwipeListener {

	private final CardEventListener cardEventListener;
	private final float baseRotationDegrees;
	private boolean isAnimationRunning;
	private TouchEvent touchEvent;
	private PointF lastTouchPosition;

	SwipeOperator(CardEventListener flingEventListener) {
		this(15f, flingEventListener);
	}

	public SwipeOperator(float rotationDegrees, CardEventListener cardEventListener) {
		baseRotationDegrees = rotationDegrees;
		this.cardEventListener = cardEventListener;
	}

	/**
	 * Sets the {@link TopView} which should be given the swiping possibility.
	 */
	public void setSwipeView(TopView view) {
		view.setOnTouchListener(this);
		view.setOnSwipeListener(this);
	}

	@Override
	public boolean onTouch(final View view, MotionEvent event) {
		if (event.getActionIndex() != 0 || isAnimationRunning) {
			return false;
		}

		view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

		float relativeY = event.getY();
		float windowX = event.getRawX();
		float windowY = event.getRawY();
		PointF touchPosition = new PointF(windowX, windowY);

		switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				touchEvent = new TouchEvent(baseRotationDegrees, view, touchPosition, relativeY);
				lastTouchPosition = touchPosition;
				return true;

			case MotionEvent.ACTION_MOVE:

				if (TouchUtil.minorMovement(touchPosition, lastTouchPosition)) {
					return false;
				}

				ScrollProgress scrollProgress = touchEvent.moveView(touchPosition);
				cardEventListener.onScroll(view, scrollProgress.progress, scrollProgress.direction);

				lastTouchPosition = touchPosition;
				return true;

			case MotionEvent.ACTION_UP:
				isAnimationRunning = true;

				ScrollProgress scrollResult = touchEvent.resultView(touchPosition, new OnCardResult() {
					@Override
					public void onExit(FrameResult frameResult) {
						view.setLayerType(View.LAYER_TYPE_NONE, null);

						switch (frameResult.getEndEvent()) {
							case EndEvent.EXIT:
								cardEventListener.onCardExited(view, frameResult.getDirection());
								break;
							case EndEvent.CLICK:
								cardEventListener.onClick(view);
								break;
							case EndEvent.RECENTER:
								cardEventListener.onRecenter(view);
								break;
						}
						isAnimationRunning = false;
					}
				});
				cardEventListener.onScroll(view, scrollResult.progress, scrollResult.direction);

				lastTouchPosition = touchPosition;
				touchEvent = null;
				break;
			case MotionEvent.ACTION_CANCEL:
				touchEvent = null;
				break;
		}

		return false;
	}

	@Override
	public void swipe(final View view, @Direction final int direction) {
		new SwipeEvent(baseRotationDegrees, view).resultView(direction, new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				cardEventListener.onCardExited(view, direction);
			}
		});
	}
}





