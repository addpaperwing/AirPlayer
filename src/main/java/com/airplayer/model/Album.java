package com.airplayer.model;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by ZiyiTsang on 15/6/10.
 */
public class Album implements Serializable{

    private int id;
    private String title;
    private String albumArtist;
    private String year;
    private String albumArtPath;

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

    public String getAlbumArtist() {
        return albumArtist;
    }

    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getAlbumArtPath() {
        return albumArtPath;
    }

    public void setAlbumArtPath(String albumArtPath) {
        this.albumArtPath = albumArtPath;
    }

    public Uri getAlbumArtUri() {
        return Uri.parse("content://media/external/audio/albumart/" + id);
    }
}
