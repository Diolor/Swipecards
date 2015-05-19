package com.lorentzos.flingswipe;

/**
 * Created by mklepp on 19/05/15.
 */
public class Direction {
    // must be aligned with direction enum in /res/values/attrs.xml
    public static final int LEFT = 1;
    public static final int TOP = 2;
    public static final int RIGHT = 4;
    public static final int BOTTOM = 8;

    public static final int HORIZONTAL = LEFT | RIGHT;
    public static final int VERTICAL = BOTTOM | TOP;

    public static boolean hasLeft(int direction){
        return hasDirection(direction, LEFT);
    }

    public static boolean hasRight(int direction){
        return hasDirection(direction, RIGHT);
    }

    public static boolean hasTop(int direction){
        return hasDirection(direction, TOP);
    }

    public static boolean hasBottom(int direction){
        return hasDirection(direction, BOTTOM);
    }

    public static boolean hasDirection(int direction, int directionToCheck){
        return ((direction & directionToCheck) == directionToCheck);
    }
}
