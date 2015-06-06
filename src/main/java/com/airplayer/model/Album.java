package com.airplayer.model;

/**
 * Created by ZiyiTsang on 15/6/4.
 */
public class Album {
    private int id;
    private String title;
    private String artPath;

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

    public String getArtPath() {
        return artPath;
    }

    public void setArtPath(String artPath) {
        this.artPath = artPath;
    }

    //    private String artist;
//    private Uri artUri;
//
////    public String getArtist() {
////        return artist;
////    }
//
////    public void setArtist(String artist) {
////        this.artist = artist;
////    }
//
//    public Uri getArtUri() {
//        return artUri;
//    }
//
//    public void setArtUri(Uri artUri) {
//        this.artUri = artUri;
//    }
//
    public String toString() {
        return title;
    }
}
