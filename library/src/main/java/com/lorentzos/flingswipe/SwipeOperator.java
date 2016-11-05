package com.lorentzos.flingswipe;

import android.view.MotionEvent;
import android.view.View;

import com.lorentzos.flingswipe.internal.EndEvent;
import com.lorentzos.flingswipe.internal.FrameResult;
import com.lorentzos.flingswipe.internal.OnCardResult;
import com.lorentzos.flingswipe.internal.PointF;
import com.lorentzos.flingswipe.internal.TouchEvent;
import com.lorentzos.flingswipe.internal.TouchUtil;

/**
 *
 */
public class SwipeOperator implements View.OnTouchListener {

	private final CardEventListener cardEventListener;
	private final float baseRotationDegrees;
	private boolean isAnimationRunning;
	private TouchEvent touchEvent;
	private PointF lastTouchPosition;

	public SwipeOperator() {
		this(15f, new SimpleCardEventListener());
	}

	public SwipeOperator(CardEventListener flingEventListener) {
		this(15f, flingEventListener);
	}

	public SwipeOperator(float rotationDegrees, CardEventListener cardEventListener) {
		baseRotationDegrees = rotationDegrees;
		this.cardEventListener = cardEventListener;
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

				float scrollProgress = touchEvent.moveView(touchPosition);
				cardEventListener.onScroll(view, scrollProgress);

				lastTouchPosition = touchPosition;
				return true;

			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				isAnimationRunning = true;

				float scrollResult = touchEvent.resultView(touchPosition, new OnCardResult() {
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
				cardEventListener.onScroll(view, scrollResult);

				lastTouchPosition = touchPosition;
				break;
		}

		return false;
	}

}





