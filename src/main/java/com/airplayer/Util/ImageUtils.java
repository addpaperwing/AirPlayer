package com.airplayer.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;

import java.io.FileNotFoundException;

/**
 * Created by ZiyiTsang on 15/6/6.
 */
public class ImageUtils {

    public static Bitmap getListItemThumbnail (Activity activity, String artPath) {
        if (artPath != null && !(artPath.equals(""))) {
            try {
                Display display = activity.getWindowManager().getDefaultDisplay();
                Point point = new Point();
                display.getSize(point);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                options.inJustDecodeBounds = false;
                options.inDither = false;
                Bitmap bm = BitmapFactory.decodeFile(artPath, options);

                float fWidth = bm.getWidth();
                float fHeight = bm.getHeight();
                Matrix matrix = new Matrix();

                float scaleWidth = point.x/fWidth;
                float scaleHeight = point.x/fHeight;

                matrix.postScale(scaleWidth , scaleHeight);

                int width = (int) fWidth;
                int height = (int) fHeight;

                bm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);

                return bm;
            } catch (NullPointerException e) {
                return null;
            }
        } else {
            return null;
        }
    }
}
