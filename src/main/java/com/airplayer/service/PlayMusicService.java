package com.airplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
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
    public static final String START_TO_PLAY_A_NEW_SONG = "com.airplayer.START_TO_PLAY_A_NEW_SONG";

    // play mode
    public static final int SINGLE_REPEAT_MODE = 0;
    public static final int ACCORDING_TO_PRIORITY_MODE = 1;
    public static final int SHUFFLE_MODE = 2;

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
                    case SINGLE_REPEAT_MODE:
                        play(mPlayList.get(mPosition));
                        Log.d(TAG, "on completion, and play mode is " + mPlayMode);
                        break;
                    case ACCORDING_TO_PRIORITY_MODE:
//                        if (mPosition > mPlayList.size()) {
//                            mPosition = 0;
//                        } else {
//                            mPosition++;
//                        }
//                        play(mPlayList.get(mPosition));
                        mBinder.next();
                        Log.d(TAG, "on completion, and play mode is " + mPlayMode);
                        break;
                    case SHUFFLE_MODE:
                        int random = (int) Math.round(Math.random() * (mPlayList.size() - 1));
                        play(mPlayList.get(random));

                }
            }
        });
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
            mPlayList = playList;
            mOrderList = playList;
            play(mPlayList.get(mPosition));
        }

        public void resumeMusic() {
            if (mPlayer != null) {
                mPlayer.start();
                isPause = false;
            }
        }

        public void pauseMusic() {
            if (mPlayer != null) {
                mPlayer.pause();
                isPause = true;
                Log.d(TAG, "player is paused");
            }
        }

        public void previous() {
            if (mPlayer != null) {
                float max = 100;
                if (max * getProgress() > 10) {
                    setProgress(0);
                } else {
                    playMusic(--mPosition, mPlayList);
                }
            }
        }

        public void next() {
            if (mPosition > mPlayList.size()) {
                mPosition = 0;
            } else {
                mPosition++;
            }
            play(mPlayList.get(mPosition));
        }

        public float getProgress() {
            return ((float)mPlayer.getCurrentPosition()) / ((float)mPlayer.getDuration());
        }

        public void setProgress(double p) {
            mPlayer.seekTo((int) (mPlayer.getDuration() * p));
        }

        public void switchLoopMode(boolean isLoop) {
            if (isLoop) {
                mPlayMode = SINGLE_REPEAT_MODE;
            } else {
                mPlayMode = ACCORDING_TO_PRIORITY_MODE;
            }
            Log.d(TAG, "switch loop succeed, now play mode is " + mPlayMode);
        }

        public void switchShuffleMode(boolean isShuffle) {
            if (isShuffle) {
                mPlayMode = SHUFFLE_MODE;
            } else {
                mPlayMode = ACCORDING_TO_PRIORITY_MODE;
            }
            Log.d(TAG, "switch shuffle succeed, now play mode is " + mPlayMode);
        }

//        public void switchShuffleList(boolean shuffle) {
//            if (shuffle) {
//                List<Song> shuffleList = new ArrayList<>();
//                if (songPlaying != null) {
//                    shuffleList.add(songPlaying);
//
//                }
//                Log.d(TAG, mPlayList.size() + "");
//                do {
//                    int random = (int) Math.round(Math.random() * (mPlayList.size() - 1));
//                    Song song = mPlayList.get(random);
//                    shuffleList.add(song);
//                    mPlayList.remove(random);
//                } while (mPlayList.size() > 0);
//                isListShuffled = true;
//                Log.d(TAG, mPlayList.size() + "");
//                mPlayList = shuffleList;
//                Log.d(TAG, mPlayList.size() + "");
//                Log.d(TAG, "play list has been shuffled, play mode is " + mPlayMode);
//            } else {
//                mPlayList = mOrderList;
//                Log.d(TAG, mPlayList.size() + "");
//                isListShuffled = false;
//                Log.d(TAG, "play list has been set back to ordered list, play mode is " + mPlayMode);
//            }
//        }

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
            return mPlayer.isPlaying();
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
            Intent intent = new Intent(START_TO_PLAY_A_NEW_SONG);
            sendBroadcast(intent);
        } catch (IOException e) {
            Log.e(TAG, "fail to set data source of player", e);
        }
    }
}
