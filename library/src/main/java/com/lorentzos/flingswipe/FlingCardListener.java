package com.lorentzos.flingswipe;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by dionysis_lorentzos on 5/8/14
 * for package com.lorentzos.swipecards
 * and project Swipe cards.
 * Use with caution dinausaurs might appear!
 */


public class FlingCardListener implements View.OnTouchListener {

    private final float objectX;
    private final float objectY;
    private final int objectH;
    private final int objectW;
    private final int parentWidth;
    private final int parentHeight;
    private final FlingListener mFlingListener;
    private final Object dataObject;
    private final float halfWidth;
    private final float halfHeight;
    private float BASE_ROTATION_DEGREES;
    private int DIRECTION;

    private float aPosX;
    private float aPosY;
    private float aDownTouchX;
    private float aDownTouchY;
    private static final int INVALID_POINTER_ID = -1;

    // The active pointer is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;
    private View frame = null;


    private final int TOUCH_ABOVE = 0;
    private final int TOUCH_BELOW = 1;
    private int touchPosition;
    private final Object obj = new Object();
    private boolean isAnimationRunning = false;
    private float MAX_COS = (float) Math.cos(Math.toRadians(45));


    public FlingCardListener(View frame, Object itemAtPosition, FlingListener flingListener) {
        this(frame,itemAtPosition, 15f, Direction.HORIZONTAL, flingListener);
    }

    public FlingCardListener(View frame, Object itemAtPosition, float rotation_degrees, int DIRECTION, FlingListener flingListener) {
        super();
        this.frame = frame;
        this.objectX = frame.getX();
        this.objectY = frame.getY();
        this.objectH = frame.getHeight();
        this.objectW = frame.getWidth();
        this.halfWidth = objectW/2f;
        this.halfHeight = objectH/2f;
        this.dataObject = itemAtPosition;
        this.parentWidth = ((ViewGroup) frame.getParent()).getWidth();
        this.parentHeight = ((ViewGroup) frame.getParent()).getHeight();
        this.BASE_ROTATION_DEGREES = rotation_degrees;
        this.DIRECTION = DIRECTION;
        this.mFlingListener = flingListener;
    }


