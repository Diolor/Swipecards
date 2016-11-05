package com.lorentzos.flingswipe;

import android.database.DataSetObserver;
import android.util.Log;

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
		Log.wtf("RequestLayoutObserver", "onChanged ");
		adapterView.requestLayout();
	}

	@Override
	public void onInvalidated() {
		Log.wtf("RequestLayoutObserver", "onInvalidated ");
		adapterView.requestLayout();
	}

}
