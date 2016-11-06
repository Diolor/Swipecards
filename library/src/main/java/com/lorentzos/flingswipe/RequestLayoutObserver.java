package com.lorentzos.flingswipe;

import android.database.DataSetObserver;

/**
 *
 */
class RequestLayoutObserver extends DataSetObserver {

	private final SwipeFlingAdapterView adapterView;

	RequestLayoutObserver(SwipeFlingAdapterView adapterView) {
		this.adapterView = adapterView;
	}

	@Override
	public void onChanged() {
		adapterView.requestLayout();
	}

	@Override
	public void onInvalidated() {
		adapterView.requestLayout();
	}

}
