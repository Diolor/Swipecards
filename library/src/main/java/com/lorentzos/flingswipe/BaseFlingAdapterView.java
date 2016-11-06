package com.lorentzos.flingswipe;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Adapter;
import android.widget.AdapterView;

/**
 */
abstract class BaseFlingAdapterView extends AdapterView<Adapter> {

	private int heightMeasureSpec;
	private int widthMeasureSpec;

	BaseFlingAdapterView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setSelection(int i) {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		this.widthMeasureSpec = widthMeasureSpec;
		this.heightMeasureSpec = heightMeasureSpec;
	}

	public int getWidthMeasureSpec() {
		return widthMeasureSpec;
	}

	public int getHeightMeasureSpec() {
		return heightMeasureSpec;
	}

}
