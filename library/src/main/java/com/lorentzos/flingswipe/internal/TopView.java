package com.lorentzos.flingswipe.internal;

import android.view.View;

/**
 * Delegates a given {@link View} to add swipe listening functionality.
 */
public class TopView {
	private final View view;
	private OnSwipeListener onSwipeListener;

	public TopView(View view) {
		this.view = view;
	}

	/**
	 * @return the view this class has been created from.
	 */
	public View get() {
		return view;
	}

	/**
	 * Performs a swipe animation to the given view.
	 *
	 * @param direction the direction of the swipe animation.
	 */
	public void swipe(@Direction int direction) {
		onSwipeListener.swipe(view, direction);
	}

	/**
	 * Sets a traditional {@link View.OnTouchListener} in order to animate the rotation
	 * and capture the pointer up events to swipe away the card.
	 *
	 * @param onTouchListener the listener
	 */
	void setOnTouchListener(View.OnTouchListener onTouchListener) {
		view.setOnTouchListener(onTouchListener);
	}

	/**
	 * Sets a {@link OnSwipeListener} to observe manual swiping events.
	 *
	 * @param onSwipeListener the listener
	 */
	void setOnSwipeListener(OnSwipeListener onSwipeListener) {
		this.onSwipeListener = onSwipeListener;
	}
}
