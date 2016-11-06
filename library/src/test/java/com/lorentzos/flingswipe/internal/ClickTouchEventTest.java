package com.lorentzos.flingswipe.internal;

import android.view.View;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Tests the click functionality of of the Touch event
 */
public class ClickTouchEventTest extends TouchEventTest {

	private TouchEvent touchEvent;

	@Before
	public void setUp() throws Exception {
		View mockView = MockViewFactory.createWithAnimate();
		PointF startTouch = new PointF(120f, 120f);

		touchEvent = new TouchEvent(15f, mockView, startTouch, 40f);
	}

	@Test
	public void clckView() throws Exception {
		// Given
		final CountDownLatch latch = new CountDownLatch(1);
		PointF endTouch = new PointF(122f, 122f);

		// When

		touchEvent.resultView(endTouch, new OnCardResult() {
			@Override
			public void onExit(FrameResult frameResult) {
				if (frameResult.getEndEvent() == EndEvent.CLICK) {
					latch.countDown();
				}
			}
		});

		// Then
		Assert.assertTrue(latch.await(2, TimeUnit.SECONDS));
	}
}