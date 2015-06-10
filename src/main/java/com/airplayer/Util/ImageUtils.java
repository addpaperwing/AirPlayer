package com.airplayer.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileNotFoundException;

/**
 * Created by ZiyiTsang on 15/6/6.
 */
public class ImageUtils {

    public static Bitmap getListItemThumbnail (String artPath) {
        if (artPath != null && !(artPath.equals(""))) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                options.inJustDecodeBounds = false;
                options.inDither = false;
                return BitmapFactory.decodeFile(artPath, options);
            } catch (NullPointerException e) {
                return null;
            }
        } else {
            return null;
        }
    }
}
