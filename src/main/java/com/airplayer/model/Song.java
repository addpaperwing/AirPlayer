package com.airplayer.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.airplayer.activity.AirMainActivity;
import com.airplayer.util.QueryUtils;

import java.io.Serializable;

/**
 * Created by ZiyiTsang on 15/6/7.
 */
public class Song extends AirModel implements Serializable {

    // control
    private boolean play;
    private boolean pause;

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public boolean isPlay() {
        return play;
    }

    public void setPlay(boolean play) {
        this.play = play;
    }

    // display
    private int id;
    private String title;
    private String album;
    private int albumId;
    private String artist;
    private int duration;
    private String path;
    private int track;

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

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getTrack() {
        return track;
    }

    public void setTrack(int track) {
        this.track = track;
    }

    public Uri getAlbumArtUri() {
        boolean b = sSp.getBoolean(albumId + "", false);
        if (!b) {
            return Uri.parse("content://media/external/audio/albumart/" + albumId);
        } else {
            return Uri.parse("file://"
                    + Uri.decode(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    + "/AirPlayer/"
                    + artist + " - " + album + ".jpg"));
        }
    }

    public String getAlbumArtPath(Context context) {
        boolean b = sSp.getBoolean(albumId + "", false);
        if (!b) {
            return QueryUtils.getAlbumArtPath(context, album);
        } else {
            return AirMainActivity.EXTERNAL_PICTURE_FOLDER + artist + " - " + album + ".jpg";
        }
    }
}
