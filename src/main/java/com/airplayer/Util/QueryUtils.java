package com.airplayer.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;


/**
 * Created by ZiyiTsang on 15/6/5.
 */
public class QueryUtils {

    public static String getAlbumArtPath(Context context, int albumId) {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Albums.ALBUM_ART },
                "_id = ?",
                new String[]{String.valueOf(albumId)},
                null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String path = cursor.getString(0);
                cursor.close();
                return path;
            }
            return "";
        } else {
            return "";
        }
    }

}