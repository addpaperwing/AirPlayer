package com.airplayer.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;


/**
 * Created by ZiyiTsang on 15/7/1.
 */
public abstract class AirMulScrollListener extends RecyclerView.OnScrollListener {

    private int[] pageScrollDistance = {0, 0, 0};
    private int page = 0;

    public void setPage(int page) {
        this.page = page;
    }

    private int viewHeight;
    private int viewScrolledDistance = 0;
    private boolean toolbarHide = false;

    public AirMulScrollListener(int viewHeight) {
        this.viewHeight = viewHeight;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        // codes below makes tool bar act like a child view of recyclerView
        viewScrolledDistance += dy;
        if (dy > 0) {
            if (viewScrolledDistance > viewHeight) {
                viewScrolledDistance = viewHeight;
                toolbarHide = true;
            }
        } else {
            if (viewScrolledDistance < 0) {
                viewScrolledDistance = 0;
                toolbarHide = false;
            }
        }
        onViewScrolled(viewScrolledDistance);

        pageScrollDistance[page] += dy;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            if (!toolbarHide && viewScrolledDistance > 0 && pageScrollDistance[page] > viewHeight) {
                onHide();
                toolbarHide = true;
                viewScrolledDistance = viewHeight;
            } else if (toolbarHide && viewScrolledDistance < viewHeight || pageScrollDistance[page] < viewHeight){
                onShow();
                toolbarHide = false;
                viewScrolledDistance = 0;
            }
        }
    }

    public void reset() {
        viewScrolledDistance = 0;
        toolbarHide = false;
    }

    public abstract void onViewScrolled(int viewScrolledDistance);
    public abstract void onHide();
    public abstract void onShow();
}
