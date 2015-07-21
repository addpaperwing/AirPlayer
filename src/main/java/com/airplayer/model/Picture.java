package com.airplayer.model;

/**
 * Created by ZiyiTsang on 15/7/21.
 */
public class Picture {

    String thumbUrl;
    String objUrl;

    public Picture(String thumbUrl, String objUrl) {
        this.thumbUrl = thumbUrl;
        this.objUrl = objUrl;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public String getObjUrl() {
        return objUrl;
    }
}
