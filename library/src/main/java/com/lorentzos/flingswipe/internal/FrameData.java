package com.lorentzos.flingswipe.internal;

import android.view.View;

import static com.lorentzos.flingswipe.internal.Direction.CENTER;
import static com.lorentzos.flingswipe.internal.Direction.DOWN;
import static com.lorentzos.flingswipe.internal.Direction.LEFT;
import static com.lorentzos.flingswipe.internal.Direction.RIGHT;
import static com.lorentzos.flingswipe.internal.Direction.UP;
import static com.lorentzos.flingswipe.internal.TouchType.TOUCH_BOTTOM;
import static com.lorentzos.flingswipe.internal.TouchType.TOUCH_TOP;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Helping class which contains the data and functionality for the flinging view.
 */
class FrameData {
	private final float startX;
	private final float startY;
	private final float height;
	private final float width;
	private final float parentHeight;
	private final float parentWidth;
	private final float leftBorder;
	private final float rightBorder;
	private final float upperBorder;
	private final float bottomBorder;

	/**
	 * Creates a new instance of {@link FrameData} from a given view.
	 *
	 * @param view the view you would like to generate data from
	 * @return a new object instance
	 */
	static FrameData fromView(View view) {
		PointF framePosition = new PointF(view.getX(), view.getY());
		int parentHeight = ((View) view.getParent()).getHeight();
		int parentWidth = ((View) view.getParent()).getWidth();

		return new FrameData(framePosition, view.getHeight(), view.getWidth(), parentHeight, parentWidth);
	}

	/**
	 * When the object rotates it's size dimensions (height/dimension) becomes bigger.
	 * <p>
	 * The below method calculates the dimension offset for the given the rotation.
	 *
	 * @param dimension      the dimension of the view with 0 degrees rotation.
	 * @param rotationFactor the base rotation factor to calculate the dimension offset
	 */
	private static float getRotationSizeOffset(float dimension, float rotationFactor) {
		return (float) (dimension / StrictMath.cos(Math.toRadians(2 * rotationFactor)) - dimension);
	}

	private FrameData(PointF framePosition, int height, int width, float parentHeight, float parentWidth) {
		startX = framePosition.x;
		startY = framePosition.y;
		this.height = height;
		this.width = width;
		this.parentHeight = parentHeight;
		this.parentWidth = parentWidth;
		leftBorder = parentWidth * 0.25f;
		rightBorder = parentWidth * 0.75f;
		upperBorder = parentHeight * 0.25f;
		bottomBorder = parentHeight * 0.75f;
	}

	/**
	 * Returns if the initial touch happened on the 50% top part or 50% bottom part.
	 *
	 * @param initialTouchY the y axis location of the initial touch relative to this view.
	 * @return the {@link TouchType} of the initial touch on this view
	 */
	@TouchType
	int getTouchType(float initialTouchY) {
		return initialTouchY < height / 2f ? TOUCH_TOP : TOUCH_BOTTOM;
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
		float updateX = dx + startX;
		float updateY = dy + startY;

		ScrollProgress scrollProgress = getScrollProgress(dx, dy);
		float rotation = 2.f * rotationFactor * dx / parentWidth;

		return new UpdatePosition(updateX, updateY, rotation, scrollProgress);
	}

	/**
	 * Creates a {@link RecenterPosition} for the view.
	 */
	RecenterPosition getRecenterPosition() {
		return new RecenterPosition(startX, startY);
	}

	/**
	 * Returns the target {@link ExitPosition} of the view for the given current position and
	 * rotation factor.
	 * <p>
	 * The position is required to make a smooth linear exit from the center.
	 *
	 * @param framePosition  the current position of the view.
	 * @param direction      the dirrection of the exit event
	 * @param rotationFactor the base rotation factor to calculate the rotation
	 * @return the {@link ExitPosition} of the view
	 * @see #getRightExitPoint(PointF, float)
	 * @see #getLeftExitPosition(PointF, float)
	 */
	ExitPosition getExitPosition(PointF framePosition, @Direction int direction, float rotationFactor) {
		switch (direction) {
			case LEFT:
				return getLeftExitPosition(framePosition, rotationFactor);
			case RIGHT:
				return getRightExitPoint(framePosition, rotationFactor);
			case UP:
				return getUpExitPoint(framePosition, rotationFactor);
			case DOWN:
				return getDownExitPoint(framePosition, rotationFactor);
			default:
				throw new IllegalStateException("Unsupported exit direction : " + direction);

		}
	}

