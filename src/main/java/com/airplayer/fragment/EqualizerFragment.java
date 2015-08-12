package com.airplayer.fragment;

import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.airplayer.R;
import com.airplayer.activity.AirMainActivity;

/**
 * Created by ZiyiTsang on 15/8/11.
 */
public class EqualizerFragment extends Fragment {

    private Spinner spinner;
    private SeekBar[] bandSeekBars = new SeekBar[5];
    private SeekBar seekBarBassBoost;
    private SeekBar seekBarSurroundSound;

    private Equalizer mEqualizer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEqualizer = ((AirMainActivity) getActivity()).getPlayerControlBinder().getEqualizer();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_equalizer, container, false);
        rootView.setPadding(0, getResources().getInteger(R.integer.padding_action_bar), 0, 0);

        // ===== spinner =====
        spinner = (Spinner) rootView.findViewById(R.id.equalizer_spinner);

        // ===== seek bars =====
        // ----- band seek bars -----
        setupBandSeekBar(rootView);

        // ----- bass booster -----
        seekBarBassBoost = (SeekBar) rootView.findViewById(R.id.seek_bar_bass_boost);

        // ----- surround sound -----
        seekBarSurroundSound = (SeekBar) rootView.findViewById(R.id.seek_bar_surround_sound);


        return rootView;
    }

    private void setupBandSeekBar(View rootView) {
        bandSeekBars[0] = (SeekBar) rootView.findViewById(R.id.seek_bar_60hz);
        bandSeekBars[1] = (SeekBar) rootView.findViewById(R.id.seek_bar_230hz);
        bandSeekBars[2] = (SeekBar) rootView.findViewById(R.id.seek_bar_910hz);
        bandSeekBars[3] = (SeekBar) rootView.findViewById(R.id.seek_bar_4khz);
        bandSeekBars[4] = (SeekBar) rootView.findViewById(R.id.seek_bar_14khz);

        for (short i = 0; i < mEqualizer.getNumberOfBands(); i++) {
            final short bandNum = i;
            bandSeekBars[i].setMax(3000);
            bandSeekBars[i].setProgress(mEqualizer.getBandLevel(bandNum) + 1500);
            bandSeekBars[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mEqualizer.setBandLevel(bandNum, (short) (progress - 1500));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }
                
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) { }
            });
        }
    }
}
