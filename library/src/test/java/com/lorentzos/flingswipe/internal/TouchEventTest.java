package com.lorentzos.flingswipe.internal;

import com.google.common.collect.Range;
import com.google.common.truth.Truth;

/**
 * Base {@link TouchEvent} test class with reusable methods.
 */
class TouchEventTest {

	static void verifyPositiveEndingProgress(float progress) {
		Truth.assertThat(progress).isWithin(1f);
	}

	static void verifyPositiveProgress(float progress) {
		Truth.assertThat(progress).isIn(Range.greaterThan(0f));
	}

}
