package com.airplayer.listener;

import android.support.v7.widget.Toolbar;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by ZiyiTsang on 15/7/15.
 */
public class SimpleAirScrollListener extends AirScrollListener {

    private Toolbar toolbar;

    public SimpleAirScrollListener(int viewHeight, Toolbar toolbar) {
        super(viewHeight);
        this.toolbar = toolbar;
    }

    @Override
    public void onViewScrolled(int viewScrolledDistance) {
        toolbar.setTranslationY(-viewScrolledDistance);
    }

    @Override
    public void onHide() {
        toolbar.animate().translationY(-viewHeight).setInterpolator(new AccelerateInterpolator(1));
    }

    @Override
    public void onShow() {
        toolbar.animate().translationY(0).setInterpolator(new AccelerateInterpolator(1));
    }
}
