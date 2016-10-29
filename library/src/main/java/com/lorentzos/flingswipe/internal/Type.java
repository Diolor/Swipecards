package com.lorentzos.flingswipe.internal;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 *
 */
@SuppressWarnings("ConstantDeclaredInInterface")
@Retention(SOURCE)
@IntDef({Type.TOUCH_TOP, Type.TOUCH_BOTTOM})
public @interface Type {

	int TOUCH_TOP = 0;
	int TOUCH_BOTTOM = 1;
}
