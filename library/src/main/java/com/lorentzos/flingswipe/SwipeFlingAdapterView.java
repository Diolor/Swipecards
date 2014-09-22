package com.lorentzos.flingswipe;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Adapter;
import android.widget.FrameLayout;

/**
 * Created by dionysis_lorentzos on 5/8/14
 * for package com.lorentzos.swipecards
 * and project Swipe cards.
 * Use with caution dinosaurs might appear!
 */

public class SwipeFlingAdapterView extends BaseFlingAdapterView
        implements FlingCardListener.FlingListener {


    private Adapter mAdapter;
    private int MAX_VISIBLE = 4;
    private int MIN_ADAPTER_STACK = 6;
    private int LAST_OBJECT_IN_STACK = 0;
    private onFlingListener ROList;
    private AdapterDataSetObserver mDataSetObserver;
    private boolean mInLayout = false;
    private View mActiveCard = null;


    public SwipeFlingAdapterView(Context context) {
        super(context);
    }

    public SwipeFlingAdapterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeFlingAdapterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }



    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mAdapter != null && mDataSetObserver==null) {
            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.registerDataSetObserver(mDataSetObserver);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAdapter != null && mDataSetObserver!=null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
            mDataSetObserver = null;
        }
    }


    @Override
    public Adapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        this.mAdapter = adapter;
    }


    @Override
    public void onCardExited() {
        ROList.removeFirstObjectInAdapter();
        mActiveCard = null;
        requestLayout();
    }

    @Override
    public void leftExit(Object dataObject) {
        ROList.onLeftCardExit(dataObject);
    }

    @Override
    public void rightExit(Object dataObject) {
        ROList.onRightCardExit(dataObject);
    }


    public void setRemoveObjectsListener(onFlingListener onFlingListener) {
        this.ROList = onFlingListener;
    }



    @Override
    public void requestLayout() {
        System.out.println("Requested layout..");
        if (!mInLayout) {
            System.out.println("Layout is not rendering");
            super.requestLayout();
        }
    }






    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // if we don't have an adapter, we don't need to do anything
        if (mAdapter == null) {
            return;
        }
        mInLayout = true;

        final int adapterCount = mAdapter.getCount();

        System.out.println(adapterCount);

        if(adapterCount == 0) {
            System.out.println("empty adapter");
            removeAllViewsInLayout();
        }else {
            View topCard = getChildAt(LAST_OBJECT_IN_STACK);
            if(mActiveCard!=null && topCard!=null && topCard==mActiveCard) {
                System.out.println("Omit next");
                removeViewsInLayout(0, LAST_OBJECT_IN_STACK);
//                try {
//                    //Remove -if any- additional views
//                    removeViewsInLayout(0, LAST_OBJECT_IN_STACK);
//                }catch (NullPointerException ignored){
//
//                }
                layoutChildren(1, adapterCount);
            }else{
                System.out.println("Reset layout");
                // Reset the UI and set top view listener
                removeAllViewsInLayout();
                layoutChildren(0, adapterCount);
                setTopView();
            }
        }

        mInLayout = false;
    }


    private void layoutChildren(final int startingIndex, int adapterCount){
        int position = startingIndex;

        while (position < adapterCount && position < MAX_VISIBLE) {
            Log.d("Position", String.valueOf(position));
//            Log.d("mAdapter.getCount", String.valueOf(mAdapter.getCount()));
            Log.d("MAX_VISIBLE", String.valueOf(MAX_VISIBLE));
            View newBottomChild = mAdapter.getView(position, null, this);
            if (newBottomChild.getVisibility() != GONE) {
                addAndMeasureChild(newBottomChild);
                LAST_OBJECT_IN_STACK = position;
            }
            position++;
        }

        positionItems(startingIndex);
    }


    private void addAndMeasureChild(View child) {

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();

        addViewInLayout(child, 0, lp, true);

        int childWidthSpec = getChildMeasureSpec(getWidthMeasureSpec(),
                getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin,
                lp.width);

        int childHeightSpec = getChildMeasureSpec(getHeightMeasureSpec(),
                getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin,
                lp.height);

        child.measure(childWidthSpec, childHeightSpec);
    }



    /**
     * Positions the children at the "correct" positions
     *
     * @param firstIteration The position of the first view to start placing on the layout
     */
    private void positionItems(int firstIteration) {
        for (int index = firstIteration; index < getChildCount(); index++) {
            View child = getChildAt(index);

            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();


            int gravity = lp.gravity;
            if (gravity == -1) {
                gravity = Gravity.TOP | Gravity.START;
            }

            final int layoutDirection = getLayoutDirection();
            final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
            final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;


            int childLeft;
            int childTop;
            switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                case Gravity.CENTER_HORIZONTAL:
                    childLeft = (getWidth() + getPaddingLeft() - getPaddingRight()  - width) / 2 +
                            lp.leftMargin - lp.rightMargin;
                    break;
                case Gravity.END:
                    childLeft = getWidth() + getPaddingRight() - width - lp.rightMargin;
                    break;
                case Gravity.START:
                default:
                    childLeft = getPaddingLeft() + lp.leftMargin;
                    break;
            }

            switch (verticalGravity) {
                case Gravity.CENTER_VERTICAL:
                    childTop = (getHeight() + getPaddingTop() - getPaddingBottom()  - width) / 2 +
                            lp.topMargin - lp.bottomMargin;
                    break;
                case Gravity.BOTTOM:
                    childTop = getHeight() - getPaddingBottom() - height - lp.bottomMargin;
                    break;
                case Gravity.TOP:
                default:
                    childTop = getPaddingTop() + lp.topMargin;
                    break;
            }

            Log.d("isLayoutRequested", String.valueOf(isLayoutRequested()));
            child.layout(childLeft, childTop, childLeft + width, childTop + height);
        }
    }



    /**
    *  Set the top view and add the fling listener
    */
    private void setTopView() {
        if(getChildCount()>0){
            mActiveCard = getChildAt(LAST_OBJECT_IN_STACK);
            if(mActiveCard!=null) {
                FlingCardListener flingCardListener = new FlingCardListener(mActiveCard, getWidth(), mActiveCard.getX(),
                        mActiveCard.getY(), mActiveCard.getHeight(), mActiveCard.getWidth(), mAdapter.getItem(0), this) ;
                mActiveCard.setOnTouchListener(flingCardListener);
            }
        }
    }


    public void setMaxVisible(int MAX_VISIBLE){
        this.MAX_VISIBLE = MAX_VISIBLE;
    }

    public void setMinStackInAdapter(int MIN_ADAPTER_STACK){
        this.MIN_ADAPTER_STACK = MIN_ADAPTER_STACK;
    }


    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new FrameLayout.LayoutParams(getContext(), attrs);
    }


    class AdapterDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            System.out.println("Changed..");
            requestLayout();
        }

        @Override
        public void onInvalidated() {
            requestLayout();
        }

    }



    public interface onFlingListener {
        public void removeFirstObjectInAdapter();
        public void onLeftCardExit(Object dataObject);
        public void onRightCardExit(Object dataObject);
        public void onAdapterAboutToEmpty(int itemsInAdapter);
    }


}
