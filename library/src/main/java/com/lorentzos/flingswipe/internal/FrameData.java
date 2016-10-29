package com.lorentzos.flingswipe.internal;

import android.graphics.PointF;
import android.support.annotation.FloatRange;
import android.util.Log;
import android.view.View;

/**
 *
 */
public class FrameData {
	private final float startX;
	private final float startY;
	private final float height;
	private final float width;
	private final float parentWidth;
	private final float leftBorder;
	private final float rightBorder;

	/**
	 * Creates a new instance of {@link FrameData} from a given view.
	 *
	 * @param view the view you would like to generate data from
	 * @return a new object instance
	 */
	public static FrameData fromView(View view) {
		Log.wtf("FrameData", "fromView " + view.getY());
		Log.wtf("FrameData", "fromView " + view.getTranslationY());

		PointF framePosition = new PointF(view.getX(), view.getY());
		Size frameSize = new Size(view.getHeight(), view.getWidth());
		int parentWidth = ((View) view.getParent()).getWidth();

		return new FrameData(framePosition, frameSize, parentWidth);
	}

	private FrameData(PointF framePosition, Size frameSize, float parentWidth) {
		startX = framePosition.x;
		startY = framePosition.y;
		height = frameSize.getHeight();
		width = frameSize.getWidth();
		this.parentWidth = parentWidth;
		leftBorder = parentWidth / 4.f;
		rightBorder = parentWidth / 0.75f;
	}

	/**
	 * Creates a new {@link TargetPosition} for the given differential in x and y axis.
	 *
	 * @param dx             the differential in x axis
	 * @param dy             the differential in y axis
	 * @param rotationFactor todo
	 * @return the new position this frame should take
	 */
	public TargetPosition createTargetPosition(float dx, float dy, float rotationFactor) {
		float rotation = 2.f * rotationFactor * dx / parentWidth;
		float scrollProgress = getScrollProgress();

		return new TargetPosition(dx, dy, rotation, scrollProgress);
	}

	@FloatRange(from = -1, to = 1)
	private float getScrollProgress() {
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

	public float getParentWidth() {
		return parentWidth;
	}

	public float getHeight() {
		return height;
	}

	public float getStartY() {
		return startY;
	}

	public float getStartX() {
		return startX;
	}

	public float getWidth() {
		return width;
	}
}
