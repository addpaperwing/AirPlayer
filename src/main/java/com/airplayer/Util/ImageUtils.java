package com.airplayer.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.util.Log;
import android.view.Display;

import com.airplayer.R;

import java.io.FileNotFoundException;

/**
 * Created by ZiyiTsang on 15/6/6.
 */
public class ImageUtils {

    public static Bitmap getWindowWideBitmap(Activity activity, String artPath) {
        if (artPath != null) {
            try {
                Display display = activity.getWindowManager().getDefaultDisplay();
                Point point = new Point();
                display.getSize(point);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                options.inJustDecodeBounds = false;
                options.inDither = false;
                Bitmap bm;
                if (artPath.equals("")) {
                    bm = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_default_art, options);
                } else {
                    bm = BitmapFactory.decodeFile(artPath, options);
                }

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

    public static Bitmap getBitmap(Context context, String path) {
        if (path != null) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                options.inJustDecodeBounds = false;
                options.inDither = false;

                Bitmap bm;
                if (path.equals("")) {
                    bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_default_art, options);
                } else {
                    bm = BitmapFactory.decodeFile(path, options);
                }

                return bm;
            } catch (NullPointerException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public static Bitmap getBitmap(Context context, Uri uri) {
        Bitmap bm = null;
        if (uri != null) {
            try {
                bm = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return bm;
    }
}
