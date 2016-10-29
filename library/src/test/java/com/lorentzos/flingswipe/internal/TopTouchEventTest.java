package com.lorentzos.flingswipe.internal;

import android.view.View;

import com.google.common.truth.Truth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.floatThat;
import static org.mockito.Mockito.verify;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class TopTouchEventTest extends TouchEventTest {

	private View mockView;
	private TouchEvent touchEvent;
	private PointF middlishTouch;

	@Before
	public void setUp() throws Exception {
		mockView = MockViewFactory.create();
		middlishTouch = new PointF(120f, 120f);

		touchEvent = new TouchEvent(15f, mockView, middlishTouch);
	}

	@Test
	public void topLeftOutside() throws Exception {
		// Given
		PointF topLeftTouch = new PointF(10f, 10f);

		// When
		float progress = touchEvent.moveView(topLeftTouch);

		// Then
		verifyLeft(topLeftTouch);
		verifyNegativeEndingProgress(progress);
	}

	@Test
	public void bottomLeftOutside() throws Exception {
		// Given
		PointF bottomLeftTouch = new PointF(10f, 200f);

		// When
		float progress = touchEvent.moveView(bottomLeftTouch);

		// Then
		verifyLeft(bottomLeftTouch);
		verifyNegativeEndingProgress(progress);
	}

	@Test
	public void topRightOutside() throws Exception {
		// Given
		PointF topRightTouch = new PointF(330f, 10f);

		// When
		float progress = touchEvent.moveView(topRightTouch);

		// Then
		verifyRight(topRightTouch);
		verifyPositiveEndingProgress(progress);
	}

	@Test
	public void bottomRightOutside() throws Exception {
		// Given
		PointF bottomRightTouch = new PointF(330f, 330f);

		// When
		float progress = touchEvent.moveView(bottomRightTouch);

		// Then
		verifyRight(bottomRightTouch);
		verifyPositiveEndingProgress(progress);
	}

	@Test
	public void topLeftInside() throws Exception {
		// Given
		PointF topLeftTouch = new PointF(110f, 10f);

		// When
		float progress = touchEvent.moveView(topLeftTouch);

		// Then
		verifyLeft(topLeftTouch);
		verifyNegativeProgress(progress);
	}

	@Test
	public void bottomLeftInside() throws Exception {
		// Given
		PointF bottomLeftTouch = new PointF(110f, 200f);

		// When
		float progress = touchEvent.moveView(bottomLeftTouch);

		// Then
		verifyLeft(bottomLeftTouch);
		verifyNegativeProgress(progress);
	}

	@Test
	public void topRightInside() throws Exception {
		// Given
		PointF topRightTouch = new PointF(290f, 10f);

		// When
		float progress = touchEvent.moveView(topRightTouch);

		// Then
		verifyRight(topRightTouch);
		verifyPositiveProgress(progress);
	}

	@Test
	public void bottomRightInside() throws Exception {
		// Given
		PointF bottomRightTouch = new PointF(230f, 160f);

		// When
		float progress = touchEvent.moveView(bottomRightTouch);

		// Then
		verifyRight(bottomRightTouch);
		verifyPositiveProgress(progress);
	}

	@Test
	public void onlyVerticalMove() throws Exception {
		// Given
		PointF verticalTouch = new PointF(120f, 160f);

		// When
		float progress = touchEvent.moveView(verticalTouch);

		// Then
		verify(mockView).setTranslationX(0);
		verify(mockView).setTranslationY(verticalTouch.y - middlishTouch.y);
		verify(mockView).setRotation(0);
		Truth.assertThat(progress).isWithin(0);
	}

	private void verifyLeft(PointF bottomLeftTouch) {
		verify(mockView).setTranslationX(bottomLeftTouch.x - middlishTouch.x);
		verify(mockView).setTranslationY(bottomLeftTouch.y - middlishTouch.y);
		verify(mockView).setRotation(floatThat(RotationMatcher.NEGATIVE));
	}

	private void verifyRight(PointF topRightTouch) {
		verify(mockView).setTranslationX(topRightTouch.x - middlishTouch.x);
		verify(mockView).setTranslationY(topRightTouch.y - middlishTouch.y);
		verify(mockView).setRotation(floatThat(RotationMatcher.POSITIVE));
	}

}