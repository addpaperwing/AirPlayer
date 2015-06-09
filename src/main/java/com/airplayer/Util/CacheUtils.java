package com.airplayer.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import cn.trinea.android.common.entity.CacheObject;
import cn.trinea.android.common.entity.FailedReason;
import cn.trinea.android.common.service.impl.ImageCache;
import cn.trinea.android.common.service.impl.ImageSDCardCache;
import cn.trinea.android.common.service.impl.PreloadDataCache.OnGetDataListener;
import cn.trinea.android.common.util.CacheManager;
import cn.trinea.android.common.util.FileUtils;

/**
 * Created by ZiyiTsang on 15/6/8.
 */
public class CacheUtils {

    public static final String TAG_CACHE = "image_cache";


    private static final ImageCache sImageCache = CacheManager.getImageCache();

    static {
        sImageCache.setOnGetDataListener(new OnGetDataListener<String, Bitmap>() {
            @Override
            public CacheObject<Bitmap> onGetData(String imagePath) {
                if (!FileUtils.isFileExist(imagePath)) {
                    return null;
                }
                Bitmap bm = BitmapFactory.decodeFile(imagePath);
                return (bm == null ? null : new CacheObject<Bitmap>(bm));
            }
        });
    }

    public CacheUtils(Context context) {
        sImageCache.initData(context, TAG_CACHE);

    }
}
