package com.lorentzos.flingswipe;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.PointF;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
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

    private static final String TAG = FlingCardListener.class.getSimpleName();
    private static final int INVALID_POINTER_ID = -1;
    private static final int TOUCH_ABOVE = 0;
    private static final int TOUCH_BELOW = 1;

    private final float objectX;
    private final float objectY;
    private final int objectH;
    private final int objectW;
    private final int parentWidth;
    private final FlingListener mFlingListener;
    private final Object dataObject;
    private final float halfWidth;
    private float baseRotationDegrees;

    private float aPosX;
    private float aPosY;
    private float aDownTouchX;
    private float aDownTouchY;

    // The active pointer is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;
    private View frame = null;
    private CheckForTap pendingCheckForTap = null;
    private UnsetPressedState unsetPressedState = null;
    private PerformClick performClick = null;


    private int touchPosition;
    private boolean isAnimationRunning = false;
    private float MAX_COS = (float) Math.cos(Math.toRadians(45));
    private boolean isTap = true;

    public FlingCardListener(View frame, Object itemAtPosition, FlingListener flingListener) {
        this(frame, itemAtPosition, 15f, flingListener);
    }

    public FlingCardListener(View frame, Object itemAtPosition, float rotationDegrees, FlingListener flingListener) {
        super();
        this.frame = frame;
        this.objectX = frame.getX();
        this.objectY = frame.getY();
        this.objectH = frame.getHeight();
        this.objectW = frame.getWidth();
        this.halfWidth = objectW / 2f;
        this.dataObject = itemAtPosition;
        this.parentWidth = ((ViewGroup) frame.getParent()).getWidth();
        this.baseRotationDegrees = rotationDegrees;
        this.mFlingListener = flingListener;

    }


    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                // from http://android-developers.blogspot.com/2010/06/making-sense-of-multitouch.html
                // Save the ID of this pointer

                mActivePointerId = event.getPointerId(0);
                float x = 0;
                float y = 0;
                boolean success = false;
                try {
                    x = event.getX(mActivePointerId);
                    y = event.getY(mActivePointerId);
                    success = true;
                } catch (IllegalArgumentException e) {
                    Log.w(TAG, "Exception in onTouch(view, event) : " + mActivePointerId, e);
                }
                if (success) {
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

                    if (y < objectH / 2) {
                        touchPosition = TOUCH_ABOVE;
                    } else {
                        touchPosition = TOUCH_BELOW;
                    }

                    if (pendingCheckForTap == null) {
                        pendingCheckForTap = new CheckForTap();
                    }
                    pendingCheckForTap.x = event.getX();
                    pendingCheckForTap.y = event.getY();
                    // delay the tap feedback because it might be a move
                    frame.postDelayed(pendingCheckForTap, ViewConfiguration.getTapTimeout());
                    isTap = true;
                }

                view.getParent().requestDisallowInterceptTouchEvent(true);
                break;

            case MotionEvent.ACTION_UP:
                mActivePointerId = INVALID_POINTER_ID;
                resetCardViewOnStack();

                if (isTap && pendingCheckForTap != null) {
                    pendingCheckForTap.run();
                    //schedule the click action after the end of the visual effects
                    if (performClick == null) {
                        performClick = new PerformClick();
                    }
                    if (!frame.post(performClick)) {
                        performClick.run();
                    }
                }

                if (unsetPressedState == null) {
                    unsetPressedState = new UnsetPressedState();
                }
                if (isTap) {
                    // wait for the end of the pressed state before unsetting the pressed state
                    frame.postDelayed(unsetPressedState, ViewConfiguration.getPressedStateDuration());
                } else {
                    // instantly unset the pressed state
                    if (!frame.post(unsetPressedState)) {
                        unsetPressedState.run();
                    }
                }
                removeTapCallback();

                view.getParent().requestDisallowInterceptTouchEvent(false);
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

                if (Math.abs(dx) >= 3.0 || Math.abs(dy) >= 3.0) {
                    // this is a clear move, cancel the tap to avoid drawable state change
                    removeTapCallback();
                    frame.setPressed(false);
                } else {
                    if (pendingCheckForTap != null) {
                        pendingCheckForTap.x = xMove;
                        pendingCheckForTap.y = yMove;
                    }
                    // don't update the view if we are not sure that's a real move
                    break;
                }

                // Move the frame
                aPosX += dx;
                aPosY += dy;

                // calculate the rotation degrees
                float distobjectX = aPosX - objectX;
                float rotation = baseRotationDegrees * 2.f * distobjectX / parentWidth;
                if (touchPosition == TOUCH_BELOW) {
                    rotation = -rotation;
                }

                //in this area would be code for doing something with the view as the frame moves.
                frame.setX(aPosX);
                frame.setY(aPosY);
                frame.setRotation(rotation);
                mFlingListener.onScroll(getScrollProgressPercent());
                break;

            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = INVALID_POINTER_ID;
                frame.setPressed(false);
                view.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }

        return true;
    }

    private void removeTapCallback() {
        if (pendingCheckForTap != null) {
            frame.removeCallbacks(pendingCheckForTap);
            isTap = false;
        }
    }

    private float getScrollProgressPercent() {
        if (movedBeyondLeftBorder()) {
            return -1f;
        } else if (movedBeyondRightBorder()) {
            return 1f;
        } else {
            float zeroToOneValue = (aPosX + halfWidth - leftBorder()) / (rightBorder() - leftBorder());
            return zeroToOneValue * 2f - 1f;
        }
    }

    private boolean resetCardViewOnStack() {
        if (movedBeyondLeftBorder()) {
            // Left Swipe
            onSelected(true, getExitPoint(-objectW), 100);
            mFlingListener.onScroll(-1.0f);
        } else if (movedBeyondRightBorder()) {
            // Right Swipe
            onSelected(false, getExitPoint(parentWidth), 100);
            mFlingListener.onScroll(1.0f);
        } else {
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
            mFlingListener.onScroll(0.0f);
        }
        return false;
    }

    private boolean movedBeyondLeftBorder() {
        return aPosX + halfWidth < leftBorder();
    }

    private boolean movedBeyondRightBorder() {
        return aPosX + halfWidth > rightBorder();
    }


    public float leftBorder() {
        return parentWidth / 4.f;
    }

    public float rightBorder() {
        return 3 * parentWidth / 4.f;
    }


    public void onSelected(final boolean isLeft,
                           float exitY, long duration) {

        isAnimationRunning = true;
        float exitX;
        if (isLeft) {
            exitX = -objectW - getRotationWidthOffset();
        } else {
            exitX = parentWidth + getRotationWidthOffset();
        }

        this.frame.animate()
                .setDuration(duration)
                .setInterpolator(new AccelerateInterpolator())
                .x(exitX)
                .y(exitY)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (isLeft) {
                            mFlingListener.onCardExited();
                            mFlingListener.leftExit(dataObject);
                        } else {
                            mFlingListener.onCardExited();
                            mFlingListener.rightExit(dataObject);
                        }
                        isAnimationRunning = false;
                    }
                })
                .rotation(getExitRotation(isLeft));
    }


    /**
     * Starts a default left exit animation.
     */
    public void selectLeft() {
        if (!isAnimationRunning)
            onSelected(true, objectY, 200);
    }

    /**
     * Starts a default right exit animation.
     */
    public void selectRight() {
        if (!isAnimationRunning)
            onSelected(false, objectY, 200);
    }


    private float getExitPoint(int exitXPoint) {
        float[] x = new float[2];
        x[0] = objectX;
        x[1] = aPosX;

        float[] y = new float[2];
        y[0] = objectY;
        y[1] = aPosY;

        LinearRegression regression = new LinearRegression(x, y);

        //Your typical y = ax+b linear regression
        return (float) regression.slope() * exitXPoint + (float) regression.intercept();
    }

    private float getExitRotation(boolean isLeft) {
        float rotation = baseRotationDegrees * 2.f * (parentWidth - objectX) / parentWidth;
        if (touchPosition == TOUCH_BELOW) {
            rotation = -rotation;
        }
        if (isLeft) {
            rotation = -rotation;
        }
        return rotation;
    }

    private final class CheckForTap implements Runnable {
        public float x;
        public float y;

        @Override
        public void run() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                frame.drawableHotspotChanged(x, y);
            }
            frame.setPressed(true);
        }
    }
    private final class UnsetPressedState implements Runnable {
        @Override
        public void run() {
            frame.setPressed(false);
        }
    }
    private final class PerformClick implements Runnable {

        @Override
        public void run() {
            mFlingListener.onClick(dataObject);
        }
    }


    /**
     * When the object rotates it's width becomes bigger.
     * The maximum width is at 45 degrees.
     * <p/>
     * The below method calculates the width offset of the rotation.
     */
    private float getRotationWidthOffset() {
        return objectW / MAX_COS - objectW;
    }


    public void setRotationDegrees(float degrees) {
        this.baseRotationDegrees = degrees;
    }

    public boolean isTouching() {
        return this.mActivePointerId != INVALID_POINTER_ID;
    }

    public PointF getLastPoint() {
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





