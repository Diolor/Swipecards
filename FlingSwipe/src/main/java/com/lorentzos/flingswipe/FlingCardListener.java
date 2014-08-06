package com.lorentzos.flingswipe;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

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
    private final float originalCenterX;
    private final float originalCenterY;
    private final int parentWidth;
    private final int parentHeight;
    private final HelperFlingListener helperFlingListener;
    private float MAX_ROTATION_DEGREES = 15.f;
    private final String TAG = "Action: ";


    public FlingCardListener(TextView frame, int parentWidth, int parentHeight,float originalX, float originalY, int originalHeight, int originalWidth, HelperFlingListener helperFlingListener) {
        super();
        this.frame = frame;
        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;
        this.originalX = originalX;
        this.originalY = originalY;
        this.originalHeight = originalHeight;
        this.originalWidth = originalWidth;
        this.originalCenterX = originalX + originalHeight/2;
        this.originalCenterY = originalY + originalWidth/2;
        this.halfWidth = this.originalWidth/2;
        this.helperFlingListener = helperFlingListener;
    }


    private float aPosX;
    private float aPosY;
    private float aLastTouchX;
    private float aLastTouchY;
    private static final int INVALID_POINTER_ID = -1;

    // The active pointer is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;
    private TextView frame = null;


    private final int TOUCH_ABOVE = 0;
    private final int TOUCH_BELOW = 1;
    private int touchPosition;


    public boolean onTouch(View view, MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                //from http://android-developers.blogspot.com/2010/06/making-sense-of-multitouch.html
                Log.d(TAG, "action down");
                // Save the ID of this pointer
                mActivePointerId = event.getPointerId(0);
                final float x = event.getX(mActivePointerId);
                final float y = event.getY(mActivePointerId);
                // Remember where we started
                aLastTouchX = x;
                aLastTouchY = y;
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
                Log.d(TAG, "action up");
                reset();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                break;

            case MotionEvent.ACTION_POINTER_UP:
                // Extract the index of the pointer that left the touch sensor
                final int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
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
                Log.d(TAG, "action move");
                float xMove = event.getX(pointerIndexMove);
                float yMove = event.getY(pointerIndexMove);

                //from http://android-developers.blogspot.com/2010/06/making-sense-of-multitouch.html
                // Calculate the distance moved
                final float dx = xMove - aLastTouchX;
                final float dy = yMove - aLastTouchY;

//                    if ( Math.abs(dx) > mTouchSlop || Math.abs(dy) > mTouchSlop){
                // Move the frame
                aPosX += dx;
                aPosY += dy;

                // calculate the rotation degrees
                float distOriginalX = aPosX - originalX;
                float rotation = MAX_ROTATION_DEGREES * 2.f * distOriginalX / parentWidth;
                if (touchPosition == TOUCH_BELOW) {
                    rotation = -rotation;
                }


                // Remember this touch position for the next move event
                //no! see http://stackoverflow.com/questions/17530589/jumping-imageview-while-dragging-getx-and-gety-values-are-jumping?rq=1 and
                // last comment in http://stackoverflow.com/questions/16676097/android-getx-gety-interleaves-relative-absolute-coordinates?rq=1
                //aLastTouchX = xMove;
                //aLastTouchY = yMove;
                Log.d(TAG, "we moved");

                //in this area would be code for doing something with the magnified view as the frame moves.
                frame.setX(aPosX);
                frame.setY(aPosY);
                frame.setRotation(rotation);
//                    }
                break;

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }
        }

        return true;
    }

    private void reset() {
        if(aPosX+halfWidth>rightBorder()) {
            onRightSelected();
        }else if( aPosX+halfWidth <leftBorder()){
            onLeftSelected();
        }else {
            aPosX = 0;
            aPosY = 0;
            aLastTouchX = 0;
            aLastTouchY = 0;
            frame.animate()
                    .setDuration(100)
                    .setInterpolator(new DecelerateInterpolator())
                    .x(originalX)
                    .y(originalY)
                    .rotation(0)
//                    .setListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            onEndAnimate();
//                        }
//
//                        @Override
//                        public void onAnimationCancel(Animator animation) {
//                            onAnimationEnd(animation);
//                        }
//                    })
            ;
        }
    }



    public float leftBorder(){
        return parentWidth/4.f;
    }

    public float rightBorder(){
        return 3*parentWidth/4.f;
    }

    public void setRotationDegrees(float degrees) {
        this.MAX_ROTATION_DEGREES = degrees;
    }

    public void onLeftSelected() {
        frame.animate()
                .setDuration(100)
                .setInterpolator(new AccelerateInterpolator())
                .x(-originalWidth)
                .y(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        helperFlingListener.onCardExited();
                        helperFlingListener.leftExit();
                    }
                });
    }

    public void onRightSelected() {

        //TODO correct exit point
        float exitPoint = (originalCenterY-aLastTouchY)*(originalCenterX-parentWidth)/(originalCenterX-aLastTouchX)+originalCenterY;

        frame.animate()
                .setDuration(100)
                .setInterpolator(new AccelerateInterpolator())
                .x(parentWidth)
                .y(exitPoint)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        helperFlingListener.onCardExited();
                        helperFlingListener.rightExit();
                    }
                });
//                .rotation(exitRotation);

    }


}