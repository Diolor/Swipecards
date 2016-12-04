package com.lorentzos.flingswipe.internal;

import android.view.MotionEvent;
import android.view.View;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Tests the {@link SwipeOperator} against touch events.
 */
public class SwipeOperatorTouchTest {

	private SwipeOperator swipeOperator;

	private View mockView;

	@Before
	public void setUp() throws Exception {
		mockView = MockViewFactory.createWithAnimate();
	}

	@Test
	public void testIgnoreEvent() throws Exception {
		// Given
		MotionEvent invalidEvent = MotionEventFactory.invalid();
		swipeOperator = new SwipeOperator(new TestCardEventListener());
		swipeOperator.setSwipeView(new TopView(mockView));

		// When
		swipeOperator.onTouch(mockView, invalidEvent);

		// Then
		verify(mockView).setOnTouchListener(swipeOperator);
		verifyNoMoreInteractions(mockView);
	}

	@Test
	public void testClick() throws Exception {
		// Given
		final CountDownLatch clickLatch = new CountDownLatch(1);
		CountDownLatch scrollLatch = new CountDownLatch(2);
		MotionEvent pointerDownEvent = MotionEventFactory.touchDownEvent();
		MotionEvent moveEvent = MotionEventFactory.moveEvent(6, 6);
		MotionEvent upEvent = MotionEventFactory.upEvent(2, 2);

		swipeOperator = new SwipeOperator(new TestCardEventListener(scrollLatch) {

			@Override
			public void onClick(View view) {
				clickLatch.countDown();
			}

		});
		swipeOperator.setSwipeView(new TopView(mockView));

		// When
		swipeOperator.onTouch(mockView, pointerDownEvent);
		swipeOperator.onTouch(mockView, moveEvent);
		swipeOperator.onTouch(mockView, upEvent);

		// Then
		Assert.assertTrue(clickLatch.await(1, TimeUnit.SECONDS));
		Assert.assertTrue(scrollLatch.await(1, TimeUnit.SECONDS));

	}

	@Test
	public void testRecenter() throws Exception {
		// Given
		final CountDownLatch recenterLatch = new CountDownLatch(1);
		CountDownLatch scrollLatch = new CountDownLatch(2);
		MotionEvent pointerDownEvent = MotionEventFactory.touchDownEvent();
		MotionEvent moveEvent = MotionEventFactory.moveEvent(6, 6);
		MotionEvent upEvent = MotionEventFactory.upEvent(10, 10);

		swipeOperator = new SwipeOperator(new TestCardEventListener(scrollLatch) {

			@Override
			public void onCardExited(View view, @Direction int direction) {
				super.onCardExited(view, direction);
			}

			@Override
			public void onRecenter(View view) {
				recenterLatch.countDown();
			}

		});
		swipeOperator.setSwipeView(new TopView(mockView));

		// When
		swipeOperator.onTouch(mockView, pointerDownEvent);
		swipeOperator.onTouch(mockView, moveEvent);
		swipeOperator.onTouch(mockView, upEvent);

		// Then
		Assert.assertTrue(recenterLatch.await(1, TimeUnit.SECONDS));
		Assert.assertTrue(scrollLatch.await(1, TimeUnit.SECONDS));
	}

	@Test
	public void testExit() throws Exception {
		// Given
		final CountDownLatch exitLatch = new CountDownLatch(1);
		CountDownLatch scrollLatch = new CountDownLatch(2);
		MotionEvent pointerDownEvent = MotionEventFactory.touchDownEvent();
		MotionEvent moveEvent = MotionEventFactory.moveEvent(60, 60);
		MotionEvent moveSlightlyEvent = MotionEventFactory.moveEvent(62, 62);
		MotionEvent upEvent = MotionEventFactory.upEvent(200, 200);

		swipeOperator = new SwipeOperator(new TestCardEventListener(scrollLatch) {
			@Override
			public void onCardExited(View view, @Direction int direction) {
				if (direction == Direction.RIGHT) {
					exitLatch.countDown();
				}
			}
		});
		swipeOperator.setSwipeView(new TopView(mockView));

		// When
		swipeOperator.onTouch(mockView, pointerDownEvent);
		swipeOperator.onTouch(mockView, moveEvent);
		swipeOperator.onTouch(mockView, moveSlightlyEvent);
		swipeOperator.onTouch(mockView, upEvent);

		// Then
		Assert.assertTrue(exitLatch.await(1, TimeUnit.SECONDS));
		Assert.assertTrue(scrollLatch.await(1, TimeUnit.SECONDS));
	}

}