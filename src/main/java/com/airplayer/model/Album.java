package com.airplayer.model;

import android.net.Uri;
import android.os.Environment;

/**
 * Created by ZiyiTsang on 15/6/10.
 */
public class Album extends AirModel implements Comparable<Album>, PictureGettable {

    private int id;
    private String title;
    private String albumArtist;
    private int year;
    private String albumArtPath;
    private int songsCount;


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

    public int getSongsCount() {
        return songsCount;
    }

    public void setSongsCount(int songsCount) {
        this.songsCount = songsCount;
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

    @Override
    public String getSearchKeyword() {
        return title;
    }

    @Override
    public String getSaveName() {
        if ((albumArtist + " - " + title).contains("/")) {
            String[] names = (albumArtist + " - " + title).split("/");
            StringBuilder builder = new StringBuilder();
            for (String str : names) {
                builder.append(str);
            }
            return builder.toString();
        } else {
            return albumArtist + " - " + title;
        }
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

    @Override
    public int compareTo(Album another) {
        return this.getYear() - another.getYear();
    }
}
