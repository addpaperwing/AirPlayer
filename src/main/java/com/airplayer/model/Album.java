package com.airplayer.model;

import java.io.Serializable;

/**
 * Created by ZiyiTsang on 15/6/10.
 */
public class Album implements Serializable{

    private String title;
    private String albumArt;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }
}
