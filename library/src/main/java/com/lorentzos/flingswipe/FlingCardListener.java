package com.lorentzos.flingswipe;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by dionysis_lorentzos on 5/8/14
 * for package com.lorentzos.swipecards
 * and project Swipe cards.
 * Use with caution dinausaurs might appear!
 */


public class FlingCardListener implements View.OnTouchListener {

    private final float originalX;
    private final float originalY;
    private final int originalHeight;
    private final int originalWidth;
    private final int halfWidth;
    private final int parentWidth;
    private final FlingListener mFlingListener;
    private final Object dataObject;
    private float BASE_ROTATION_DEGREES = 15.f;

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

    public FlingCardListener(View frame, int parentWidth, float originalX, float originalY,
                             int originalHeight, int originalWidth, Object itemAtPosition,
                             FlingListener flingListener) {
        super();
        this.frame = frame;
        this.parentWidth = parentWidth;
        this.originalX = originalX;
        this.originalY = originalY;
        this.originalHeight = originalHeight;
        this.originalWidth = originalWidth;
        this.halfWidth = this.originalWidth/2;
        this.dataObject = itemAtPosition;
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

                if (y < originalHeight/2) {
                    touchPosition = TOUCH_ABOVE;
                } else {
                    touchPosition = TOUCH_BELOW;
                }

                break;

            case MotionEvent.ACTION_UP:
                mActivePointerId = INVALID_POINTER_ID;
                if ( resetCardViewOnStack() )
                    view.performClick();
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

//              if ( Math.abs(dx) > mTouchSlop || Math.abs(dy) > mTouchSlop){

                // Move the frame
                aPosX += dx;
                aPosY += dy;

                // calculate the rotation degrees
                float distOriginalX = aPosX - originalX;
                float rotation = BASE_ROTATION_DEGREES * 2.f * distOriginalX / parentWidth;
                if (touchPosition == TOUCH_BELOW) {
                    rotation = -rotation;
                }


                // Remember this touch position for the next move event
                //no! see http://stackoverflow.com/questions/17530589/jumping-imageview-while-dragging-getx-and-gety-values-are-jumping?rq=1 and
                // last comment in http://stackoverflow.com/questions/16676097/android-getx-gety-interleaves-relative-absolute-coordinates?rq=1
                //aLastTouchX = xMove;
                //aLastTouchY = yMove;

                //in this area would be code for doing something with the magnified view as the frame moves.
                frame.setX(aPosX);
                frame.setY(aPosY);
                frame.setRotation(rotation);

//              }
                break;

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }
        }

        return true;
    }

    private boolean resetCardViewOnStack() {
        if(aPosX+halfWidth>rightBorder()) {
            onRightSelected();
        }else if( aPosX+halfWidth <leftBorder()){
            onLeftSelected();
        }else {
            float abslMoveDistance = Math.abs(aPosX-originalX);
            aPosX = 0;
            aPosY = 0;
            aDownTouchX = 0;
            aDownTouchY = 0;
            frame.animate()
                    .setDuration(100)
                    .setInterpolator(new DecelerateInterpolator())
                    .x(originalX)
                    .y(originalY)
                    .rotation(0);
            System.out.println(abslMoveDistance);
            if(abslMoveDistance<4.0){
                System.out.println("returns false");
                return true;
            }
        }
        return false;
    }



    public float leftBorder(){
        return parentWidth/4.f;
    }

    public float rightBorder(){
        return 3*parentWidth/4.f;
    }

    public void setRotationDegrees(float degrees) {
        this.BASE_ROTATION_DEGREES = degrees;
    }

    public void onLeftSelected() {
        this.frame.animate()
                .setDuration(100)
                .setInterpolator(new AccelerateInterpolator())
                .x(-originalWidth)
                .y(getExitPoint(-originalWidth))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mFlingListener.onCardExited();
                        mFlingListener.leftExit(dataObject);
                    }
                })
                .rotation(-getExitRotation());
    }


    public void onRightSelected() {
        this.frame.animate()
                .setDuration(100)
                .setInterpolator(new AccelerateInterpolator())
                .x(parentWidth)
                .y(getExitPoint(parentWidth))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mFlingListener.onCardExited();
                        mFlingListener.rightExit(dataObject);
                    }
                })
                .rotation(getExitRotation());
    }


    private float getExitPoint(int exitXPoint) {
        float[] x = new float[2];
        x[0] = originalX;
        x[1] = aPosX;

        float[] y = new float[2];
        y[0] = originalY;
        y[1] = aPosY;

        LinearRegression regression =new LinearRegression(x,y);

        //Your typical y = ax+b linear reggression
        return (float) regression.slope() * exitXPoint +  (float) regression.intercept();
    }

    private float getExitRotation(){
        float rotation= BASE_ROTATION_DEGREES * 2.f * (parentWidth - originalX)/parentWidth;
        if (touchPosition == TOUCH_BELOW) {
            rotation = -rotation;
        }
        return rotation;
    }


    protected interface FlingListener {
        public void onCardExited();
        public void leftExit(Object dataObject);
        public void rightExit(Object dataObject);
    }

}





