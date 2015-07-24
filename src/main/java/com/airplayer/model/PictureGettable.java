package com.airplayer.model;

import java.io.Serializable;

/**
 * Created by ZiyiTsang on 15/7/17.
 */
public interface PictureGettable extends Serializable {

    int REQUEST_CODE_FETCH_PICTURE = 1;

    String getQueryKeyword();
    String getSaveName();
    void setPictureDownloaded(boolean b);
    String getPicturePath();
}
