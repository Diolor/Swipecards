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

	static View create() {
		View mockView = Mockito.mock(View.class);
		View mockParent = Mockito.mock(ViewGroup.class);

		when(mockView.getX()).thenReturn(100f);
		when(mockView.getY()).thenReturn(100f);
		when(mockView.getHeight()).thenReturn(100);
		when(mockView.getWidth()).thenReturn(100);

		when(mockParent.getWidth()).thenReturn(400);
		when((View) mockView.getParent()).thenReturn(mockParent);

		return mockView;
	}

	static View createWithAnimate() {
		View mockView = Mockito.mock(View.class);
		View mockParent = Mockito.mock(ViewGroup.class);
		ViewPropertyAnimator mockViewPropertyAnimator = Mockito.mock(ViewPropertyAnimator.class, Answers.RETURNS_SELF);

		when(mockView.getX()).thenReturn(100f);
		when(mockView.getY()).thenReturn(100f);
		when(mockView.getHeight()).thenReturn(100);
		when(mockView.getWidth()).thenReturn(100);
		when(mockView.animate()).thenReturn(mockViewPropertyAnimator);

		when(mockParent.getWidth()).thenReturn(400);

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
