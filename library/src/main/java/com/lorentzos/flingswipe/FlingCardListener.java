package com.lorentzos.flingswipe;

import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;

import com.lorentzos.flingswipe.internal.FrameData;
import com.lorentzos.flingswipe.internal.TargetPosition;
import com.lorentzos.flingswipe.internal.TouchEvent;

import static java.lang.Math.abs;

/**
 * Created by dionysis_lorentzos on 5/8/14
 * for package com.lorentzos.swipecards
 * and project Swipe cards.
 * Use with caution dinausaurs might appear!
 */

public class FlingCardListener implements View.OnTouchListener {

	private static final double ON_CLICK_PIXEL_SENSITIVITY = 4.0;

	private final FlingListener mFlingListener;
	private final Object dataObject;
	private final float BASE_ROTATION_DEGREES;
	private final View frame;
	private boolean isAnimationRunning;
	private FrameData frameData;
	private TouchEvent touchEvent;
	private final CardFrame cardFrame;
	private PointF framePosition;
	private PointF lastTouchPosition;

	public FlingCardListener(View frame, Object itemAtPosition, FlingListener flingListener) {
		this(frame, itemAtPosition, 15f, flingListener);
	}

	public FlingCardListener(View frame, Object itemAtPosition, float rotation_degrees, FlingListener flingListener) {
		this.frame = frame;
		dataObject = itemAtPosition;
		BASE_ROTATION_DEGREES = rotation_degrees;
		mFlingListener = flingListener;

		cardFrame = new CardFrame(frame);
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if (event.getActionIndex() != 0) {
			return false;
		}

		float x = event.getX();
		float y = event.getY();
		PointF touchPosition = new PointF(x, y);

		switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				frameData = FrameData.fromView(view);

				touchEvent = new TouchEvent(BASE_ROTATION_DEGREES, frameData, touchPosition);
				view.getParent().requestDisallowInterceptTouchEvent(true);

				lastTouchPosition = touchPosition;
				return true;

			case MotionEvent.ACTION_MOVE:
				if (abs(touchPosition.x - lastTouchPosition.x) < ON_CLICK_PIXEL_SENSITIVITY &&
						abs(touchPosition.y - lastTouchPosition.y) < ON_CLICK_PIXEL_SENSITIVITY) {
					return false;
				}

				TargetPosition targetPosition = touchEvent.move(touchPosition);

				frame.setTranslationX(targetPosition.getTranslationX());
				frame.setTranslationY(targetPosition.getTranslationY());
				frame.setRotation(targetPosition.getRotation());

				mFlingListener.onScroll(targetPosition.getScrollProgress());

				lastTouchPosition = touchPosition;
				return true;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				lastTouchPosition = touchPosition;

				switch (touchEvent.reset(x)) {
					case RECENTER:
						cardFrame.recenter();
						mFlingListener.onScroll(0.0f);
						return true;
					case CLICK:
						cardFrame.recenter();
						mFlingListener.onScroll(0.0f);
						return true;
					case LEFT_ABOVE:
						isAnimationRunning = true;

						// Left Swipe
						cardFrame.selectedLeftTop(framePosition, new CardFrame.OnCardExited() {
							@Override
							public void call() {
								mFlingListener.onCardExited();
								mFlingListener.leftExit(dataObject);
								isAnimationRunning = false;
							}
						});
						mFlingListener.onScroll(-1.0f);
						break;

					case LEFT_BELOW:
						isAnimationRunning = true;

						// Left Swipe
						cardFrame.selectedLeftBottom(framePosition, new CardFrame.OnCardExited() {
							@Override
							public void call() {
								mFlingListener.onCardExited();
								mFlingListener.leftExit(dataObject);
								isAnimationRunning = false;
							}
						});
						mFlingListener.onScroll(-1.0f);
						return true;
					case RIGHT_ABOVE:
						isAnimationRunning = true;

						// Right Swipe
						cardFrame.selectedRightTop(framePosition, new CardFrame.OnCardExited() {
							@Override
							public void call() {
								mFlingListener.onCardExited();
								mFlingListener.rightExit(dataObject);
								isAnimationRunning = false;
							}
						});
						mFlingListener.onScroll(1.0f);
						return true;
					case RIGHT_BELOW:
						isAnimationRunning = true;

						// Right Swipe
						cardFrame.selectedRightBottom(framePosition, new CardFrame.OnCardExited() {
							@Override
							public void call() {
								mFlingListener.onCardExited();
								mFlingListener.rightExit(dataObject);
								isAnimationRunning = false;
							}
						});
						mFlingListener.onScroll(1.0f);
						return true;
				}

				view.getParent().requestDisallowInterceptTouchEvent(false);
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
		//		selected(true, objectY, 200); // TODO: 29/10/16
	}

	/**
	 * Starts a default right exit animation.
	 */
	public void selectRight() {
		if (isAnimationRunning) {
			return;
		}
		//		selected(false, objectY, 200); // TODO: 29/10/16
	}

	public boolean isTouching() {
		return true;
		//		return mActivePointerId != INVALID_POINTER_ID;
	}

	public PointF getLastPoint() {
		return framePosition;
	}

	public interface FlingListener {
		void onCardExited();

		void leftExit(Object dataObject);

		void rightExit(Object dataObject);

		void onClick(Object dataObject);

		void onScroll(float scrollProgressPercent);
	}

}





