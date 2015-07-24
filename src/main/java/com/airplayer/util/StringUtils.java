package com.airplayer.util;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * Created by ZiyiTsang on 15/6/23.
 */
public class StringUtils {

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

    public static String encodeKeyword(String keyword) {
        try {
            keyword = URLEncoder.encode(keyword, Charset.defaultCharset().name());
        } catch (UnsupportedEncodingException e) {
            Log.e("StringUtil", "Unsupported Encoding!", e);
        }
        return keyword;
    }

    public static String getPureFilename(String saveName) {
        String[] strings = new String[] { ",", "/", "?", "*", "<", ">", ":", "|" };
        for (String str : strings) {
            if (saveName.contains(str)) {
                saveName = saveName.replace(str, "");
            }
        }
        return saveName;
    }
}
