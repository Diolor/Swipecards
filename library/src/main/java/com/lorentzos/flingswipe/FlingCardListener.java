package com.lorentzos.flingswipe;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by dionysis_lorentzos on 5/8/14
 * for package com.lorentzos.swipecards
 * and project Swipe cards.
 * Use with caution dinausaurs might appear!
 */


public class FlingCardListener implements View.OnTouchListener {

    private static final String TAG = FlingCardListener.class.getSimpleName();
    private static final int INVALID_POINTER_ID = -1;

    private final float objectX;
    private final float objectY;
    private final int objectH;
    private final int objectW;
    private final int parentWidth;
    private final FlingListener mFlingListener;
    private final Object dataObject;
    private final float halfWidth;

    private float aPosX;
    private float aPosY;
    private float aDownTouchX;

    // The active pointer is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;
    private View frame = null;
    private View nextCardFrame = null;

    private boolean isAnimationRunning = false;
    private boolean isDragging = false;
    private float mDragTreshold;


    public FlingCardListener(View frame,View nextCardFrame, Object itemAtPosition, FlingListener flingListener)
    {
        super();
        this.nextCardFrame = nextCardFrame;
        this.frame = frame;
        this.objectX = frame.getX();
        this.objectY = frame.getY();
        this.objectH = frame.getHeight();
        this.objectW = frame.getWidth();
        this.halfWidth = objectW / 2f;
        this.dataObject = itemAtPosition;
        this.parentWidth = ((ViewGroup) frame.getParent()).getWidth();
        this.mFlingListener = flingListener;
        this.mDragTreshold = objectW * 0.1f;

    }


    public boolean onTouch(View view, MotionEvent event)
    {

        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:

                // from http://android-developers.blogspot.com/2010/06/making-sense-of-multitouch.html
                // Save the ID of this pointer

                mActivePointerId = event.getPointerId(0);
                float x = 0;
                float y = 0;
                boolean success = false;
                try
                {
                    x = event.getX(mActivePointerId);
                    y = event.getY(mActivePointerId);
                    success = true;
                }
                catch (IllegalArgumentException e)
                {
                    Log.w(TAG, "Exception in onTouch(view, event) : " + mActivePointerId, e);
                }
                if (success)
                {
                    // Remember where we started
                    aDownTouchX = x;
                    //to prevent an initial jump of the magnifier, aposX and aPosY must
                    //have the values from the magnifier frame
                    if (aPosX == 0)
                    {
                        aPosX = frame.getX();
                    }
                    if (aPosY == 0)
                    {
                        aPosY = frame.getY();
                    }
                }

                view.getParent().requestDisallowInterceptTouchEvent(true);
                break;

            case MotionEvent.ACTION_UP:
                mActivePointerId = INVALID_POINTER_ID;
                if (isDragging) resetCardViewOnStack();
                view.getParent().requestDisallowInterceptTouchEvent(false);
                isDragging = false;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                break;

            case MotionEvent.ACTION_POINTER_UP:
                // Extract the index of the pointer that left the touch sensor
                final int pointerIndex = (event.getAction() &
                        MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId)
                {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mActivePointerId = event.getPointerId(newPointerIndex);
                }
                isDragging = false;
                break;
            case MotionEvent.ACTION_MOVE:

                // Find the index of the active pointer and fetch its position
                final int pointerIndexMove = event.findPointerIndex(mActivePointerId);
                final float xMove = event.getX(pointerIndexMove);

                //from http://android-developers.blogspot.com/2010/06/making-sense-of-multitouch.html
                // Calculate the distance moved
                final float dx = xMove - aDownTouchX;

                if (Math.abs(dx) > mDragTreshold || isDragging)// || Math.abs(dy) > objectH * 0.1f || isDragging)
                {
                    isDragging = true;

                    // Move the frame
                    aPosX += dx;

                    float abslMoveDistance = Math.abs(aPosX - objectX);
                    float percentage = abslMoveDistance / parentWidth;
                    //in this area would be code for doing something with the view as the frame moves.
                    frame.setX(aPosX - mDragTreshold * (1f - percentage));

                    if(abslMoveDistance > 0.4f)
                    {

                        frame.setAlpha(1.4f - percentage - (0.4f * percentage));
                    }
                    if(nextCardFrame != null)
                    {
                        float nextCardScale = 0.8f + percentage * 0.2f;
                        nextCardFrame.setScaleX(nextCardScale);
                        nextCardFrame.setScaleY(nextCardScale);
                    }
                    mFlingListener.onScroll(getScrollProgressPercent());
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            {
                mActivePointerId = INVALID_POINTER_ID;
                view.getParent().requestDisallowInterceptTouchEvent(false);
                isDragging = false;
                break;
            }
        }

        return false;
    }

