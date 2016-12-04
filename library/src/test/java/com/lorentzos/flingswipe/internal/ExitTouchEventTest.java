package com.lorentzos.flingswipe.internal;

import android.view.View;

import com.google.common.truth.Truth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ExitTouchEventTest extends TouchEventTest {

	private static final OnCardResult ON_CARD_RESULT = new OnCardResult() {
		@Override
		public void onExit(FrameResult frameResult) {

		}
	};
	private TouchEvent touchEvent;
	private View mockView;

	@Before
	public void setUp() throws Exception {
		mockView = MockViewFactory.createWithAnimate();
		PointF middlishTouch = new PointF(120f, 120f);

		touchEvent = new TouchEvent(15f, mockView, middlishTouch, 40f);
	}

	@Test
	public void topLeftOutside() throws Exception {
		// Given
		PointF topLeftTouch = new PointF(10f, 10f);

		// When
		ScrollProgress progress = touchEvent.resultView(topLeftTouch, ON_CARD_RESULT);

		// Then
		verifyPositiveEndingProgress(progress.progress);
		Truth.assertThat(progress.direction).isEqualTo(Direction.UP);
	}

	@Test
	public void bottomLeftOutside() throws Exception {
		// Given
		PointF bottomLeftTouch = new PointF(50f, 200f);

		// When
		ScrollProgress progress = touchEvent.resultView(bottomLeftTouch, ON_CARD_RESULT);

		// Then
		verifyPositiveEndingProgress(progress.progress);
		Truth.assertThat(progress.direction).isEqualTo(Direction.LEFT);
	}

	@Test
	public void topRightOutside() throws Exception {
		// Given
		PointF topRightTouch = new PointF(330f, 10f);

		// When
		ScrollProgress progress = touchEvent.resultView(topRightTouch, ON_CARD_RESULT);

		// Then
		verifyPositiveEndingProgress(progress.progress);
		Truth.assertThat(progress.direction).isEqualTo(Direction.UP);
	}

	@Test
	public void bottomRightOutside() throws Exception {
		// Given
		PointF bottomRightTouch = new PointF(330f, 330f);

		// When
		ScrollProgress progress = touchEvent.resultView(bottomRightTouch, ON_CARD_RESULT);

		// Then
		verifyPositiveEndingProgress(progress.progress);
		Truth.assertThat(progress.direction).isEqualTo(Direction.RIGHT);
	}

	@Test
	public void topLeftInside() throws Exception {
		// Given
		PointF topLeftTouch = new PointF(110f, 10f);

		// When
		ScrollProgress progress = touchEvent.resultView(topLeftTouch, ON_CARD_RESULT);

		// Then
		verifyPositiveProgress(progress.progress);
		Truth.assertThat(progress.direction).isEqualTo(Direction.UP);
	}

	@Test
	public void bottomLeftInside() throws Exception {
		// Given
		PointF bottomLeftTouch = new PointF(110f, 200f);

		// When
		ScrollProgress progress = touchEvent.resultView(bottomLeftTouch, ON_CARD_RESULT);

		// Then
		verifyPositiveProgress(progress.progress);
		Truth.assertThat(progress.direction).isEqualTo(Direction.LEFT);
	}

	@Test
	public void topRightInside() throws Exception {
		// Given
		PointF topRightTouch = new PointF(290f, 10f);

		// When
		ScrollProgress progress = touchEvent.resultView(topRightTouch, ON_CARD_RESULT);

		// Then
		verifyPositiveProgress(progress.progress);
		Truth.assertThat(progress.direction).isEqualTo(Direction.UP);
	}

	@Test
	public void bottomRightInside() throws Exception {
		// Given
		PointF bottomRightTouch = new PointF(290f, 160f);

		// When
		ScrollProgress progress = touchEvent.resultView(bottomRightTouch, ON_CARD_RESULT);

		// Then
		verifyPositiveProgress(progress.progress);
		Truth.assertThat(progress.direction).isEqualTo(Direction.RIGHT);
	}

	@Test
	public void onlyVerticalMove() throws Exception {
		// Given
		PointF verticalTouch = new PointF(120f, 360f);

		// When
		ScrollProgress progress = touchEvent.resultView(verticalTouch, ON_CARD_RESULT);

		// Then
		Truth.assertThat(progress.progress).isWithin(0);
		Truth.assertThat(progress.direction).isEqualTo(Direction.DOWN);
	}

}