package com.lorentzos.flingswipe.internal;

/**
 *
 */
public class ResultPosition {

	private final ResultState resultState;
	private final float scrollProgress;

	public ResultPosition(ResultState resultState, float scrollProgress) {

		this.resultState = resultState;
		this.scrollProgress = scrollProgress;
	}

	public ResultState getResultState() {
		return resultState;
	}

	public float getScrollProgress() {
		return scrollProgress;
	}
}
