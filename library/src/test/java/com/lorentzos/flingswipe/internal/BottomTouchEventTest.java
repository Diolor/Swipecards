package com.lorentzos.flingswipe.internal;

import android.view.View;

import com.google.common.truth.Truth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static com.lorentzos.flingswipe.internal.MockViewFactory.INITIAL_VIEW_X;
import static com.lorentzos.flingswipe.internal.MockViewFactory.INITIAL_VIEW_Y;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.floatThat;
import static org.mockito.Mockito.verify;

/**
 * Tests when the user touches the bottom part of the view.
 */
@RunWith(MockitoJUnitRunner.class)
public class BottomTouchEventTest extends TouchEventTest {

	private View mockView;
	private TouchEvent touchEvent;
	private PointF initialTouch;

	@Before
	public void setUp() throws Exception {
		mockView = MockViewFactory.create();
		initialTouch = new PointF(120f, 170f);

		touchEvent = new TouchEvent(15f, mockView, initialTouch, 70f);
	}

	@Test
	public void topLeftOutside() throws Exception {
		// Given
		PointF topLeftTouch = new PointF(10f, 10f);

		// When
		ScrollProgress progress = touchEvent.moveView(topLeftTouch);

		// Then
		verifyLeft(topLeftTouch);
		verifyPositiveEndingProgress(progress.progress);
	}

	@Test
	public void bottomLeftOutside() throws Exception {
		// Given
		PointF bottomLeftTouch = new PointF(10f, 200f);

		// When
		ScrollProgress progress = touchEvent.moveView(bottomLeftTouch);

		// Then
		verifyLeft(bottomLeftTouch);
		verifyPositiveEndingProgress(progress.progress);
	}

	@Test
	public void topRightOutside() throws Exception {
		// Given
		PointF topRightTouch = new PointF(330f, 10f);

		// When
		ScrollProgress progress = touchEvent.moveView(topRightTouch);

		// Then
		verifyRight(topRightTouch);
		verifyPositiveEndingProgress(progress.progress);
	}

	@Test
	public void bottomRightOutside() throws Exception {
		// Given
		PointF bottomRightTouch = new PointF(330f, 330f);

		// When
		ScrollProgress progress = touchEvent.moveView(bottomRightTouch);

		// Then
		verifyRight(bottomRightTouch);
		verifyPositiveEndingProgress(progress.progress);
	}

	@Test
	public void topLeftInside() throws Exception {
		// Given
		PointF topLeftTouch = new PointF(110f, 10f);

		// When
		ScrollProgress progress = touchEvent.moveView(topLeftTouch);

		// Then
		verifyLeft(topLeftTouch);
		verifyPositiveProgress(progress.progress);
	}

	@Test
	public void bottomLeftInside() throws Exception {
		// Given
		PointF bottomLeftTouch = new PointF(110f, 200f);

		// When
		ScrollProgress progress = touchEvent.moveView(bottomLeftTouch);

		// Then
		verifyLeft(bottomLeftTouch);
		verifyPositiveProgress(progress.progress);
	}

	@Test
	public void topRightInside() throws Exception {
		// Given
		PointF topRightTouch = new PointF(290f, 10f);

		// When
		ScrollProgress progress = touchEvent.moveView(topRightTouch);

		// Then
		verifyRight(topRightTouch);
		verifyPositiveProgress(progress.progress);
	}

	@Test
	public void bottomRightInside() throws Exception {
		// Given
		PointF bottomRightTouch = new PointF(290f, 160f);

		// When
		ScrollProgress progress = touchEvent.moveView(bottomRightTouch);

		// Then
		verifyRight(bottomRightTouch);
		verifyPositiveProgress(progress.progress);
	}

	@Test
	public void onlyVerticalMove() throws Exception {
		// Given
		PointF verticalTouch = new PointF(120f, 160f);

		// When
		ScrollProgress progress = touchEvent.moveView(verticalTouch);

		// Then
		verify(mockView).setX(INITIAL_VIEW_X);
		verify(mockView).setY(verticalTouch.y - initialTouch.y + INITIAL_VIEW_Y);
		verify(mockView).setRotation(eq(-0f));
		Truth.assertThat(progress.progress).isWithin(0);
	}

	private void verifyLeft(PointF moveTouch) {
		verify(mockView).setX(moveTouch.x - initialTouch.x + INITIAL_VIEW_X);
		verify(mockView).setY(moveTouch.y - initialTouch.y + INITIAL_VIEW_Y);
		verify(mockView).setRotation(floatThat(RotationMatcher.POSITIVE));
	}

	private void verifyRight(PointF moveTouch) {
		verify(mockView).setX(moveTouch.x - initialTouch.x + INITIAL_VIEW_X);
		verify(mockView).setY(moveTouch.y - initialTouch.y + INITIAL_VIEW_Y);
		verify(mockView).setRotation(floatThat(RotationMatcher.NEGATIVE));
	}
}