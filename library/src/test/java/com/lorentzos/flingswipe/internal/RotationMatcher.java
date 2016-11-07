package com.lorentzos.flingswipe.internal;

import org.mockito.ArgumentMatcher;

/**
 * {@link ArgumentMatcher} for floats
 */
class RotationMatcher {
	static final ArgumentMatcher<Float> POSITIVE = new ArgumentMatcher<Float>() {
		@Override
		public boolean matches(Float argument) {
			return argument > 0f;
		}
	};
	static final ArgumentMatcher<Float> NEGATIVE = new ArgumentMatcher<Float>() {
		@Override
		public boolean matches(Float argument) {
			return argument < 0f;
		}
	};

}
