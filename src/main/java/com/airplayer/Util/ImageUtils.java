package com.airplayer.Util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by ZiyiTsang on 15/6/6.
 */
public class ImageUtils {

    public static Bitmap getAlbumArt(String artPath){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        options.inJustDecodeBounds = false;
        options.inDither = false;
        try {
            return BitmapFactory.decodeFile(artPath, options);
        } catch (NullPointerException e) {
            return null;
        }
//        return null;
    }
}
