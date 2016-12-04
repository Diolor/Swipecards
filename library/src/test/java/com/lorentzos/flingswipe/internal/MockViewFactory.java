package com.lorentzos.flingswipe.internal;

import android.animation.Animator;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;

import org.mockito.Answers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Helping class to mock {@link View} classes
 */
class MockViewFactory {

	private static final int SIDE_SIZE_PIXELS = 100;
	static final float INITIAL_VIEW_Y = 100f;
	static final float INITIAL_VIEW_X = 100f;

	static View create() {
		View mockView = Mockito.mock(View.class);
		View mockParent = Mockito.mock(ViewGroup.class);

		when(mockView.getX()).thenReturn(INITIAL_VIEW_X);
		when(mockView.getY()).thenReturn(INITIAL_VIEW_Y);
		when(mockView.getHeight()).thenReturn(SIDE_SIZE_PIXELS);
		when(mockView.getWidth()).thenReturn(SIDE_SIZE_PIXELS);

		when(mockParent.getWidth()).thenReturn(400);
		when(mockParent.getHeight()).thenReturn(450);
		when((View) mockView.getParent()).thenReturn(mockParent);

		return mockView;
	}

	static View createWithAnimate() {
		View mockView = Mockito.mock(View.class);
		View mockParent = Mockito.mock(ViewGroup.class);
		ViewPropertyAnimator mockViewPropertyAnimator = Mockito.mock(ViewPropertyAnimator.class, Answers.RETURNS_SELF);

		when(mockView.getX()).thenReturn(INITIAL_VIEW_X);
		when(mockView.getY()).thenReturn(INITIAL_VIEW_Y);
		when(mockView.getHeight()).thenReturn(SIDE_SIZE_PIXELS);
		when(mockView.getWidth()).thenReturn(SIDE_SIZE_PIXELS);
		when(mockView.animate()).thenReturn(mockViewPropertyAnimator);

		when(mockParent.getWidth()).thenReturn(400);
		when(mockParent.getHeight()).thenReturn(450);

		when((View) mockView.getParent()).thenReturn(mockParent);

		when(mockViewPropertyAnimator.setListener(any(Animator.AnimatorListener.class))).thenAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				((Animator.AnimatorListener) invocation.getArguments()[0]).onAnimationEnd(null);
				return null;
			}
		});

		return mockView;
	}
}
