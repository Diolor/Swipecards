package com.lorentzos.flingswipe.internal;

import android.graphics.PointF;
import android.support.annotation.FloatRange;
import android.util.Log;
import android.view.View;

import static com.lorentzos.flingswipe.internal.Type.TOUCH_BOTTOM;
import static com.lorentzos.flingswipe.internal.Type.TOUCH_TOP;

/**
 *
 */
public class FrameData {
	@SuppressWarnings("ConstantMathCall")
	private static final float MAX_COS = (float) StrictMath.cos(Math.toRadians(45));

	private final float startX;
	private final float startY;
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
	public static FrameData fromView(View view) {
		PointF framePosition = new PointF(view.getX(), view.getY());
		Size frameSize = new Size(view.getHeight(), view.getWidth());
		int parentWidth = ((View) view.getParent()).getWidth();

		return new FrameData(framePosition, frameSize, parentWidth);
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

	private FrameData(PointF framePosition, Size frameSize, float parentWidth) {
		startX = framePosition.x;
		startY = framePosition.y;
		height = frameSize.getHeight();
		width = frameSize.getWidth();
		this.parentWidth = parentWidth;
		leftBorder = parentWidth / 4.f;
		rightBorder = parentWidth / 0.75f;

		rotationWidthOffset = getRotationWidthOffset(width);
	}

	/**
	 * Returns if the initial touch happened on the 50% top part or 50% bottom part.
	 *
	 * @param initialTouchY the y axis location of the initial touch
	 * @return the {@link Type} of the initial touch on this view
	 */
	@Type
	public int getTouchType(float initialTouchY) {
		return initialTouchY < startX + height / 2f ? TOUCH_TOP : TOUCH_BOTTOM;
	}

	/**
	 * Creates a new {@link UpdatePosition} for the given differential in x and y axis.
	 *
	 * @param dx             the differential in x axis
	 * @param dy             the differential in y axis
	 * @param rotationFactor todo
	 * @return the new position this frame should take
	 */
	public UpdatePosition createUpdatePosition(float dx, float dy, float rotationFactor) {
		float rotation = 2.f * rotationFactor * dx / parentWidth;
		float scrollProgress = getScrollProgress();
		Log.wtf("FrameData", "createUpdatePosition " + scrollProgress);

		return new UpdatePosition(dx, dy, rotation, scrollProgress);
	}

	// TODO: 29/10/16
	public RecenterPosition getRecenterPosition() {
		return new RecenterPosition(startX, startY);
	}

	/**
	 * Returns the target left {@link ExitPosition} of the view for the given
	 * current position and rotation factor.
	 * <p>
	 * The position is required to make a smooth linear exit from the center.
	 *
	 * @param framePosition  the current position of the view.
	 * @param rotationFactor todo
	 * @return the left {@link ExitPosition} of the view
	 * @see #getRightExitPoint(PointF, float)
	 */
	public ExitPosition getLeftExitPosition(PointF framePosition, float rotationFactor) {
		float exitX = -width - rotationWidthOffset;
		float exitY = calculateExitY(framePosition, exitX);
		float exitRotation = getExitRotation(rotationFactor);

		return new ExitPosition(exitX, exitY, exitRotation);
	}

	// TODO: 29/10/16
	public ExitPosition getRightExitPoint(PointF framePosition, float rotationFactor) {
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

	@FloatRange(from = -1, to = 1)
	public float getScrollProgress() {
		float frameCenterX = startX + width / 2f;

		if (frameCenterX < leftBorder) {
			return -1;
		}

		if (frameCenterX > rightBorder) {
			return 1;
		}

		float movableFrameRange = rightBorder - leftBorder;
		float zeroToOneValue = (frameCenterX - leftBorder) / movableFrameRange;

		return zeroToOneValue * 2f - 1f;
	}
}
