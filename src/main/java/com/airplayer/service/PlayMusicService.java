package com.airplayer.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.airplayer.model.Song;
import com.airplayer.notification.AirNotification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/10.
 */
public class PlayMusicService extends Service {

    public static final String TAG = "PlayMusicService";

    // broadcast action and extra keys - value
    public static final String PLAY_STATE_CHANGE = "com.airplayer.PLAY_STATE_CHANGE";
    public static final String PLAY_STATE_KEY = "com.airplayer.PLAY_STATE_CHANGE.PLAY_STATE_KEY";
    public static final int PLAY_STATE_PLAY = 0;
    public static final int PLAY_STATE_PAUSE = 1;

    public static final String NOTIFICATION_OPERATION = "com.airplayer.NOTIFICATION_OPERATION";
    public static final String NOTIFICATION_OPERATION_KEY = "com.airplayer.NOTIFICATION_OPERATION_KEY";
    public static final int NOTIFICATION_OPERATION_PREVIOUS = 5;
    public static final int NOTIFICATION_OPERATION_PLAY_PAUSE = 0;
    public static final int NOTIFICATION_OPERATION_NEXT = 2;

    // play mode
    public static final int PLAY_LIST_MODE = 0;
    public static final int LOOP_LIST_MODE = 1;
    public static final int SINGLE_SONG_LOOP_MODE = 2;

    private PlayerControlBinder mBinder = new PlayerControlBinder();
    private MediaPlayer mPlayer;

    private List<Song> mPlayList = new ArrayList<>();

    private int mPosition;
    private int previousPosition;

    private Song songPlaying;

    private int mPlayMode = PLAY_LIST_MODE;

    private boolean pause = false;
    private boolean shuffle = false;

    private AirNotification notification;

    private NotificationControlReceiver receiver;

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mPlayMode != SINGLE_SONG_LOOP_MODE) {
                    if (shuffle) {
                        mPosition = (int) Math.round(Math.random() * (mPlayList.size() - 1));
                    } else {
                        nextPosition();
                    }
                }
                play();
                if (mPlayMode == PLAY_LIST_MODE) {
                    mBinder.pauseMusic();
                }
            }
        });
        receiver = new NotificationControlReceiver();
        IntentFilter filter = new IntentFilter(NOTIFICATION_OPERATION);
        registerReceiver(receiver, filter);
        Log.d(TAG, "service create");
        notification = new AirNotification(this);
        notification.cancel();
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
        mBinder.onActivityFinish();
        mPlayer.release();
        super.onDestroy();
        Log.d(TAG, "service destroy");
    }

    public class PlayerControlBinder extends Binder {

        public void playMusic(int position, List<Song> playList) {
            mPosition = position;
            mPlayList = playList;
            previousPosition = mPosition - 1;
            play();
        }

        public void resumeMusic() {
            if (mPlayer != null) {
                songPlaying.setPause(false);
                mPlayer.start();
                pause = false;
                Intent intent = new Intent(PLAY_STATE_CHANGE);
                intent.putExtra(PLAY_STATE_KEY, PLAY_STATE_PLAY);
                sendBroadcast(intent);
                notification.push(songPlaying);
                Log.d(TAG, "player is resumed");
            }
        }

        public void pauseMusic() {
            if (mPlayer != null) {
                songPlaying.setPause(true);
                mPlayer.pause();
                pause = true;
                Intent intent = new Intent(PLAY_STATE_CHANGE);
                intent.putExtra(PLAY_STATE_KEY, PLAY_STATE_PAUSE);
                sendBroadcast(intent);
                notification.push(songPlaying);
                Log.d(TAG, "player is paused");
            }
        }

        public void previousMusic() {
            previousPosition();
            play();
        }

        public void nextMusic() {
            nextPosition();
            play();
        }

        public float getProgress() {
            return ((float)mPlayer.getCurrentPosition()) / ((float)mPlayer.getDuration());
        }

        public void setProgress(double p) {
            mPlayer.seekTo((int) (mPlayer.getDuration() * p));
        }

        public void setPlayMode(int mode) {
            mPlayMode = mode;
            Log.d(TAG, "switch loop succeed, now play mode is " + mPlayMode);
        }

        public int getPlayMode() {
            return mPlayMode;
        }

        public void setShuffle(boolean switcher) {
            shuffle = switcher;
        }

        public boolean isShuffle() {
            return shuffle;
        }

        public List<Song> getPlayList() {
            return mPlayList;
        }

        public Song getSongPlaying() {
            return songPlaying;
        }

        public boolean isPlaying() {
            return mPlayer.isPlaying() || pause;
        }

        public boolean isPause() {
            return pause;
        }

        public int getPosition() {
            return mPosition;
        }

        public void onActivityFinish() {
            notification.cancel();
            unregisterReceiver(receiver);
        }
    }

    private class NotificationControlReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, intent.getIntExtra(NOTIFICATION_OPERATION_KEY, -1) + "");
            switch (intent.getIntExtra(NOTIFICATION_OPERATION_KEY, -1)) {
                case NOTIFICATION_OPERATION_PREVIOUS:
                    previousPosition();
                    play();
                    break;
                case NOTIFICATION_OPERATION_PLAY_PAUSE:
                    if (pause) {
                        mBinder.resumeMusic();
                    } else {
                        mBinder.pauseMusic();
                    }
                    break;
                case NOTIFICATION_OPERATION_NEXT:
                    nextPosition();
                    play();
                    break;
                default:
                    break;
            }
        }
    }

    private void play() {
        try {
            if (songPlaying != null) {
                songPlaying.setPause(false);
                songPlaying.setPlay(false);
            }
            songPlaying = mPlayList.get(mPosition);
            songPlaying.setPlay(true);
            songPlaying.setPause(false);
            pause = false;
            mPlayer.reset();
            mPlayer.setDataSource(songPlaying.getPath());
            mPlayer.prepare();
            mPlayer.start();
            Intent intent = new Intent(PLAY_STATE_CHANGE);
            intent.putExtra(PLAY_STATE_KEY, PLAY_STATE_PLAY);
            sendBroadcast(intent);
            notification.push(songPlaying);
        } catch (IOException e) {
            Log.e(TAG, "fail to set data source of player", e);
        }
    }

    private void nextPosition() {
        previousPosition = mPosition;
        if (shuffle) {
            mPosition = (int) Math.round(Math.random() * (mPlayList.size() - 1));
        } else {
            mPosition++;
            if (mPosition >= mPlayList.size()) {
                mPosition = 0;
            }
        }
    }

    private void previousPosition() {
        if (mPlayer != null) {
            float max = 100;
            float progressInPercent = ((float)mPlayer.getCurrentPosition())
                    / ((float)mPlayer.getDuration());
            if (max * progressInPercent < 10) {
                if (previousPosition != -1) {
                    mPosition = previousPosition;
                    previousPosition = mPosition - 1;
                }
            }
        }
    }
}
