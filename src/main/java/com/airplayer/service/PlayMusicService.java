package com.airplayer.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.Handler;
import android.util.Log;

import com.airplayer.model.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/10.
 */
public class PlayMusicService extends Service {

    public static final String TAG = "PlayMusicService";

    // keys for widget
    public static final String SELECTED_MUSIC_POSITION = "selected_music_position";
    public static final String PLAY_LIST = "play_list";
    public static final String KEY_OF_PLAY_MODE = "key_of_play_mode";

    // keys for sliding up panel
    public static final String ALBUM_ART_OF_SONG_PLAYING = "album_art_of_song_playing";
    public static final String TITLE_OF_SONG_PLAYING = "title_of_song_playing";
    public static final String DURATION_OF_SONG_PLAYING = "duration_of_song_playing";


    // broadcast action
    public static final String START_TO_PLAY_MUSIC = "com.airplayer.START_TO_PLAY_MUSIC";
    public static final String PLAY_MODE_CHANGE = "com.airplayer.PLAY_MODE_CHANGE";
    public static final String PLAYER_OPERATION = "com.airplayer.PLAYER_OPERATION";

    // play mode
    public static final int SINGLE_REPEAT_MODE = 0;
    public static final int ACCORDING_TO_PRIORITY_MODE = 1;
    public static final int SHUFFLE_PLAY_MODE = 2;

    private PlayerControlBinder mBinder = new PlayerControlBinder();
    private PlayModeReceiver receiver;
    private MediaPlayer mPlayer;

    private List<Song> mPlayList = new ArrayList<>();
    private int mPosition;
    private int mPlayMode = 2;
    private Song songPlaying;

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                switch (mPlayMode) {
                    case 0:
                        Log.d(TAG, "on completion, and play mode is " + mPlayMode);
                        break;
                    case 1:
                        mPosition = mPosition > mPlayList.size()? 0: mPosition++;
                        play(mPlayList.get(mPosition));
                        Log.d(TAG, "on completion, and play mode is " + mPlayMode);
                        break;
                    case 2:
                        int random = (int) Math.round(Math.random() * mPlayList.size());
                        play(mPlayList.get(random));
                        mPlayList.remove(random);
                        Log.d(TAG, "on completion, and play mode is " + mPlayMode);
                        break;
                }
            }
        });

        receiver = new PlayModeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PLAY_MODE_CHANGE);
        registerReceiver(receiver, filter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        mPlayer.release();
        super.onDestroy();
    }

    public class PlayerControlBinder extends Binder {

        public void playMusic(int position, List<Song> playList) {
            play(playList.get(position));
            mPosition = position;
            mPlayList = playList;
        }

        public void stopMusic() {
            if (mPlayer != null) {
                mPlayer.stop();
            }
        }

        public void resumeMusic() {
            if (mPlayer != null) {
                mPlayer.start();
            }
        }

        public void pauseMusic() {
            if (mPlayer != null && mPlayer.isPlaying()) {
                mPlayer.pause();
            }
        }

        public double getProgress() {
            return mPlayer.getCurrentPosition() / mPlayer.getDuration();
        }

        public void setProgress(int p) {
            mPlayer.seekTo(mPlayer.getDuration() * p);
        }

        public void setSingleRepeat(boolean isLoop) {
            mPlayer.setLooping(isLoop);
            if (isLoop) {
                mPlayMode = SINGLE_REPEAT_MODE;
            } else {
                mPlayMode = ACCORDING_TO_PRIORITY_MODE;
            }
            Log.d(TAG, "set loop succeed now loop is " + mPlayer.isLooping());
        }

        public void setAccordingToPriority() {
            mPlayMode = ACCORDING_TO_PRIORITY_MODE;
        }

        public void setShufflePlay() {
            mPlayMode = SHUFFLE_PLAY_MODE;
        }

        public Song getSongPlaying() {
            return songPlaying;
        }
    }

    public class PlayModeReceiver extends BroadcastReceiver {

        public PlayModeReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "receive broadcast");
            String action = intent.getAction();
            switch (action) {
                case PLAY_MODE_CHANGE:
                    mPlayMode = intent.getIntExtra(KEY_OF_PLAY_MODE, 1);
                    if (mPlayMode == 0) {
                        mPlayer.setLooping(true);
                    } else {
                        mPlayer.setLooping(false);
                    }
                    Log.d(TAG, "succeed to switch play mode to " + mPlayMode);
                    break;
                case PLAYER_OPERATION:
                    break;
            }
        }
    }

    private void play(Song song) {
        try {
            mPlayer.reset();
            mPlayer.setDataSource(song.getPath());
            mPlayer.prepare();
            mPlayer.start();
            songPlaying = song;
            Intent intent = new Intent(START_TO_PLAY_MUSIC);
            sendBroadcast(intent);
        } catch (IOException e) {
            Log.e(TAG, "fail to set data source of player", e);
        }
    }
}
