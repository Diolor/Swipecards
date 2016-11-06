package com.lorentzos.flingswipe;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Adapter;
import android.widget.FrameLayout;

import com.lorentzos.flingswipe.internal.CardEventListener;
import com.lorentzos.flingswipe.internal.Direction;
import com.lorentzos.flingswipe.internal.SwipeOperator;
import com.lorentzos.flingswipe.internal.TopView;

/**
 *
 */
public class SwipeFlingAdapterView extends BaseFlingAdapterView implements CardEventListener {

	private static final int MAX_VISIBLE = 4;
	private static final int MIN_ADAPTER_STACK = 6;
	private static final float ROTATION_DEGREES = 15.f;
	private final int maxVisible;
	private final SwipeOperator swipeOperator = new SwipeOperator(this);
	private final RequestLayoutObserver dataSetObserver = new RequestLayoutObserver(this);
	private final Object object = new Object();
	private boolean inLayout;
	private boolean nextLayoutPass;
	private OnExitListener onExitListener;
	private OnScrollListener onScrollListener;
	private OnRecenterListener onRecenterListener;
	private Adapter adapter;
	private TopView topView;

	private static FrameLayout.LayoutParams createDefaultParams() {
		return new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	public SwipeFlingAdapterView(Context context) {
		this(context, null);
	}

	public SwipeFlingAdapterView(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.SwipeStyle);
	}

	public SwipeFlingAdapterView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwipeAdapterView, defStyle, 0);
		maxVisible = a.getInt(R.styleable.SwipeAdapterView_max_visible, MAX_VISIBLE);
		int minAdapterStack = a.getInt(R.styleable.SwipeAdapterView_min_adapter_stack, MIN_ADAPTER_STACK);
		float rotationDegress = a.getFloat(R.styleable.SwipeAdapterView_rotation_degrees, ROTATION_DEGREES);
		a.recycle();
	}

	@Override
	public void requestLayout() {
		if (!inLayout) {
			super.requestLayout();
		}
	}

	private void layoutChildren(int startingIndex, int adapterCount) {
		int min = Math.min(adapterCount, maxVisible);

		boolean noTopView = true;

		for (int i = startingIndex; i < min; i++) {
			View child = adapter.getView(i, null, this);

			Log.wtf("SwipeFlingAdapterView", "layoutChildren i: " + i);

			if (child.getVisibility() != GONE) {
				makeAndAddView(child);
			}

			if (noTopView) {
				noTopView = false;

				topView = new TopView(child);
				swipeOperator.setSwipeView(topView);
			}
		}

	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private void makeAndAddView(View child) {

		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) child.getLayoutParams();
		if (params == null) {
			params = createDefaultParams();
		}

		addViewInLayout(child, 0, params, true);

		boolean needsMeasure = child.isLayoutRequested();
		if (needsMeasure) {
			measureChild(child, params);
		} else {
			cleanupLayoutState(child);
		}

		int width = child.getMeasuredWidth();
		int height = child.getMeasuredHeight();

		int gravity = params.gravity;
		if (gravity == -1) {
			gravity = Gravity.TOP | Gravity.START;
		}

		int layoutDirection = getLayoutDirection();
		int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
		int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

		int childLeft = getChildLeftPosition(params, width, absoluteGravity);
		int childTop = getChildTopPosition(params, height, verticalGravity);

		child.layout(childLeft, childTop, childLeft + width, childTop + height);
	}

	private void measureChild(View child, FrameLayout.LayoutParams lp) {

		int horizontalPadding = getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin;
		int verticalPadding = getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin;

		int childWidthSpec = getChildMeasureSpec(getWidthMeasureSpec(), horizontalPadding, lp.width);
		int childHeightSpec = getChildMeasureSpec(getHeightMeasureSpec(), verticalPadding, lp.height);

		child.measure(childWidthSpec, childHeightSpec);
	}

	private int getChildLeftPosition(FrameLayout.LayoutParams params, int width, int absoluteGravity) {
		switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
			case Gravity.CENTER_HORIZONTAL:
				int parentMiddleX = (getWidth() + getPaddingLeft() - getPaddingRight() - width) / 2;
				return parentMiddleX + params.leftMargin - params.rightMargin;

			case Gravity.END:
				return getWidth() + getPaddingRight() - width - params.rightMargin;

			case Gravity.START:
			default:
				return getPaddingLeft() + params.leftMargin;
		}
	}

	private int getChildTopPosition(FrameLayout.LayoutParams params, int height, int verticalGravity) {

		switch (verticalGravity) {
			case Gravity.CENTER_VERTICAL:
				int parentMiddleY = (getHeight() + getPaddingTop() - getPaddingBottom() - height) / 2;
				return parentMiddleY + params.topMargin - params.bottomMargin;

			case Gravity.BOTTOM:
				return getHeight() - getPaddingBottom() - height - params.bottomMargin;

			case Gravity.TOP:
			default:
				return getPaddingTop() + params.topMargin;

		}
	}

	@Override
	public Adapter getAdapter() {
		return adapter;
	}

	@Override
	public void setAdapter(Adapter adapter) {
		if (adapter == null) {
			throw new IllegalStateException("Passed adapter was null.");
		}

		if (this.adapter != null) {
			this.adapter.unregisterDataSetObserver(dataSetObserver);
		}

		this.adapter = adapter;
		this.adapter.registerDataSetObserver(dataSetObserver);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		// if we don't have an adapter, we don't need to do anything
		if (adapter == null) {
			return;
		}

		inLayout = true;
		removeAllViewsInLayout();
		topView = null;

		int adapterCount = adapter.getCount();

		if (adapterCount != 0) {
			layoutChildren(0, adapterCount);
		}

		inLayout = false;
		nextLayoutPass = false;
	}

	@Override
	public View getSelectedView() {
		if (topView == null) {
			return null;
		}
		return topView.get();
	}

	public void setOnExitListener(OnExitListener onExitListener) {
		this.onExitListener = onExitListener;
	}

	public void setOnScrollListener(OnScrollListener onScrollListener) {
		this.onScrollListener = onScrollListener;
	}

	public void setOnRecenterListener(OnRecenterListener onRecenterListener) {
		this.onRecenterListener = onRecenterListener;
	}

	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new FrameLayout.LayoutParams(getContext(), attrs);
	}

	@Override
	public void onCardExited(View view, @Direction int direction) {
		if (onExitListener == null) {
			return;
		}

		onExitListener.onExit(view, direction);
	}

	@Override
	public void onScroll(View view, float scrollProgressPercent) {
		onScrollListener.onScroll(view, scrollProgressPercent);
	}

	@Override
	public void onRecenter(View view) {
		onRecenterListener.onRecenter(view);
	}

	@Override
	public void onClick(View view) {
		performItemClick(view, 0, 0);
	}

	public void swipeRight() {
		synchronized (object) {
			swipe(Direction.RIGHT);
		}
	}

	public void swipeLeft() {
		synchronized (object) {
			swipe(Direction.LEFT);
		}
	}

	private void swipe(int direction) {
		if (nextLayoutPass) {
			return;
		}
		nextLayoutPass = true;
		topView.swipe(direction);
	}

}
