package com.airplayer.model;

import android.net.Uri;


/**
 * Created by ZiyiTsang on 15/6/4.
 */
public class Artist {
    private int id;
    private String name;
    private Uri artistImage;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getArtistImage() {
        return artistImage;
    }

    public void setArtistImage(Uri artistImage) {
        this.artistImage = artistImage;
    }

    public String toString() {
        return name;
    }
}
