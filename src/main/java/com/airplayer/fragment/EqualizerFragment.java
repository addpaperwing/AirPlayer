package com.airplayer.fragment;

import android.content.SharedPreferences;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.support.v7.widget.Toolbar;

import com.airplayer.R;
import com.airplayer.activity.AirMainActivity;

import java.util.ArrayList;

/**
 * Created by ZiyiTsang on 15/8/11.
 */
public class EqualizerFragment extends Fragment {

    private static final String TAG = EqualizerFragment.class.getSimpleName();

    private Spinner spinner;
    private SeekBar[] bandSeekBars = new SeekBar[5];
    private SeekBar seekBarBassBoost;
    private SeekBar seekBarSurroundSound;

    private Equalizer mEqualizer;

    //===== sharedPreferences =====
    public static final String EQUALIZER_GENRES = "equalizer_genres";
    public static final String EQUALIZER_USER_BAND = "equalizer_user_band";
    private SharedPreferences mSp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEqualizer = ((AirMainActivity) getActivity()).getPlayerControlBinder().getEqualizer();
        mSp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Toolbar toolbar = ((AirMainActivity)getActivity()).getToolbar();
        if (toolbar.getY() < 0) {
            Log.d(TAG, "toolbar.y < 0");
            toolbar.setTranslationY(0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_equalizer, container, false);
        rootView.setPadding(0, getResources().getInteger(R.integer.padding_action_bar), 0, 0);

        // ===== spinner =====
        spinner = (Spinner) rootView.findViewById(R.id.equalizer_spinner);
        ArrayList<String> presets = getPresets();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>
                (getActivity(), R.layout.spinner_item_equalizer, presets);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item_equalizer);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(mSp.getInt(EQUALIZER_GENRES, 0));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSp.edit().putInt(EQUALIZER_GENRES, position).apply();
                short presetPosition = (short) position;
                if (presetPosition < mEqualizer.getNumberOfPresets()) {
                    mEqualizer.usePreset(presetPosition);
                    updateBandSeekBarProgress();
                } else {
                    for (int i = 0; i < mEqualizer.getNumberOfBands(); i++) {
                        bandSeekBars[i].setProgress(mSp.getInt(EQUALIZER_USER_BAND + i, 0) + 1500);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // ===== seek bars =====
        // ----- band seek bars -----
        setupBandSeekBar(rootView);

        // ----- bass booster -----
        seekBarBassBoost = (SeekBar) rootView.findViewById(R.id.seek_bar_bass_boost);

        // ----- surround sound -----
        seekBarSurroundSound = (SeekBar) rootView.findViewById(R.id.seek_bar_surround_sound);

        return rootView;
    }

    private ArrayList<String> getPresets() {
        ArrayList<String> presets = new ArrayList<>();
        short presetsNum = mEqualizer.getNumberOfPresets();
        for (short i = 0; i < presetsNum + 1; i++) {
            if (i == presetsNum) {
                presets.add("User");
            } else {
                presets.add(mEqualizer.getPresetName(i));
            }
        }
        return presets;
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
                    int userBandLevel = progress - 1500;
                    mEqualizer.setBandLevel(bandNum, (short) userBandLevel);
                    if (fromUser) {
                        spinner.setSelection(mEqualizer.getNumberOfPresets());
                        mSp.edit().putInt(EQUALIZER_USER_BAND + bandNum, userBandLevel).apply();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) { }
            });
        }
    }

    private void updateBandSeekBarProgress() {
        for (short i = 0; i < mEqualizer.getNumberOfBands(); i++) {
            bandSeekBars[i].setProgress(mEqualizer.getBandLevel(i) + 1500);
        }
    }
}
