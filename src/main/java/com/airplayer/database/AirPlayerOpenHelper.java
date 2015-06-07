package com.airplayer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ZiyiTsang on 15/6/7.
 */
public class AirPlayerOpenHelper extends SQLiteOpenHelper {

    public static final String CREATE_TABLE = "create table air_music ("
            + "id integer primary key autoincrement, "
            + "title text, "
            + "album text, "
            + "artist text, "
            + "duration integer, "
            + "path text, "
            + "album_art text, "
            + "artist_image text)";

    public AirPlayerOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
