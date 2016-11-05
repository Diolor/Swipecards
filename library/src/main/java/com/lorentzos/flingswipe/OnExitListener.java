package com.lorentzos.flingswipe;

import android.view.View;

import com.lorentzos.flingswipe.internal.Direction;

/**
 *
 */
public interface OnExitListener {
	void onExit(View view, @Direction int direction);
}
