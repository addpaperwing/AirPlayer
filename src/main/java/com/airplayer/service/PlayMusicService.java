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
import android.provider.MediaStore;
import android.util.Log;

import com.airplayer.model.Song;
import com.airplayer.util.QueryUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/10.
 */
public class PlayMusicService extends Service {

    public static final String TAG = "PlayMusicService";

    // broadcast action
    public static final String START_TO_PLAY_MUSIC = "com.airplayer.START_TO_PLAY_MUSIC";

    // play mode
    public static final int SINGLE_REPEAT_MODE = 0;
    public static final int ACCORDING_TO_PRIORITY_MODE = 1;

    private PlayerControlBinder mBinder = new PlayerControlBinder();
    private MediaPlayer mPlayer;

    private List<Song> mPlayList = new ArrayList<>();
    private List<Song> mOrderList = new ArrayList<>();

    private int mPosition;
    private int mPlayMode = 1;
    private Song songPlaying;

    private boolean isListShuffled;
    private boolean isPause = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                switch (mPlayMode) {
                    case 0:
                        play(mPlayList.get(mPosition));
                        Log.d(TAG, "on completion, and play mode is " + mPlayMode);
                        break;
                    case 1:
                        if (mPosition > mPlayList.size()) {
                            mPosition = 0;
                        } else {
                            mPosition++;
                        }
                        play(mPlayList.get(mPosition));
                        Log.d(TAG, "on completion, and play mode is " + mPlayMode);
                        break;
                }
            }
        });
        mOrderList = QueryUtils.loadSongList(this, null, null, MediaStore.Audio.Media.TITLE);
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
        mPlayer.release();
        super.onDestroy();
    }

    public class PlayerControlBinder extends Binder {

        public void playMusic(int position, List<Song> playList) {
            mPosition = position;
            mPlayList = playList == null ? mOrderList : playList;
            play(mPlayList.get(mPosition));
        }

        public void resumeMusic() {
            if (mPlayer != null) {
                mPlayer.start();
                isPause = false;
            }
        }

        public void pauseMusic() {
            if (mPlayer != null && mPlayer.isPlaying()) {
                mPlayer.pause();
                isPause = true;
                Log.d(TAG, "player is paused");
            }
        }

        public float getProgress() {
            return ((float)mPlayer.getCurrentPosition()) / ((float)mPlayer.getDuration());
        }

        public void setProgress(double p) {
            mPlayer.seekTo((int) (mPlayer.getDuration() * p));
        }

        public void switchPlayMode(boolean isLoop) {
            if (isLoop) {
                mPlayMode = SINGLE_REPEAT_MODE;
            } else {
                mPlayMode = ACCORDING_TO_PRIORITY_MODE;
            }
            Log.d(TAG, "set loop succeed, now play mode is " + mPlayMode);
        }

        public void switchShuffleList(boolean shuffle) {
            if (shuffle) {
                List<Song> shuffleList = new ArrayList<>();
                if (songPlaying != null) {
                    shuffleList.add(songPlaying);
                    mPlayList.remove(songPlaying);
                }
                do {
                    int random = (int) Math.round(Math.random() * mPlayList.size());
                    Song song = mPlayList.get(random);
                    shuffleList.add(song);
                    mPlayList.remove(random);
                } while (mPlayList.size() == 0);
                isListShuffled = true;
                Log.d(TAG, mPlayList.size() + "");
                mPlayList = shuffleList;
                Log.d(TAG, "play list has been shuffled, play mode is " + mPlayMode);
            } else {
                mPlayList = mOrderList;
                isListShuffled = false;
                Log.d(TAG, "play list has been set back to ordered list, play mode is " + mPlayMode);
            }
        }

        public List<Song> getPlayList() {
            return mPlayList;
        }

        public Song getSongPlaying() {
            return songPlaying;
        }

        public int getPlayMode() {
            return mPlayMode;
        }

        public boolean isPlaying() {
            return isPause() || mPlayer.isPlaying();
        }

        public boolean isListShuffled() {
            return isListShuffled;
        }

        public boolean isPause() {
            return isPause;
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
