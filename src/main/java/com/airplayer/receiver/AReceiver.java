package com.airplayer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.airplayer.service.PlayMusicService;

/**
 * Created by ZiyiTsang on 15/6/11.
 */
public class AReceiver extends BroadcastReceiver {

    public static final String START_TO_PLAY_MUSIC = "com.airplayer.START_TO_PLAY_MUSIC";

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
