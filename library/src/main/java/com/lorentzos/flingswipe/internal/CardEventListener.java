package com.lorentzos.flingswipe.internal;

import android.view.View;

/**
 *
 */
public interface CardEventListener {
	void onCardExited(View view, @Direction int direction);

	void onScroll(View view, float scrollProgressPercent, @Direction int direction);

	void onRecenter(View view);

	void onClick(View view);
}
