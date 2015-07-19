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
 * <br>Service to control play music actions</br>
 */
public class PlayMusicService extends Service {

    public static final String TAG = "PlayMusicService";

    // broadcast action and extra keys - value
    /**
     * <br>Broadcast intent action</br>
     */
    public static final String PLAY_STATE_CHANGE = "com.airplayer.PLAY_STATE_CHANGE";

    /**
     * <br>Broadcast intent action</br>
     */
    public static final String PLAY_STATE_KEY = "com.airplayer.PLAY_STATE_CHANGE.PLAY_STATE_KEY";

    /**
     * <br>Broadcast intent action</br>
     */
    public static final String NOTIFICATION_OPERATION = "com.airplayer.NOTIFICATION_OPERATION";

    /**
     * <br>Broadcast intent action</br>
     */
    public static final String NOTIFICATION_OPERATION_KEY = "com.airplayer.NOTIFICATION_OPERATION_KEY";

    /**
     * <br>Play state value</br>
     */
    public static final int PLAY_STATE_PLAY = 0;

    /**
     * <br>Play state value</br>
     */
    public static final int PLAY_STATE_PAUSE = 1;

    /**
     * <br>Notification operation broadcast intent extra</br>
     */
    public static final int NOTIFICATION_OPERATION_PREVIOUS = 5;
    public static final int NOTIFICATION_OPERATION_PLAY_PAUSE = 0;
    public static final int NOTIFICATION_OPERATION_NEXT = 2;

    /**
     * <br>Play mode value</br>
     */
    public static final int PLAY_LIST_MODE = 0;
    public static final int LOOP_LIST_MODE = 1;
    public static final int SINGLE_SONG_LOOP_MODE = 2;

    /**
     * <br>Binder</br>
     */
    private PlayerControlBinder mBinder = new PlayerControlBinder();

    /**
     * <br>Media player</br>
     */
    private MediaPlayer mPlayer;

    /**
     * <br>Play list</br>
     */
    private List<Song> mPlayList = new ArrayList<>();

    /**
     * <br>Position of song playing in { @see mPlayList }</br>
     */
    private int mPosition;

    /**
     * <br>Position  of last song played in { @see mPlayList }</br>
     */
    private int lastPosition;

    /**
     * <br>Song instance of playing song</br>
     */
    private Song songPlaying;

    /**
     * <br>Play Mode</br>
     */
    private int mPlayMode = PLAY_LIST_MODE;

    /**
     * <br>Value to control pause actions</br>
     */
    private boolean pause = false;

    /**
     * <br>Value to control shuffle play mode</br>
     */
    private boolean shuffle = false;

    /**
     * <br>Notification { @see AirNotification }</br>
     */
    private AirNotification notification;

    /**
     * <br>Receiver for notification operation</br>
     */
    private PlayMusicReceiver playMusicReceiver;

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
                        if (mPosition == 0 && mPlayMode == PLAY_LIST_MODE) {
                            play();
                            mBinder.pauseMusic();
                            return;
                        }
                    }
                }
                play();
            }
        });
        playMusicReceiver = new PlayMusicReceiver();
        IntentFilter filter = new IntentFilter(NOTIFICATION_OPERATION);
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(playMusicReceiver, filter);
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
        unregisterReceiver(playMusicReceiver);
        mPlayer.release();
        notification.cancel();
        super.onDestroy();
        Log.d(TAG, "service destroy");
    }

    public class PlayerControlBinder extends Binder {

        public void playMusic(int position, List<Song> playList) {
            mPosition = position;
            mPlayList = playList;
            lastPosition = mPosition - 1;
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
            if (mPlayer != null && songPlaying != null && !pause) {
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
        lastPosition = mPosition;
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
                if (lastPosition != -1) {
                    mPosition = lastPosition;
                    lastPosition = mPosition - 1;
                }
            }
        }
    }

    /**
     * <br>Receive broadcast sent when user operating at notification</br>
     */
    private class PlayMusicReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case NOTIFICATION_OPERATION:
                    onNotificationActionsReceive(intent);
                    break;
                case Intent.ACTION_HEADSET_PLUG:
                    onHeadsetPlugActionReceive(intent);
                    break;
            }
        }
    }

    private void onNotificationActionsReceive(Intent intent) {
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

    private void onHeadsetPlugActionReceive(Intent intent) {
        if (intent.getIntExtra("state", -1) == 0) {
            mBinder.pauseMusic();
            Log.d(TAG, "Headset unplug");
        }
    }
}
