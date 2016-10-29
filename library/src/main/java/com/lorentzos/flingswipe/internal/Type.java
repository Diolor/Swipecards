package com.lorentzos.flingswipe.internal;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 *
 */
@SuppressWarnings("ConstantDeclaredInInterface")
@Retention(SOURCE)
@IntDef({Type.TOUCH_ABOVE, Type.TOUCH_BELOW})
public @interface Type {

	int TOUCH_ABOVE = 0;
	int TOUCH_BELOW = 1;
}
