package com.airplayer.listener;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by ZiyiTsang on 15/7/1.
 */
public abstract class AirScrollListener extends RecyclerView.OnScrollListener {

    private boolean haveParallax = false;

    public void setHaveParallax(boolean haveParallax) {
        this.haveParallax = haveParallax;
    }

    private View mParallax = null;

    private void initParallax(RecyclerView recyclerView) {
        View topView = recyclerView.getChildAt(0);
        mParallax = getTopParallax(topView);
    }

    private View getTopParallax(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            return getTopParallax(group.getChildAt(0));
        } else {
            return view;
        }
    }

    private int totalScrollDistance = 0;

    protected int getTotalScrollDistance() {
        return totalScrollDistance;
    }

    protected void setTotalScrollDistance(int dy) {
        totalScrollDistance += dy;
    }

    protected int viewHeight;
    protected int viewScrolledDistance = 0;
    protected boolean toolbarHide = false;

    public AirScrollListener(int viewHeight) {
        this.viewHeight = viewHeight;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        // codes below makes tool bar move with recyclerView when it scrolled
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

        // measure the totalDistance user scrolled, min is 0, max is pixels of height of recyclerView
        setTotalScrollDistance(dy);

        // set parallax translationY to half of totalScrollDistance every scrolled
        // it will look like half distance scrolled than others and create a parallax
        if (haveParallax) {
            mParallax.setTranslationY(getTotalScrollDistance() / 2);
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if (haveParallax) {
            if (mParallax == null && newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                // if parallax is null, initial it when user drag recycler view
                initParallax(recyclerView);
            }
        }

        // when recyclerView is settled, toolbar start to animate (show/hide) in different situation
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            if (!toolbarHide && viewScrolledDistance > 0 && getTotalScrollDistance() > viewHeight) {
                onHide();
                toolbarHide = true;
                viewScrolledDistance = viewHeight;
            } else if (toolbarHide && viewScrolledDistance < viewHeight || getTotalScrollDistance() < viewHeight){
                onShow();
                toolbarHide = false;
                viewScrolledDistance = 0;
            }

            if (getTotalScrollDistance() == 0) {
                // when toolbar back to top of the view (totalScrollDistance back to 0)
                // set it's color to transparent
                onScrollBackToTop();
            }
        }
    }

    public abstract void onViewScrolled(int viewScrolledDistance);
    public abstract void onHide();
    public abstract void onShow();
    public void onScrollBackToTop() { }
}
