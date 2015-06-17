package com.airplayer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airplayer.activity.AirMainActivity;


/**
 * Created by ZiyiTsang on 15/6/4.
 */
public class NowPlayingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((AirMainActivity)getActivity()).getToolbar().setVisibility(View.VISIBLE);

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
