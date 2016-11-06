package com.lorentzos.flingswipe.internal;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * The ending type of a {@link TouchEvent}
 */
@SuppressWarnings("ConstantDeclaredInInterface")
@Retention(SOURCE)
@IntDef({EndEvent.EXIT, EndEvent.RECENTER, EndEvent.CLICK})
@interface EndEvent {

	int CLICK = 0;
	int RECENTER = 1;
	int EXIT = 2;

}
