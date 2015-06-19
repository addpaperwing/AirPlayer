package com.airplayer.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
public class PlayMusicFragment extends Fragment implements View.OnClickListener{

    public static final String TAG = "PlayMusicFragment";

    public static final int HANDLE_SEEK_BAR_PROGRESS = 0;

    private PlayMusicService.PlayerControlBinder mBinder;

    private Song mSong;

    private ImageView mTitleImageView;
    private TextView mTitleTextView;
    private TextView mArtistTextView;
    private ImageView mTitlePlayButton;

    private TextView mPlayingTimeTextView;
    private TextView mDurationTextView;

    private SeekBar mSeekBar;
    private ImageView mSwitchPlayMode;
    private ImageView mPreviousButton;
    private ImageView mPlayAndPauseButton;
    private ImageView mNextButton;
    private ImageView mShuffleList;

    private boolean isLooping = false;
    private boolean shuffle = false;

    GetSongReceiver receiver;

//    public static PlayMusicFragment newInstance(Song song) {
//        PlayMusicFragment fragment = new PlayMusicFragment();
//        Bundle args = new Bundle();
//        args.putSerializable("song", song);
//        fragment.setArguments(args);
//        return fragment;
//    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_SEEK_BAR_PROGRESS:
                    mSeekBar.setProgress(Math.round((float) msg.obj));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinder = ((AirMainActivity) getActivity()).getPlayerControlBinder();
        receiver = new GetSongReceiver();
        IntentFilter filter = new IntentFilter(PlayMusicService.START_TO_PLAY_A_NEW_SONG);
        getActivity().registerReceiver(receiver, filter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (mBinder.isPlaying()) {
                        if (!mBinder.isPause()) {
                            Message msg = new Message();
                            msg.what = HANDLE_SEEK_BAR_PROGRESS;
                            msg.obj = mBinder.getProgress() * mSeekBar.getMax();
                            handler.sendMessage(msg);
                        }
                        sleepThread();
                    }
                }
            }

            private void sleepThread() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Fail to sleep thread " + e);
                }
            }
        }).start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sliding_fragment, container, false);

        // head tool bar section
        mTitleImageView = (ImageView) rootView.findViewById(R.id.sliding_layout_title_image);
        mTitleTextView = (TextView) rootView.findViewById(R.id.sliding_layout_title_song_title);
        mArtistTextView = (TextView) rootView.findViewById(R.id.sliding_layout_title_artist_name);


        mTitlePlayButton = (ImageView) rootView.findViewById(R.id.sliding_layout_title_play_button);
        mTitlePlayButton.setOnClickListener(this);

        // background and center image section

        // foot player control section
        mSeekBar = (SeekBar) rootView.findViewById(R.id.sliding_layout_bottom_seek_bar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int maxProgress = seekBar.getMax();
                    double percentOfBar = ((double)progress) / ((double)maxProgress);
                    Log.d(TAG, progress + " / " + maxProgress + " = " + percentOfBar);
                    mBinder.setProgress(percentOfBar);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSwitchPlayMode = (ImageView) rootView.findViewById(R.id.sliding_layout_switch_mode);
        mSwitchPlayMode.setImageResource((mBinder.getPlayMode() == PlayMusicService.SINGLE_REPEAT_MODE ?
                R.drawable.btn_repeat_one : R.drawable.btn_repeat));
        mSwitchPlayMode.setOnClickListener(this);

        mPreviousButton = (ImageView) rootView.findViewById(R.id.sliding_layout_previous_button);
        mPreviousButton.setOnClickListener(this);

        mPlayAndPauseButton = (ImageView) rootView.findViewById(R.id.sliding_layout_play_and_pause_button);
        mPlayAndPauseButton.setImageResource(mBinder.isPause() ?
                R.drawable.btn_play : android.R.drawable.ic_media_pause);
        mPlayAndPauseButton.setOnClickListener(this);

        mNextButton = (ImageView) rootView.findViewById(R.id.sliding_layout_next_button);
        mNextButton.setOnClickListener(this);

        mShuffleList = (ImageView) rootView.findViewById(R.id.sliding_layout_shuffle_play);
        mShuffleList.setImageResource(mBinder.isListShuffled() ?
                R.drawable.btn_shuffle_all : R.drawable.btn_shuffle);
        mShuffleList.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(receiver);
        super.onDestroy();
    }

    public class GetSongReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mSong = mBinder.getSongPlaying();
            Bitmap nowPlaySongArt = ImageUtils.getListItemThumbnail(
                    getActivity(), QueryUtils.getAlbumArtPath(getActivity(), mSong.getAlbum()));

            // head tool bar section
            mTitleImageView.setImageBitmap(nowPlaySongArt);
            mTitleTextView.setText(mSong.getTitle());
            mArtistTextView.setText(mSong.getArtist());

            // background and center image section

            // foot player control section
            mSwitchPlayMode.setImageResource((mBinder.getPlayMode() == PlayMusicService.SINGLE_REPEAT_MODE ?
                    R.drawable.btn_repeat_one : R.drawable.btn_repeat));
            mPlayAndPauseButton.setImageResource(mBinder.isPause() ?
                    R.drawable.btn_play : android.R.drawable.ic_media_pause);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sliding_layout_switch_mode:
                isLooping = !isLooping;
                mBinder.switchLoopMode(isLooping);
                mSwitchPlayMode.setImageResource(isLooping ?
                        R.drawable.btn_repeat_one : R.drawable.btn_repeat);
                break;
            case R.id.sliding_layout_previous_button:
                mBinder.previous();
                break;
            case R.id.sliding_layout_play_and_pause_button:
            case R.id.sliding_layout_title_play_button:
                if (mBinder.isPause()) {
                    mBinder.resumeMusic();
                    mPlayAndPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                } else {
                    mBinder.pauseMusic();
                    mPlayAndPauseButton.setImageResource(R.drawable.btn_play);
                }
                break;
            case R.id.sliding_layout_next_button:
                mBinder.next();
                break;
            case R.id.sliding_layout_shuffle_play:
                shuffle = !shuffle;
                mBinder.switchShuffleMode(shuffle);
                mShuffleList.setImageResource(shuffle ?
                        R.drawable.btn_shuffle_all : R.drawable.btn_shuffle);
                break;
        }
    }
}
