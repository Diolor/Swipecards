package com.lorentzos.flingswipe.internal;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * The type of touch of the user on the view.
 */
@SuppressWarnings("ConstantDeclaredInInterface")
@Retention(SOURCE)
@IntDef({TouchType.TOUCH_TOP, TouchType.TOUCH_BOTTOM})
@interface TouchType {

	/**
	 * If the user touched the top 50% of the view
	 */
	int TOUCH_TOP = 0;

	/**
	 * If the user touched the bottom 50% of the view
	 */
	int TOUCH_BOTTOM = 1;
}
