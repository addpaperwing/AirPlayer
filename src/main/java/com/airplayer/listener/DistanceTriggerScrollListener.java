package com.airplayer.listener;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

/**
 * Created by ZiyiTsang on 15/7/31.
 */
public class DistanceTriggerScrollListener extends SimpleAirScrollListener {

    private int triggerDistance;
    private int regularDistance;

    public DistanceTriggerScrollListener(int viewHeight, Toolbar toolbar, int triggerDistance) {
        super(viewHeight, toolbar);
        this.triggerDistance = triggerDistance;
        regularDistance = triggerDistance;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (getTotalScrollDistance() >= triggerDistance) {
            onScrollToTriggerDistance();
        }
    }

    public void onScrollToTriggerDistance() {
        triggerDistance = triggerDistance + regularDistance;
    }
}