    public boolean onTouch(View view, MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                //from http://android-developers.blogspot.com/2010/06/making-sense-of-multitouch.html
                // Save the ID of this pointer
                mActivePointerId = event.getPointerId(0);
                final float x = event.getX(mActivePointerId);
                final float y = event.getY(mActivePointerId);

                // Remember where we started
                aDownTouchX = x;
                aDownTouchY = y;
                //to prevent an initial jump of the magnifier, aposX and aPosY must
                //have the values from the magnifier frame
                if (aPosX == 0) {
                    aPosX = frame.getX();
                }
                if (aPosY == 0) {
                    aPosY = frame.getY();
                }

                if (y < objectH/2) {
                    touchPosition = TOUCH_ABOVE;
                } else {
                    touchPosition = TOUCH_BELOW;
                }

                break;

            case MotionEvent.ACTION_UP:
                mActivePointerId = INVALID_POINTER_ID;
                resetCardViewOnStack();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                break;

            case MotionEvent.ACTION_POINTER_UP:
                // Extract the index of the pointer that left the touch sensor
                final int pointerIndex = (event.getAction() &
                        MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mActivePointerId = event.getPointerId(newPointerIndex);
                }
                break;
            case MotionEvent.ACTION_MOVE:

                // Find the index of the active pointer and fetch its position
                final int pointerIndexMove = event.findPointerIndex(mActivePointerId);
                final float xMove = event.getX(pointerIndexMove);
                final float yMove = event.getY(pointerIndexMove);

                //from http://android-developers.blogspot.com/2010/06/making-sense-of-multitouch.html
                // Calculate the distance moved
                final float dx = xMove - aDownTouchX;
                final float dy = yMove - aDownTouchY;


                // Move the frame
                aPosX += dx;
                aPosY += dy;

                // calculate the rotation degrees
                float distobjectX = aPosX - objectX;
                float rotation = BASE_ROTATION_DEGREES * 2.f * distobjectX / parentWidth;
                if (touchPosition == TOUCH_BELOW) {
                    rotation = -rotation;
                }

                //in this area would be code for doing something with the view as the frame moves.
                frame.setX(aPosX);
                frame.setY(aPosY);
                frame.setRotation(rotation);
                mFlingListener.onScroll(getScrollProgressPercentVertical(), getScrollProgressPercentHorizontal());
                break;

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }
        }

        return true;
    }

    private float getScrollProgressPercentVertical() {
        if (movedBeyondLeftBorder()) {
            return -1f;
        } else if (movedBeyondRightBorder()) {
            return 1f;
        } else {
            float zeroToOneValue = (aPosX + halfWidth - leftBorder()) / (rightBorder() - leftBorder());
            return zeroToOneValue * 2f - 1f;
        }
    }

    private float getScrollProgressPercentHorizontal() {
        if (movedBeyondTopBorder()) {
            return -1f;
        } else if (movedBeyondBottomBorder()) {
            return 1f;
        } else {
            float zeroToOneValue = (aPosY + halfHeight - topBorder()) / (bottomBorder() - topBorder());
            return zeroToOneValue * 2f - 1f;
        }
    }

    private boolean resetCardViewOnStack() {
        float scrollHorizontal = getScrollProgressPercentHorizontal();
        float scrollVertical = getScrollProgressPercentVertical();
        if(movedBeyondLeftBorder() && Direction.hasLeft(DIRECTION)){
            // Left Swipe
            onSelected(Direction.LEFT, getExitXPoint(-objectW), 100);
        }else if(movedBeyondRightBorder() && Direction.hasRight(DIRECTION)) {
            // Right Swipe
            onSelected(Direction.RIGHT, getExitXPoint(parentWidth), 100);
        }else if(movedBeyondTopBorder() && Direction.hasTop(DIRECTION)) {
            // Top Swipe
            onSelected(Direction.TOP, getExitYPoint(-objectH), 100);
        }else if(movedBeyondBottomBorder() && Direction.hasBottom(DIRECTION)) {
            // Bottom Swipe
            onSelected(Direction.BOTTOM, getExitYPoint(parentHeight), 100);
        }else {
            scrollHorizontal = 0;
            scrollVertical = 0;
            float abslMoveDistance = Math.abs(aPosX-objectX);
            aPosX = 0;
            aPosY = 0;
            aDownTouchX = 0;
            aDownTouchY = 0;
            frame.animate()
                    .setDuration(200)
                    .setInterpolator(new OvershootInterpolator(1.5f))
                    .x(objectX)
                    .y(objectY)
                    .rotation(0);
            if(abslMoveDistance<4.0){
                mFlingListener.onClick(dataObject);
            }
        }
        mFlingListener.onScroll(scrollHorizontal, scrollVertical);
        return false;
    }

    private boolean movedBeyondLeftBorder() {
        return aPosX+halfWidth < leftBorder();
    }

    private boolean movedBeyondRightBorder() {
        return aPosX+halfWidth > rightBorder();
    }

    private boolean movedBeyondTopBorder() {
        return aPosY+halfHeight < topBorder();
    }

    private boolean movedBeyondBottomBorder() {
        return aPosY+halfHeight > bottomBorder();
    }


    public float leftBorder(){
        return parentWidth/4.f;
    }

    public float rightBorder(){
        return 3*parentWidth/4.f;
    }

    public float topBorder(){
        return parentHeight/4.f;
    }

    public float bottomBorder(){
        return 3*parentHeight/4.f;
    }

    public void onSelected(final int direction,
                           float exitOther, long duration){

        isAnimationRunning = true;
        float exitX = exitOther;
        if(Direction.hasLeft(direction)) {
            exitX = -objectW-getRotationWidthOffset();
        }else if(Direction.hasRight(direction)) {
            exitX = parentWidth+getRotationWidthOffset();
        }

        float exitY = exitOther;
        if(Direction.hasTop(direction)) {
            exitY = -objectH-getRotationHeightOffset();
        }else if(Direction.hasBottom(direction)) {
            exitY = parentHeight+getRotationHeightOffset();
        }
        this.frame.animate()
                .setDuration(duration)
                .setInterpolator(new AccelerateInterpolator())
                .x(exitX)
                .y(exitY)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mFlingListener.exit(direction, dataObject);

                        mFlingListener.onCardExited();
                        isAnimationRunning = false;
                    }
                })
                .rotation(getExitRotation(direction));
    }




    /**
     * Starts a default left exit animation.
     */
    public void selectLeft(){
        if(!isAnimationRunning)
            onSelected(Direction.LEFT, objectY, 200);
    }

    /**
     * Starts a default right exit animation.
     */
    public void selectRight(){
        if(!isAnimationRunning)
            onSelected(Direction.RIGHT, objectY, 200);
    }

    /**
     * Starts a default top exit animation.
     */
    public void selectTop(){
        if(!isAnimationRunning)
            onSelected(Direction.TOP, objectX, 200);
    }

    /**
     * Starts a default bottom exit animation.
     */
    public void selectBottom(){
        if(!isAnimationRunning)
            onSelected(Direction.BOTTOM, objectX, 200);
    }


    private float getExitXPoint(int exitXPoint) {
        float[] x = new float[2];
        x[0] = objectX;
        x[1] = aPosX;

        float[] y = new float[2];
        y[0] = objectY;
        y[1] = aPosY;

        LinearRegression regression =new LinearRegression(x,y);

        //Your typical y = ax+b linear regression
        return (float) regression.slope() * exitXPoint +  (float) regression.intercept();
    }

    private float getExitYPoint(int exitYPoint) {
        float[] x = new float[2];
        x[0] = objectX;
        x[1] = aPosX;

        float[] y = new float[2];
        y[0] = objectY;
        y[1] = aPosY;

        LinearRegression regression =new LinearRegression(x,y);

        //Your typical y = ax+b linear regression
        // x = (y-b)/a
        return (float) ((exitYPoint - (float) regression.intercept())/regression.slope());
    }

    private float getExitRotation(int direction){
        float rotation= BASE_ROTATION_DEGREES * 2.f * (parentWidth - objectX)/parentWidth;
        if (touchPosition == TOUCH_BELOW) {
            rotation = -rotation;
        }
        if(Direction.hasTop(direction) || Direction.hasLeft(direction)){
            rotation = -rotation;
        }
        return rotation;
    }


    /**
     * When the object rotates it's width becomes bigger.
     * The maximum width is at 45 degrees.
     *
     * The below method calculates the width offset of the rotation.
     *
     */
    private float getRotationWidthOffset() {
        return objectW/MAX_COS - objectW;
    }

    private float getRotationHeightOffset() {
        return objectH/MAX_COS - objectH;
    }


    public void setRotationDegrees(float degrees) {
        this.BASE_ROTATION_DEGREES = degrees;
    }


    protected interface FlingListener {
        public void onCardExited();
        public void exit(int direction, Object dataObject);
        public void onClick(Object dataObject);
        public void onScroll(float scrollProgressPercentHorizontal, float scrollProgressPercentVertical);
    }

}





