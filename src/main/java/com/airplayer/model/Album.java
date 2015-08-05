package com.airplayer.model;

import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.airplayer.util.StringUtils;

/**
 * Created by ZiyiTsang on 15/6/10.
 */
public class Album extends AirModel implements Comparable<Album>, PictureGettable {

    /**
     * control
     */
    private int freq;

    public void freqAddOne() {
        if (freq == 0) {
            freq = sSp.getInt(id + "freq", 0);
        }
        sSp.edit().putInt(id + "freq", freq + 1).apply();
    }

    public int getFreq() {
        if (freq == 0) {
            freq = sSp.getInt(id + "freq", 0);
        }
        return freq;
    }


    /**
     * display
     */
    private int id;
    private String title;
    private String albumArtist;
    private int year;
    private String albumArtPath;
    private Artist artist;

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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public String getAlbumArtPath() {
        if (albumArtPath == null) {
            return "";
        }
        boolean b = sSp.getBoolean(id + "", false);
        if (!b) {
            return albumArtPath;
        } else {
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    + "/AirPlayer/"
                    + getSaveName() + ".jpg";
        }
    }

    public void setAlbumArtPath(String albumArtPath) {
        this.albumArtPath = albumArtPath;
    }

    public Uri getAlbumArtUri() {
        return Uri.parse("file://" + Uri.decode(getAlbumArtPath()));
    }


    /**
     * Implements { @link com.airplayer.model.PictureGettable }
     */

    @Override
    public String getQueryKeyword() {
        return title;
    }

    @Override
    public String getSaveName() {
        return StringUtils.getPureFilename(albumArtist) + " - " + StringUtils.getPureFilename(title);
    }

    @Override
    public void setPictureDownloaded(boolean b) {
        sSp.edit().putBoolean(id + "", b).apply();
    }

    @Override
    public String getPicturePath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/AirPlayer/"
                + getSaveName() + ".jpg";
    }

    /**
     * override compareTo method to sort album quickly
     * @param another instance to compare with
     * @return positive or 0 or negative
     */
    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(Album another) {
        if (another != null) {
            return this.getYear() - another.getYear();
        }
        return -1;
    }
}
