package com.lorentzos.flingswipe.internal;

import android.support.annotation.FloatRange;
import android.view.View;

import static com.lorentzos.flingswipe.internal.TouchType.TOUCH_BOTTOM;
import static com.lorentzos.flingswipe.internal.TouchType.TOUCH_TOP;

/**
 * Helping class which contains the data and functionality for the flinging view.
 */
class FrameData {
	@SuppressWarnings("ConstantMathCall")
	private static final float MAX_COS = (float) StrictMath.cos(Math.toRadians(45));

	public final float startX;
	public final float startY;
	private final float height;
	private final float width;
	private final float parentWidth;
	private final float leftBorder;
	private final float rightBorder;
	private final float rotationWidthOffset;

	/**
	 * Creates a new instance of {@link FrameData} from a given view.
	 *
	 * @param view the view you would like to generate data from
	 * @return a new object instance
	 */
	static FrameData fromView(View view) {
		PointF framePosition = new PointF(view.getX(), view.getY());
		int parentWidth = ((View) view.getParent()).getWidth();

		return new FrameData(framePosition, view.getHeight(), view.getWidth(), parentWidth);
	}

	/**
	 * When the object rotates it's width becomes bigger.
	 * The maximum width is at 45 degrees.
	 * <p>
	 * The below method calculates the width offset of the rotation.
	 *
	 * @param width the width of the view with 0 degrees rotation.
	 */
	private static float getRotationWidthOffset(float width) {
		return width / MAX_COS - width;
	}

	private FrameData(PointF framePosition, int height, int width, float parentWidth) {
		startX = framePosition.x;
		startY = framePosition.y;
		this.height = height;
		this.width = width;
		this.parentWidth = parentWidth;
		leftBorder = parentWidth * 0.25f;
		rightBorder = parentWidth * 0.75f;

		rotationWidthOffset = getRotationWidthOffset(width);
	}

	/**
	 * Returns if the initial touch happened on the 50% top part or 50% bottom part.
	 *
	 * @param initialTouchY the y axis location of the initial touch
	 * @return the {@link TouchType} of the initial touch on this view
	 */
	@TouchType
	int getTouchType(float initialTouchY) {
		return initialTouchY < startX + height / 2f ? TOUCH_TOP : TOUCH_BOTTOM;
	}

	/**
	 * Creates a new {@link UpdatePosition} for the given differential in x and y axis.
	 *
	 * @param dx             the differential in x axis
	 * @param dy             the differential in y axis
	 * @param rotationFactor the base rotation factor to calculate the rotation
	 * @return the new position this frame should take
	 */
	UpdatePosition createUpdatePosition(float dx, float dy, float rotationFactor) {
		float rotation = 2.f * rotationFactor * dx / parentWidth;
		float scrollProgress = getScrollProgress(dx);

		return new UpdatePosition(dx, dy, rotation, scrollProgress);
	}

	/**
	 * Creates a {@link RecenterPosition} for the view.
	 */
	RecenterPosition getRecenterPosition() {
		return new RecenterPosition(startX, startY);
	}

	/**
	 * Returns the target left {@link ExitPosition} of the view for the given
	 * current position and rotation factor.
	 * <p>
	 * The position is required to make a smooth linear exit from the center.
	 *
	 * @param framePosition  the current position of the view.
	 * @param rotationFactor the base rotation factor to calculate the rotation
	 * @return the left {@link ExitPosition} of the view
	 * @see #getRightExitPoint(PointF, float)
	 */
	ExitPosition getLeftExitPosition(PointF framePosition, float rotationFactor) {
		float exitX = -width - rotationWidthOffset;
		float exitY = calculateExitY(framePosition, exitX);
		float exitRotation = getExitRotation(rotationFactor);

		return new ExitPosition(exitX, exitY, exitRotation);
	}

	/**
	 * Returns the target right {@link ExitPosition} of the view for the given
	 * current position and rotation factor.
	 * <p>
	 * The position is required to make a smooth linear exit from the center.
	 *
	 * @param framePosition  the current position of the view.
	 * @param rotationFactor the base rotation factor to calculate the rotation
	 * @return the right {@link ExitPosition} of the view
	 * @see #getLeftExitPosition(PointF, float)
	 */
	ExitPosition getRightExitPoint(PointF framePosition, float rotationFactor) {
		float exitX = parentWidth;
		float exitY = calculateExitY(framePosition, exitX);
		float exitRotation = getExitRotation(rotationFactor);

		return new ExitPosition(exitX, exitY, exitRotation);
	}

	private float calculateExitY(PointF framePosition, float exitXPoint) {
		float slope = (framePosition.y - startY) / (framePosition.x - startX);
		float intercept = startY - slope * startX;

		//Your typical y = ax+b linear regression
		return slope * exitXPoint + intercept;
	}

	private float getExitRotation(float rotationFactor) {
		return 2.f * rotationFactor * (width - startX) / parentWidth;
	}

	/**
	 * Generates the scroll progress based on the position of the view.
	 *
	 * @param dx
	 * @return todo
	 */
	@FloatRange(from = -1, to = 1)
	float getScrollProgress(float dx) {
		float currentCenterX = startX + dx + width / 2;

		if (currentCenterX < leftBorder) {
			return -1;
		}

		if (currentCenterX > rightBorder) {
			return 1;
		}

		float halfMovableFrameRange = (rightBorder - leftBorder) / 2f;

		if (dx > 0f) {
			float distanceToRightBorder = rightBorder - currentCenterX;
			return 1 - distanceToRightBorder / halfMovableFrameRange;
		}

		if (dx < 0f) {
			float distanceToLeftBorder = currentCenterX - leftBorder;
			return distanceToLeftBorder / halfMovableFrameRange - 1;
		}

		return 0;
	}
}
