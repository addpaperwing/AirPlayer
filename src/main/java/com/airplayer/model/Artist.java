package com.airplayer.model;

import android.net.Uri;
import android.os.Environment;

import com.airplayer.util.StringUtils;

/**
 * Created by ZiyiTsang on 15/6/10.
 */
public class Artist extends AirModel implements PictureGettable, Comparable<Artist> {

    private int id;

    private String name;

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

    public String getArtistPicturePath() {
        if (sSp.getBoolean(name + id, false)) {
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    + "/AirPlayer/"
                    + getSaveName() + ".jpg";
        } else {
            return "";
        }

    }

    public Uri getArtistPictureUri() {
        if (sSp.getBoolean(name + id, false)) {
            return Uri.parse("file://" + Uri.decode(getArtistPicturePath()));
        } else {
            return null;
        }
    }

    @Override
    public String getQueryKeyword() {
        return name;
    }

    @Override
    public String getSaveName() {
        return StringUtils.getPureFilename(name);
    }

    @Override
    public void setPictureDownloaded(boolean b) {
        sSp.edit().putBoolean(name + id, b).apply();
    }

    @Override
    public String getPicturePath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/AirPlayer/"
                + getSaveName() + ".jpg";
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(Artist another) {
        if (another != null) {
            return getName().compareTo(another.getName());
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Artist) {
            Artist target = (Artist) o;
            return getName().equals(target.getName()) && getId() == target.getId();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Integer.getInteger(getName().hashCode() + "" + getId());
    }
}
