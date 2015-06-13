package com.airplayer.fragment;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.activity.AirMainActivity;
import com.airplayer.model.Song;
import com.airplayer.service.PlayMusicService;
import com.airplayer.util.ImageUtils;
import com.airplayer.util.QueryUtils;

/**
 * Created by ZiyiTsang on 15/6/10.
 */
public class PlayMusicFragment extends Fragment {

    public static final String TAG = "PlayMusicFragment";

    private PlayMusicService.PlayerControlBinder mBinder;

    private Song mSong;

    private ImageView mTitleImageView;
    private TextView mTitleTextView;
    private TextView mArtistTextView;
    private ImageView mTitlePlayButton;
    private TextView mPlayingTimeTextView;
    private TextView mDurationTextView;
    private SeekBar seekBar;

    private boolean isLooping = false;

    public static PlayMusicFragment newInstance(Song song) {
        PlayMusicFragment fragment = new PlayMusicFragment();
        Bundle args = new Bundle();
        args.putSerializable("song", song);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSong = (Song) getArguments().getSerializable("song");
        mBinder = ((AirMainActivity) getActivity()).getPlayerControlBinder();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sliding_fragment, container, false);

        mTitleImageView = (ImageView) rootView.findViewById(R.id.sliding_layout_title_image);
        mTitleImageView.setImageBitmap(
                ImageUtils.getListItemThumbnail(
                        QueryUtils.getAlbumArtPath(
                                getActivity(), mSong.getAlbum())));

        mTitleTextView = (TextView) rootView.findViewById(R.id.sliding_layout_title_song_title);
        mTitleTextView.setText(mSong.getTitle());

        mArtistTextView = (TextView) rootView.findViewById(R.id.sliding_layout_title_artist_name);
        mArtistTextView.setText(mSong.getArtist());

        mTitlePlayButton = (ImageView) rootView.findViewById(R.id.sliding_layout_title_play_button);
        mTitlePlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLooping = !isLooping;
                mBinder.setSingleRepeat(isLooping);
            }
        });

        seekBar = (SeekBar) rootView.findViewById(R.id.sliding_layout_bottom_seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return rootView;
    }
}
