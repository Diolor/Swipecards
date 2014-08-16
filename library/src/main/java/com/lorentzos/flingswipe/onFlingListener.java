package com.lorentzos.flingswipe;

/**
 * Created by dionysis_lorentzos on 6/8/14
 * for package com.lorentzos.swipecards
 * and project Swipe cards.
 * Use with caution dinausaurs might appear!
 */
public interface onFlingListener {
    void removeFirstObjectInAdapter();
    void onLeftCardExit(Object dataObject);
    void onRightCardExit(Object dataObject);
    void onAdapterAboutToEmpty(int itemsInAdapter);
}
