package com.lorentzos.flingswipe.internal;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * The direction of the card exit.
 */
@SuppressWarnings("ConstantDeclaredInInterface")
@Retention(SOURCE)
@IntDef({Direction.CENTER, Direction.LEFT, Direction.RIGHT, Direction.UP, Direction.DOWN})
public @interface Direction {

	int CENTER = -1;
	int LEFT = 1;
	int RIGHT = 2;
	int UP = 3;
	int DOWN = 4;
}
