package com.airplayer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.airplayer.R;
import com.airplayer.activity.AirMainActivity;
import com.airplayer.service.PlayMusicService;

/**
 * Created by ZiyiTsang on 15/8/11.
 */
public class EqualizerFragment extends Fragment {

    private SeekBar seekBar60hz;
    private SeekBar seekBar230hz;
    private SeekBar seekBar910hz;
    private SeekBar seekBar4khz;
    private SeekBar seekBar14khz;
    private SeekBar seekBarBassBoost;
    private SeekBar seekBarSurroundSound;

    private PlayMusicService.PlayerControlBinder mBinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinder = ((AirMainActivity) getActivity()).getPlayerControlBinder();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_equalizer, container, false);
        rootView.setPadding(0, getResources().getInteger(R.integer.padding_action_bar), 0, 0);
        seekBar60hz = (SeekBar) rootView.findViewById(R.id.seek_bar_60hz);
        seekBar230hz = (SeekBar) rootView.findViewById(R.id.seek_bar_230hz);
        seekBar910hz = (SeekBar) rootView.findViewById(R.id.seek_bar_910hz);
        seekBar4khz = (SeekBar) rootView.findViewById(R.id.seek_bar_4khz);
        seekBar14khz = (SeekBar) rootView.findViewById(R.id.seek_bar_14khz);
        seekBarBassBoost = (SeekBar) rootView.findViewById(R.id.seek_bar_bass_boost);
        seekBarSurroundSound = (SeekBar) rootView.findViewById(R.id.seek_bar_surround_sound);

        return rootView;
    }
}
