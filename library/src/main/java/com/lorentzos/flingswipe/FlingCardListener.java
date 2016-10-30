package com.lorentzos.flingswipe;

import android.view.MotionEvent;
import android.view.View;

import com.lorentzos.flingswipe.internal.Direction;
import com.lorentzos.flingswipe.internal.EndEvent;
import com.lorentzos.flingswipe.internal.FrameResult;
import com.lorentzos.flingswipe.internal.OnCardResult;
import com.lorentzos.flingswipe.internal.PointF;
import com.lorentzos.flingswipe.internal.TouchEvent;
import com.lorentzos.flingswipe.internal.TouchUtil;

/**
 * Created by dionysis_lorentzos on 5/8/14
 * for package com.lorentzos.swipecards
 * and project Swipe cards.
 * Use with caution dinausaurs might appear!
 */

public class FlingCardListener implements View.OnTouchListener {

	private final FlingListener flingListener;
	private final float baseRotationDegrees;
	private boolean isAnimationRunning;
	private TouchEvent touchEvent;
	private PointF lastTouchPosition;

	public FlingCardListener() {
		this(15f);

	}

	public FlingCardListener(float rotationDegrees) {
		baseRotationDegrees = rotationDegrees;
		flingListener = new SimpleFlingListener();
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
				flingListener.onScroll(scrollProgress);

				lastTouchPosition = touchPosition;
				return true;

			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				isAnimationRunning = true;

				float scrollResult = touchEvent.resultView(touchPosition, new OnCardResult() {
					@Override
					public void onExit(FrameResult frameResult) {
						isAnimationRunning = false;
						view.setLayerType(View.LAYER_TYPE_NONE, null);

						switch (frameResult.getEndEvent()) {
							case EndEvent.EXIT:
								flingListener.onCardExited(frameResult.getDirection());
								break;
							case EndEvent.CLICK:
								flingListener.onClick(view);
								break;
							case EndEvent.RECENTER:
								flingListener.onRecenter();
								break;
						}
					}
				});
				flingListener.onScroll(scrollResult);

				lastTouchPosition = touchPosition;
				break;
		}

		return false;
	}

	/**
	 * Starts a default left exit animation.
	 */
	public void selectLeft() {
		if (isAnimationRunning) {
			return;
		}
		//		exit(true, objectY, 200); // TODO: 29/10/16
	}

	/**
	 * Starts a default right exit animation.
	 */
	public void selectRight() {
		if (isAnimationRunning) {
			return;
		}
		//		exit(false, objectY, 200); // TODO: 29/10/16
	}

	public boolean isTouching() {
		return true;
		//		return mActivePointerId != INVALID_POINTER_ID;
	}

	public android.graphics.PointF getLastPoint() {
		return new android.graphics.PointF(0, 0); // TODO: 29/10/16
	}

	public interface FlingListener {
		void onCardExited(@Direction int direction);

		void onScroll(float scrollProgressPercent);

		void onRecenter();

		void onClick(View view);
	}

}





