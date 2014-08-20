package com.lorentzos.flingswipe;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
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

public class SwipeFlingAdapterView extends BaseFlingAdapterView implements HelperFlingListener {


    private Adapter mAdapter;
    private int MAX_VISIBLE = 4;
    private int MIN_ADAPTER_STACK = 6;
    private int LAST_OBJECT_IN_STACK = 0;
    private onFlingListener ROList;

    private final DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            observedChange();
        }

        @Override
        public void onInvalidated() {
            observedChange();
        }


        private void observedChange() {

            final int adapterCount = mAdapter.getCount();

            if(adapterCount <= MIN_ADAPTER_STACK){
                ROList.onAdapterAboutToEmpty(adapterCount);
            }

            if(adapterCount  ==0){
                removeAllViewsInLayout();
            }else if( adapterCount == 1){
                //Add one object and set top view

                removeAllViewsInLayout();
                layoutChildren(0);
                setTopView();

            }else if( adapterCount <= MAX_VISIBLE){
                //Add objects but leave the top view as is
                try {
                    //Remove -if any- additional views
                    removeViewsInLayout(0, LAST_OBJECT_IN_STACK);
                }catch (NullPointerException e){
                }
                layoutChildren(1);
            }

        }

    };



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
    public Adapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }
        this.mAdapter = adapter;
        mAdapter.registerDataSetObserver(mDataSetObserver);
    }


    @Override
    public void onCardExited() {
        ROList.removeFirstObjectInAdapter();
        removeAllViewsInLayout();
        layoutChildren(0);
        setTopView();
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
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // if we don't have an adapter, we don't need to do anything
        if (mAdapter == null) {
            return;
        }
//        layoutChildren(0);
    }


    private void layoutChildren(final int index){
        int position = index;

        while (position < mAdapter.getCount() && position < MAX_VISIBLE) {
            View newBottomChild = mAdapter.getView(position, null, this);
            if (newBottomChild.getVisibility() != GONE) {
                addAndMeasureChild(newBottomChild);
                LAST_OBJECT_IN_STACK = position;
            }
            position++;
        }

        positionItems(index);
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

            int childLeft;
            int childTop;

            int gravity = lp.gravity;
            if (gravity == -1) {
                gravity = Gravity.TOP | Gravity.START;
            }

            final int layoutDirection = getLayoutDirection();
            final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
            final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

            switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                case Gravity.CENTER_HORIZONTAL:
                    childLeft = (getWidth() + getPaddingLeft() - getPaddingRight()  - width) / 2 +
                            lp.leftMargin - lp.rightMargin;
                    break;
                case Gravity.RIGHT:
                    childLeft = getWidth() + getPaddingRight() - width - lp.rightMargin;
                    break;
                case Gravity.LEFT:
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

            child.layout(childLeft, childTop, childLeft + width, childTop + height);
            System.out.println("positioned");
        }
    }



    /**
    *  Set the top view and add the fling listener
    */
    private void setTopView() {
        if(getChildCount()>0){
            View tv = getChildAt(LAST_OBJECT_IN_STACK);

            if(tv!=null) {
                FlingCardListener flingCardListener = new FlingCardListener(tv, getWidth(), tv.getX(),
                        tv.getY(), tv.getHeight(), tv.getWidth(), mAdapter.getItem(0), this) ;
                tv.setOnTouchListener(flingCardListener);
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

}
