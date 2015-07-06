package com.airplayer.adapter;

import android.support.v7.widget.RecyclerView;
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

    private int viewHeight;
    private int viewScrolledDistance = 0;
    private boolean toolbarHide = false;

    public AirScrollListener(int viewHeight) {
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

        // measure the totalDistance user scrolled, min is 0, max is pixels of height of recyclerView
        totalScrollDistance += dy;

        // set parallax translationY to half of totalScrollDistance every scrolled
        // make it look like half distance scrolled than others
        if (haveParallax) {
            mParallax.setTranslationY(totalScrollDistance / 2);
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            // if parallax is null, initial it when user drag recycler view
            if (mParallax == null && haveParallax) {
                initParallax(recyclerView);
            }
        }

        // when recyclerView is settled, toolbar start to animate (show/hide) in different situation
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            if (!toolbarHide && viewScrolledDistance > 0 && totalScrollDistance > viewHeight) {
                onHide();
                toolbarHide = true;
                viewScrolledDistance = viewHeight;
            } else if (toolbarHide && viewScrolledDistance < viewHeight || totalScrollDistance < viewHeight){
                onShow();
                toolbarHide = false;
                viewScrolledDistance = 0;
            }

            if (totalScrollDistance == 0) {
                // when toolbar back to top of the view (totalScrollDistance back to 0)
                // set it's color to transparent
                onScrollBackToTop();
            }
        }
    }

    public abstract void onViewScrolled(int viewScrolledDistance);
    public abstract void onHide();
    public abstract void onShow();
    public abstract void onScrollBackToTop();
}
