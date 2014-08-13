package com.lorentzos.flingswipe;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

/**
 * Created by dionysis_lorentzos on 5/8/14
 * for package com.lorentzos.swipecards
 * and project Swipe cards.
 * Use with caution dinausaurs might appear!
 */

public class SwipeFlingAdapterView extends BaseFlingAdapterView implements HelperFlingListener {


    private Adapter mAdapter;
    private int MAX_VISIBLE = 3;
    private int LAST_OBJECT_IN_STACK = 0;
    private int position = 0;
    private onFlingListener ROList;
    private final DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            Log.d("Observer", "observed change!");
            position=0;
            removeAllViewsInLayout();
            requestLayout();
        }

        @Override
        public void onInvalidated() {
            Log.d("Observer", "observed change!");
            position=0;
            removeAllViewsInLayout();
            requestLayout();
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
        if(mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }
        this.mAdapter = adapter;
        mAdapter.registerDataSetObserver(mDataSetObserver);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // if we don't have an adapter, we don't need to do anything
        if (mAdapter == null) {
            return;
        }

//        if (getChildCount() == 0) {
//            position = 0;
        while (position < mAdapter.getCount() && position < MAX_VISIBLE) {
            View newBottomChild = mAdapter.getView(position, null, this);
            addAndMeasureChild(newBottomChild);
            LAST_OBJECT_IN_STACK = position;
            position++;
        }

        positionItems();
        setTopView();
    }


    private void addAndMeasureChild(View child) {
        ViewGroup.LayoutParams params = child.getLayoutParams();
        System.out.println(params);
        if (params == null) {
            //TODO render the correct dimentsions
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        addViewInLayout(child, 0, params, true);

        child.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
    }

    /**
     * Positions the children at the "correct" positions
     */
    private void positionItems() {
        for (int index = 0; index < getChildCount(); index++) {
            View child = getChildAt(index);

            int width = child.getLayoutParams().width;
            int height = child.getLayoutParams().height;
            int left = (getWidth() - width) / 2;
            int top = (getHeight() - height) / 2;

            child.layout(left, top, left + width, top + height);
        }
    }

    /*
    *  Set the top view and add the fling listener
    */
    private void setTopView() {
        if(getChildCount()>0){
            View tv = getChildAt(LAST_OBJECT_IN_STACK);
            if(tv!=null) {
                FlingCardListener flingCardListener = new FlingCardListener(tv, getWidth(), tv.getX(),
                        tv.getY(), tv.getHeight(), tv.getWidth(), this) ;
                tv.setOnTouchListener(flingCardListener);
            }
        }
    }




    @Override
    public void onCardExited() {
        ROList.removeFirstObjectInAdapter();
        requestLayout();
    }

    @Override
    public void leftExit() {
        ROList.onLeftCardExit();
    }

    @Override
    public void rightExit() {
        ROList.onRightCardExit();
    }


    public void setRemoveObjectsListener(onFlingListener onFlingListener) {
        this.ROList = onFlingListener;
    }
}
