package com.airplayer.listener;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Administrator on 2015/9/10 0010.
 */
public abstract class EasyRecyclerViewListener extends RecyclerView.OnScrollListener {

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (!recyclerView.canScrollVertically(1) && recyclerView.canScrollVertically(-1)) {
            onScrollToBottom();
        }
    }

    public abstract void onScrollToBottom();
}
