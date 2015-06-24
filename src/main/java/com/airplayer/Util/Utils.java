package com.airplayer.util;

/**
 * Created by ZiyiTsang on 15/6/23.
 */
public class Utils {

    public static String getFormatTime(int duration) {
        int min = 0;
        int sec;

        sec = duration / 1000;
        if(sec > 60){
            min = sec / 60;
            sec = sec % 60;
        }
        return String.format("%02d:%02d", min, sec);
    }
}
