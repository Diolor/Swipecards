package com.lorentzos.flingswipe;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by dionysis_lorentzos on 6/8/14
 * for package com.lorentzos.swipecards
 * and project Swipe cards.
 * Use with caution dinausaurs might appear!
 */
abstract class BaseFlingAdapterView extends AdapterView {


    public BaseFlingAdapterView(Context context) {
        super(context);
    }

    public BaseFlingAdapterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseFlingAdapterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public View getSelectedView() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void setSelection(int i) {
        throw new UnsupportedOperationException("Not supported");
    }


}
