package com.lorentzos.flingswipe.internal;

import android.view.View;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class SwipeOperatorManualSwipeTest {

	private View mockView;

	@Before
	public void setUp() throws Exception {
		mockView = MockViewFactory.createWithAnimate();
	}

	@Test
	public void manualLeftSwipe() throws Exception {
		// Given
		final CountDownLatch exitLatch = new CountDownLatch(1);
		SwipeOperator swipeOperator = new SwipeOperator(new TestCardEventListener() {
			@Override
			public void onCardExited(View view, @Direction int direction) {
				if (direction == Direction.LEFT) {
					exitLatch.countDown();
				}
			}
		});
		swipeOperator.setSwipeView(new TopView(mockView));

		// When
		swipeOperator.swipe(mockView, Direction.LEFT);

		// Then
		Assert.assertTrue(exitLatch.await(1, TimeUnit.SECONDS));
	}
}