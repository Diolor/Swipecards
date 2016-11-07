package com.lorentzos.flingswipe.internal;

import android.view.MotionEvent;

import org.mockito.Mockito;

import static org.mockito.Mockito.when;

/**
 *
 */

public class MotionEventFactory {

	private static final float Y = 150f;
	private static final float RAW_X = 250f;
	private static final float RAW_Y = 250f;

	public static MotionEvent invalid() {
		MotionEvent mock = Mockito.mock(MotionEvent.class);
		when(mock.getActionIndex()).thenReturn(1);

		return mock;
	}

	public static MotionEvent touchDownEvent() {
		MotionEvent mock = Mockito.mock(MotionEvent.class);

		when(mock.getY()).thenReturn(Y);
		when(mock.getRawX()).thenReturn(RAW_X);
		when(mock.getRawY()).thenReturn(RAW_Y);

		when(mock.getActionMasked()).thenReturn(MotionEvent.ACTION_DOWN);

		return mock;
	}

	public static MotionEvent moveEvent(int dx, int dy) {
		MotionEvent mock = Mockito.mock(MotionEvent.class);

		when(mock.getY()).thenReturn(Y + dy);
		when(mock.getRawX()).thenReturn(RAW_X + dx);
		when(mock.getRawY()).thenReturn(RAW_Y + dy);

		when(mock.getActionMasked()).thenReturn(MotionEvent.ACTION_MOVE);

		return mock;
	}

	public static MotionEvent upEvent(int dx, int dy) {
		MotionEvent mock = Mockito.mock(MotionEvent.class);

		when(mock.getY()).thenReturn(Y + dy);
		when(mock.getRawX()).thenReturn(RAW_X + dx);
		when(mock.getRawY()).thenReturn(RAW_Y + dy);

		when(mock.getActionMasked()).thenReturn(MotionEvent.ACTION_UP);

		return mock;
	}

}
