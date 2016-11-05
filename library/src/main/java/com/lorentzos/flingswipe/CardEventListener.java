package com.lorentzos.flingswipe;

import android.view.View;

import com.lorentzos.flingswipe.internal.Direction;

/**
 *
 */
public interface CardEventListener {
	void onCardExited(View view, @Direction int direction);

	void onScroll(View view, float scrollProgressPercent);

	void onRecenter(View view);

	void onClick(View view);
}
