package com.lorentzos.flingswipe;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lorentzos.flingswipe.internal.Direction;
import com.lorentzos.flingswipe.internal.OnCardResult;
import com.lorentzos.flingswipe.internal.EndType;
import com.lorentzos.flingswipe.internal.TouchEvent;

import static java.lang.Math.abs;

/**
 * Created by dionysis_lorentzos on 5/8/14
 * for package com.lorentzos.swipecards
 * and project Swipe cards.
 * Use with caution dinausaurs might appear!
 */

public class FlingCardListener implements View.OnTouchListener, OnCardResult {

	private static final double ON_CLICK_PIXEL_SENSITIVITY = 4.0;

	private final FlingListener mFlingListener;
	private final Object dataObject;
	private final float baseRotationDegrees;
	private boolean isAnimationRunning;
	private TouchEvent touchEvent;
	private PointF lastTouchPosition;

	public FlingCardListener(Object itemAtPosition, FlingListener flingListener) {
		this(itemAtPosition, 15f, flingListener);
	}

	public FlingCardListener(Object itemAtPosition, float rotation_degrees, FlingListener flingListener) {
		dataObject = itemAtPosition;
		baseRotationDegrees = rotation_degrees;
		mFlingListener = flingListener;

	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if (event.getActionIndex() != 0) {
			return false;
		}
		boolean b = view instanceof TextView;
		Log.wtf("FlingCardListener", "onTouch " + b);
		view = ((ViewGroup) view).getChildAt(0); // TODO: 29/10/16

		float x = event.getX();
		float y = event.getY();
		PointF touchPosition = new PointF(x, y);

		switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:

				touchEvent = new TouchEvent(baseRotationDegrees, view, touchPosition);
				lastTouchPosition = touchPosition;
				return true;

			case MotionEvent.ACTION_MOVE:
				if (abs(touchPosition.x - lastTouchPosition.x) < ON_CLICK_PIXEL_SENSITIVITY &&
						abs(touchPosition.y - lastTouchPosition.y) < ON_CLICK_PIXEL_SENSITIVITY) {
					return false;
				}

				float scrollProgress = touchEvent.moveView(touchPosition);
				mFlingListener.onScroll(scrollProgress);

				lastTouchPosition = touchPosition;
				return true;

			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				isAnimationRunning = true;

				float scrollResult = touchEvent.resultView(this);
				mFlingListener.onScroll(scrollResult);

				lastTouchPosition = touchPosition;
				break;
		}

		return false;
	}

	@Override
	public void onExit(@EndType int exit, @Direction int direction) {

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

	public PointF getLastPoint() {
		return new PointF(0, 0); // TODO: 29/10/16
	}

	public interface FlingListener {
		void onCardExited();

		void leftExit(Object dataObject);

		void rightExit(Object dataObject);

		void onClick(Object dataObject);

		void onScroll(float scrollProgressPercent);
	}

}





