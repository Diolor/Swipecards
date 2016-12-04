package com.lorentzos.flingswipe.internal;

import android.view.View;

import java.util.concurrent.CountDownLatch;

/**
 *
 */
class TestCardEventListener implements CardEventListener {

	private final CountDownLatch scrollLatch;

	TestCardEventListener(CountDownLatch scrollLatch) {
		this.scrollLatch = scrollLatch;
	}

	TestCardEventListener() {
		scrollLatch = null;
	}

	@Override
	public void onCardExited(View view, @Direction int direction) {

	}

	@Override
	public void onScroll(View view, float scrollProgressPercent, @Direction int direction) {
		if (scrollLatch != null) {
			scrollLatch.countDown();
		}
	}

	@Override
	public void onRecenter(View view) {

	}

	@Override
	public void onClick(View view) {

	}
}
