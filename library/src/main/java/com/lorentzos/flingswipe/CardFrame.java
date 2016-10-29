package com.lorentzos.flingswipe;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.PointF;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 *
 */
public class CardFrame {

	@SuppressWarnings("ConstantMathCall")
	private static final float MAX_COS = (float) StrictMath.cos(Math.toRadians(45));
	private static final float BASE_ROTATION_DEGREES = 15f;

	private final float startX;
	private final float startY;
	private final float height;
	private final float width;
	private final float parentWidth;
	private final View frame;

	public CardFrame(View frame) {
		startX = frame.getX();
		startY = frame.getY();

		height = frame.getHeight();
		width = frame.getWidth();

		parentWidth = ((ViewGroup) frame.getParent()).getWidth();
		this.frame = frame;
	}

	public void recenter() {
		frame.animate()
				.setInterpolator(new OvershootInterpolator(1.5f))
				.setDuration(200)
				.x(startX)
				.y(startY)
				.rotation(0);
	}

	void selectedLeftTop(PointF framePosition, OnCardExited onCardExited) {
		selectedLeft(framePosition, onCardExited).rotation(getExitRotationTopLeft());
	}

	void selectedLeftBottom(PointF framePosition, OnCardExited onCardExited) {
		selectedLeft(framePosition, onCardExited).rotation(getExitRotationBottomLeft());
	}

	private ViewPropertyAnimator selectedLeft(PointF framePosition, final OnCardExited onCardExited) {
		float exitX = -width - getRotationWidthOffset();
		float exitY = calculateExitY(framePosition, -width);

		return selected(onCardExited, exitX, exitY);
	}

	void selectedRightTop(PointF framePosition, OnCardExited onCardExited) {
		selectedRight(framePosition, onCardExited).rotation(getExitRotationBottomLeft());
	}

	void selectedRightBottom(PointF framePosition, OnCardExited onCardExited) {
		selectedRight(framePosition, onCardExited).rotation(getExitRotationTopLeft());
	}

	private ViewPropertyAnimator selectedRight(PointF framePosition, final OnCardExited onCardExited) {
		float exitX = parentWidth + getRotationWidthOffset();
		float exitY = calculateExitY(framePosition, parentWidth);

		return selected(onCardExited, exitX, exitY);
	}

	private ViewPropertyAnimator selected(final OnCardExited onCardExited, float exitX, float exitY) {
		return frame.animate()
				.setInterpolator(new AccelerateInterpolator())
				.setDuration(100)
				.x(exitX)
				.y(exitY)
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						onCardExited.call();
					}
				});
	}

	private float calculateExitY(PointF framePosition, float exitXPoint) {
		float slope = (framePosition.y - startY) / (framePosition.x - startX);
		float intercept = startY - slope * startX;

		//Your typical y = ax+b linear regression
		return slope * exitXPoint + intercept;
	}

	private float getExitRotationBottomLeft() {
		return BASE_ROTATION_DEGREES * 2.f * (width - startX) / parentWidth;
	}

	private float getExitRotationTopLeft() {
		return -(BASE_ROTATION_DEGREES * 2.f * (width - startX) / parentWidth);
	}

	/**
	 * When the object rotates it's width becomes bigger.
	 * The maximum width is at 45 degrees.
	 * <p/>
	 * The below method calculates the width offset of the rotation.
	 */
	private float getRotationWidthOffset() {
		return width / MAX_COS - width;
	}

	interface OnCardExited {
		void call();
	}
}
