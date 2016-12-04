package com.lorentzos.flingswipe.internal;

import android.view.View;

/**
 * Interface definition for a callback to be invoked when a swipe event is dispatched to this view.
 */
interface OnSwipeListener {

	/**
	 * Called when a swipe event is dispatched to a view.
	 *
	 * @param view      The view the swipe event has been dispatched to.
	 * @param direction The direction of the swipe event
	 */
	void swipe(View view, @Direction int direction);
}
