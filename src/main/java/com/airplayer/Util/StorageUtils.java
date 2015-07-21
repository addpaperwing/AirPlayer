package com.airplayer.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ZiyiTsang on 15/7/8.
 */
public class StorageUtils {

    public static File saveImage(Context context, String fileName, Bitmap bm) throws IOException, RuntimeException {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            String storageDirPath = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES).getPath();
            File dir = new File(storageDirPath + "/AirPlayer");
            if (!dir.exists()) {
                boolean check1 = dir.mkdirs();
                if (check1) {
                    Log.d("StorageUtils", "Folder not found, created new");
                }
            }
            File file = new File(dir.getPath() + "/" + fileName);
            if (file.exists()) {
                boolean check2 = file.delete();
                if (check2) {
                    Log.d("StorageUtils", "File exists, deleted");
                }
            }
            boolean check3 = file.createNewFile();
            if (check3) {
                Log.d("StorageUtils", "New file created succeed");
            }

            saveBitmap(context, file, bm);
            return file;
        } else {
            throw new RuntimeException("External Storage unavailable");
        }
    }

    public static File saveImage(Context context, String fileName, String urlSpec) throws IOException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlSpec);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            byte[] bitmapBytes = out.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            return saveImage(context, fileName, bitmap);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static void saveBitmap(final Context context, final File file, final Bitmap bitmap) throws IOException, NullPointerException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, bos);
            bos.flush();
            bos.close();
            Log.d("StorageUtils.saveBitmap", "Success to save bitmap to " + file.getPath());
            // send a scan broadcast to update image library
            sendScanBroadcast(context, file);
        } else {
            throw new NullPointerException();
        }
    }

    public static void sendScanBroadcast(Context context, File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }
}
