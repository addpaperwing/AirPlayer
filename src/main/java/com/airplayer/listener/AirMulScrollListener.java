package com.airplayer.listener;



/**
 * Created by ZiyiTsang on 15/7/1.
 */
public abstract class AirMulScrollListener extends AirScrollListener {

    private int[] pageScrollDistance = {0, 0, 0};
    private int page = 0;

    public AirMulScrollListener(int viewHeight) {
        super(viewHeight);
    }

    @Override
    protected int getTotalScrollDistance() {
        return pageScrollDistance[page];
    }

    @Override
    protected void setTotalScrollDistance(int dy) {
        pageScrollDistance[page] += dy;
    }

    public void reset() {
        viewScrolledDistance = 0;
        toolbarHide = false;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
