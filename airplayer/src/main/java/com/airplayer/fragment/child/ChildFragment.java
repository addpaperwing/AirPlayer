package com.airplayer.fragment.child;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;

import com.airplayer.activity.AirMainActivity;

/**
 * Created by Administrator on 2015/9/17 0017.
 */
public class ChildFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ===== AppbarLayout =====
        AppBarLayout appBarLayout = ((AirMainActivity) getActivity()).getAppBarLayout();
        appBarLayout.setExpanded(true);
    }
}