    private float getScrollProgressPercent()
    {
        if (movedBeyondLeftBorder())
        {
            return -1f;
        }
        else if (movedBeyondRightBorder())
        {
            return 1f;
        }
        else
        {
            float zeroToOneValue = (aPosX + halfWidth - leftBorder()) / (rightBorder() - leftBorder());
            return zeroToOneValue * 2f - 1f;
        }
    }

    private boolean resetCardViewOnStack()
    {
        if (movedBeyondLeftBorder())
        {
            // Left Swipe
            onSelected(true, getExitPoint(-objectW), 100);
            mFlingListener.onScroll(-1.0f);
        }
        else if (movedBeyondRightBorder())
        {
            // Right Swipe
            onSelected(false, getExitPoint(parentWidth), 100);
            mFlingListener.onScroll(1.0f);
        }
        else
        {
            float abslMoveDistance = Math.abs(aPosX - objectX);
            aPosX = 0;
            aDownTouchX = 0;
            frame.animate()
                    .setDuration(200)
                    .setInterpolator(new OvershootInterpolator(1.5f))
                    .x(objectX)
                    .alpha(1).setListener(new AnimatorListenerAdapter() {
                //.rotation(0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    isAnimationRunning = false;
                }
            });
            if(nextCardFrame!=null)
            {
                nextCardFrame.animate().scaleX(1).scaleY(1).setDuration(200);
            }
            isAnimationRunning = true;
            mFlingListener.onScroll(0.0f);
            if (abslMoveDistance < 4.0)
            {
                mFlingListener.onClick(dataObject);
            }
        }
        return false;
    }

    private boolean movedBeyondLeftBorder()
    {
        return aPosX + halfWidth < leftBorder();
    }

    private boolean movedBeyondRightBorder()
    {
        return aPosX + halfWidth > rightBorder();
    }


    public float leftBorder()
    {
        return parentWidth / 4.f;
    }

    public float rightBorder()
    {
        return 3 * parentWidth / 4.f;
    }


    public void onSelected(final boolean isLeft,
                           float exitY, long duration)
    {

        isAnimationRunning = true;
        float exitX;
        if (isLeft)
        {
            exitX = -objectW;// - getRotationWidthOffset();
        }
        else
        {
            exitX = parentWidth;// + getRotationWidthOffset();
        }

        this.frame.animate()
                .setDuration(duration)
                .setInterpolator(new AccelerateInterpolator())
                .x(exitX)
                        //.y(exitY)
                .alpha(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        if (isLeft)
                        {
                            Log.d(TAG, "left.mFlingListener.onCardExited()");
                            mFlingListener.onCardExited();
                            mFlingListener.leftExit(dataObject);
                        }
                        else
                        {
                            Log.d(TAG, "right.mFlingListener.onCardExited()");
                            mFlingListener.onCardExited();
                            mFlingListener.rightExit(dataObject);
                        }
                        isAnimationRunning = false;
                    }
                });
                if(nextCardFrame!=null)
                {
                    nextCardFrame.animate().scaleX(1).scaleY(1).setDuration(duration);
                }
    }


    /**
     * Starts a default left exit animation.
     */
    public void selectLeft()
    {
        if (!isAnimationRunning)
            onSelected(true, objectY, 200);
    }

    /**
     * Starts a default right exit animation.
     */
    public void selectRight()
    {
        if (!isAnimationRunning)
            onSelected(false, objectY, 200);
    }


    private float getExitPoint(int exitXPoint)
    {
        float[] x = new float[2];
        x[0] = objectX;
        x[1] = aPosX;

        float[] y = new float[2];
        y[0] = objectY;
        y[1] = objectY;

        LinearRegression regression = new LinearRegression(x, y);

        //Your typical y = ax+b linear regression
        return (float) regression.slope() * exitXPoint + (float) regression.intercept();
    }

//    private float getExitRotation(boolean isLeft)
//    {
//        float rotation = BASE_ROTATION_DEGREES * 2.f * (parentWidth - objectX) / parentWidth;
//        if (touchPosition == TOUCH_BELOW)
//        {
//            rotation = -rotation;
//        }
//        if (isLeft)
//        {
//            rotation = -rotation;
//        }
//        return rotation;
//    }


    /**
     * When the object rotates it's width becomes bigger.
     * The maximum width is at 45 degrees.
     * <p/>
     * The below method calculates the width offset of the rotation.
     */
//    private float getRotationWidthOffset()
//    {
//        return objectW / MAX_COS - objectW;
//    }

//
//    public void setRotationDegrees(float degrees)
//    {
//        this.BASE_ROTATION_DEGREES = degrees;
//    }

    public boolean isTouching()
    {
        return this.mActivePointerId != INVALID_POINTER_ID;
    }

    public PointF getLastPoint()
    {
        return new PointF(this.aPosX, this.aPosY);
    }

    protected interface FlingListener {
        void onCardExited();

        void leftExit(Object dataObject);

        void rightExit(Object dataObject);

        void onClick(Object dataObject);

        void onScroll(float scrollProgressPercent);
    }

}





