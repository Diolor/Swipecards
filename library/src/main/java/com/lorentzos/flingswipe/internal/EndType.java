package com.lorentzos.flingswipe.internal;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * The ending type of a {@link TouchEvent}
 */
@SuppressWarnings("ConstantDeclaredInInterface")
@Retention(SOURCE)
@IntDef({EndType.EXIT, EndType.RECENTER})
public @interface EndType {

	int EXIT = 0;
	int RECENTER = 1;

}
