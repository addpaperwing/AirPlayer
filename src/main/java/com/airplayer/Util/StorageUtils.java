package com.airplayer.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ZiyiTsang on 15/7/8.
 */
public class StorageUtils {

    public static File savePhoto(Context context, String path, String fileName, Bitmap bm) {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            String storageDirPath = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES).getPath();
            File dir = new File(storageDirPath + path);
            Log.d("tag", dir.getPath());
            if (!dir.exists()) {
                boolean check1 = dir.mkdir();
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
            try {
                boolean check3 = file.createNewFile();
                if (check3) {
                    Log.d("StorageUtils", "New file created succeed");
                }

                saveBitmap(context, file, bm);
                return file;
            } catch (IOException e) {
                Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return null;
            }
        } else {
            Toast.makeText(context, "External Storage unavailable", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private static void saveBitmap(final Context context, final File file, final Bitmap bitmap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 30, bos);
                    bos.flush();
                    bos.close();
                    Log.d("StorageUtils.saveBitmap", "Success to save bitmap");
                    // send a scan broadcast to update image library
                    sendScanBroadcast(context, file);
                } catch (IOException e) {
                    Log.d("StorageUtils.saveBitmap", "Fail to save bitmap");
                }
            }
        }).start();

    }

    public static void sendScanBroadcast(Context context, File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }
}
