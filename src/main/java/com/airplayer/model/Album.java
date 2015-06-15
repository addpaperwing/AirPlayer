package com.airplayer.model;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by ZiyiTsang on 15/6/10.
 */
public class Album implements Serializable{

    private int id;
    private String title;
    private String albumArt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public Uri getAlbumArtUri() {
        return Uri.parse("content://media/external/audio/albumart/" + id);
    }
}
