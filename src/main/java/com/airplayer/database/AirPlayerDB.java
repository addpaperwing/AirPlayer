package com.airplayer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.airplayer.model.Music;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/7.
 */
public class AirPlayerDB {

    public static final String DB_NAME = "air_player.db";

    public static final int VERSION = 1;

    public static final String TABLE_NAME = "air_music";

    public static final String TITLE = "title";
    public static final String ALBUM = "album";
    public static final String ARTIST = "artist";
    public static final String DURATION = "duration";
    public static final String PATH = "path";
    public static final String ALBUM_ART = "album_art";
    public static final String ARTIST_IMAGE = "artist_image";

    public static AirPlayerDB airPlayerDB;

    private SQLiteDatabase db;

    private AirPlayerDB(Context context) {
        AirPlayerOpenHelper helper = new AirPlayerOpenHelper(context, DB_NAME, null, VERSION);
        db = helper.getWritableDatabase();
    }

    public synchronized static AirPlayerDB newInstance(Context context) {
        if (airPlayerDB == null) {
            airPlayerDB = new AirPlayerDB(context);
        }
        return airPlayerDB;
    }

    public void saveMusicInfo(Music music) {
        if (music != null) {
            ContentValues values = new ContentValues();
            values.put(TITLE, music.getTitle());
            values.put(ALBUM, music.getAlbum());
            values.put(ARTIST, music.getArtist());
            values.put(DURATION, music.getDuration());
            values.put(PATH, music.getPath());
            values.put(ALBUM_ART, music.getAlbumArt());
            db.insert(TABLE_NAME, null, values);
        }
    }

    public List<Music> loadList (String[] projection, String selection, String[] selectionArgs,
                                int listType) {
        List<Music> list = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String artist;
                    String album;
                    switch (listType) {
                        case 0:
                            artist = cursor.getString(0);
                            if (!checkIfListHas(artist, list, true)) {
                                Music music = new Music();
                                music.setArtist(cursor.getString(0));
                                music.setArtistImage(cursor.getString(1));
                                list.add(music);
                            }
                            break;
                        case 1:
                            album = cursor.getString(0);
                            if (!checkIfListHas(album, list, false)) {
                                Music music = new Music();
                                music.setAlbum(cursor.getString(0));
                                music.setAlbumArt(cursor.getString(1));
                                list.add(music);
                            }
                            break;
                        case 2:
                            Music music = new Music();
                            music.setTitle(cursor.getString(0));
                            music.setAlbum(cursor.getString(1));
                            music.setArtist(cursor.getString(2));
                            music.setDuration(cursor.getInt(3));
                            music.setPath(cursor.getString(4));
                            music.setAlbumArt(cursor.getString(5));
                            list.add(music);
                            break;
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }

    private boolean checkIfListHas(String checkParam, List<Music> checkTag, boolean checkArtist) {
        for (int i = 0; i < checkTag.size(); i++) {
            if (checkArtist) {
                if (checkParam.equals(checkTag.get(i).getArtist())) {
                    return true;
                }
            } else {
                if (checkParam.equals(checkTag.get(i).getAlbum())) {
                    return true;
                }
            }
        }
        return false;
    }
}
