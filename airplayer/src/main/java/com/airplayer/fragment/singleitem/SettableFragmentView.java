package com.airplayer.fragment.singleitem;

import android.support.v7.widget.RecyclerView;

/**
 * Created by ZiyiTsang on 15/7/5.
 */
public interface SettableFragmentView {
    int getRootViewId();
    void setupRecyclerView(RecyclerView recyclerView);
}