	/**
	 * Returns the target left {@link ExitPosition} of the view for the given current position and
	 * rotation factor.
	 * <p>
	 * The position is required to make a smooth linear exit from the center.
	 *
	 * @param framePosition  the current position of the view.
	 * @param rotationFactor the base rotation factor to calculate the rotation
	 * @return the left {@link ExitPosition} of the view
	 * @see #getRightExitPoint(PointF, float)
	 */
	private ExitPosition getLeftExitPosition(PointF framePosition, float rotationFactor) {
		float exitX = -width - getRotationSizeOffset(width, rotationFactor);
		float exitY = calculateHorizontalExitY(framePosition, exitX);
		float exitRotation = getExitRotation(rotationFactor);

		return new ExitPosition(exitX, exitY, exitRotation);
	}

	/**
	 * Returns the target right {@link ExitPosition} of the view for the given current position and
	 * rotation factor.
	 * <p>
	 * The position is required to make a smooth linear exit from the center.
	 *
	 * @param framePosition  the current position of the view.
	 * @param rotationFactor the base rotation factor to calculate the rotation
	 * @return the right {@link ExitPosition} of the view
	 * @see #getLeftExitPosition(PointF, float)
	 */
	private ExitPosition getRightExitPoint(PointF framePosition, float rotationFactor) {
		float exitX = parentWidth + getRotationSizeOffset(width, rotationFactor);
		float exitY = calculateHorizontalExitY(framePosition, exitX);
		float exitRotation = getExitRotation(rotationFactor);

		return new ExitPosition(exitX, exitY, exitRotation);
	}

	private ExitPosition getDownExitPoint(PointF framePosition, float rotationFactor) {
		float exitY = parentHeight + getRotationSizeOffset(height, rotationFactor);
		float exitX = calculateVerticalExitX(framePosition, exitY);
		float exitRotation = getExitRotation(rotationFactor);

		return new ExitPosition(exitX, exitY, exitRotation);
	}

	private ExitPosition getUpExitPoint(PointF framePosition, float rotationFactor) {
		float exitY = -height - getRotationSizeOffset(height, rotationFactor);
		float exitX = calculateVerticalExitX(framePosition, exitY);
		float exitRotation = getExitRotation(rotationFactor);

		return new ExitPosition(exitX, exitY, exitRotation);
	}

	private float calculateHorizontalExitY(PointF framePosition, float exitXPoint) {
		if (Float.compare(framePosition.y, startY) == 0) {
			return startY;
		}

		float slope = (framePosition.y - startY) / (framePosition.x - startX);
		float intercept = startY - slope * startX;

		//Your typical y = ax+b linear regression
		return slope * exitXPoint + intercept;
	}

	private float calculateVerticalExitX(PointF framePosition, float exitYPoint) {
		if (Float.compare(framePosition.x, startX) == 0) {
			return startX;
		}

		float slope = (framePosition.x - startX) / (framePosition.y - startY);
		float intercept = startX - slope * startY;

		//Your typical y = ax+b linear regression
		return slope * exitYPoint + intercept;
	}

	private float getExitRotation(float rotationFactor) {
		return 2.f * rotationFactor * (width - startX) / parentWidth;
	}

	ScrollProgress getScrollProgress(float dx, float dy) {
		float horizontalProgress = getScrollHorizontalProgress(dx);
		float verticalProgress = getScrollVerticalProgress(dy);

		if (abs(horizontalProgress) > abs(verticalProgress)) {
			// bigger horizontal progress
			if (horizontalProgress > 0) {
				return new ScrollProgress(RIGHT, min(1, horizontalProgress));
			} else if (horizontalProgress < 0) {
				return new ScrollProgress(LEFT, -1 * max(-1, horizontalProgress));
			}

		} else {
			// bigger vertical progress
			if (verticalProgress > 0) {
				return new ScrollProgress(DOWN, min(1, verticalProgress));
			} else if (verticalProgress < 0) {
				return new ScrollProgress(UP, -1 * max(-1, verticalProgress));
			}
		}

		return new ScrollProgress(CENTER, 0);
	}

	/**
	 * Generates the scroll progress based on the position of the view.
	 *
	 * @param dx the differential in x axis
	 * @return the horizontal scroll progress
	 */
	private float getScrollHorizontalProgress(float dx) {
		float currentCenterX = startX + dx + width / 2;
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

	private float getScrollVerticalProgress(float dy) {
		float currentCenterY = startY + dy + height / 2;
		float halfMovableFrameRange = (upperBorder - bottomBorder) / 2f;

		if (dy > 0f) {
			float distanceToBottomBorder = bottomBorder - currentCenterY;
			return distanceToBottomBorder / halfMovableFrameRange + 1;
		}

		if (dy < 0f) {
			float distanceToUpperBorder = currentCenterY - upperBorder;
			return -distanceToUpperBorder / halfMovableFrameRange - 1;
		}

		return 0;
	}
}
