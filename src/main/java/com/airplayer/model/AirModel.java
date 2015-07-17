package com.airplayer.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ZiyiTsang on 15/7/17.
 */
public class AirModel {

    protected static SharedPreferences sSp;

    public static void initModels(Context context) {
        sSp = PreferenceManager.getDefaultSharedPreferences(context);
    }
}
