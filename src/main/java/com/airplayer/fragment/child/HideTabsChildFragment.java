package com.airplayer.fragment.child;

import android.support.design.widget.TabLayout;
import android.view.View;

import com.airplayer.activity.AirMainActivity;

/**
 * Created by Administrator on 2015/9/17 0017.
 */
public class HideTabsChildFragment extends ChildFragment {

    @Override
    public void onResume() {
        super.onResume();
        TabLayout tabLayout = ((AirMainActivity) getActivity()).getTabLayout();
        tabLayout.setVisibility(View.GONE);
    }
}
