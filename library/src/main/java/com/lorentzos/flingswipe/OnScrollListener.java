package com.lorentzos.flingswipe;

import android.view.View;

import com.lorentzos.flingswipe.internal.Direction;

/**
 *
 */
public interface OnScrollListener {
	void onScroll(View view, float scrollProgressPercent, @Direction int direction);
}
