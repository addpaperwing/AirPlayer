package com.airplayer.fragment;

import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.airplayer.R;
import com.airplayer.activity.AirMainActivity;


/**
 * Created by ZiyiTsang on 15/6/4.
 */
public class NowPlayingFragment extends Fragment {

    private ImageView gif;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((AirMainActivity)getActivity()).getToolbar().setVisibility(View.VISIBLE);

        View rootView = inflater.inflate(R.layout.fragment_now_playing, container, false);

        AnimationDrawable animation;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animation = (AnimationDrawable) getActivity().getDrawable(R.drawable.animation_equalizer);
        } else {
            animation = (AnimationDrawable) getResources().getDrawable(R.drawable.animation_equalizer);
        }

        gif = (ImageView) rootView.findViewById(R.id.testGIF);
        gif.setImageDrawable(animation);
        if (animation != null) animation.start();

        return rootView;
    }
}
