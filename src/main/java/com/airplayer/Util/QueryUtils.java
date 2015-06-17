package com.airplayer.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.airplayer.model.Album;
import com.airplayer.model.Artist;
import com.airplayer.model.Song;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ZiyiTsang on 15/6/5.
 */
public class QueryUtils {


    public static String getAlbumArtPath(Context context, String albumTitle) {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{ MediaStore.Audio.Albums.ALBUM_ART },
                "album = ?", new String[]{ albumTitle },
                null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String path = cursor.getString(0);
                cursor.close();
                return path;
            }
            return "";
        } else {
            return "";
        }
    }

    public static List<Artist> loadArtistList(Context context) {
        List<Artist> list = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Artists.ARTIST},
                null, null, MediaStore.Audio.Artists.ARTIST);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Artist artist = new Artist();
                    artist.setName(cursor.getString(0));
                    list.add(artist);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }

    public static List<Album> loadAlbumList(Context context, String selection, String[] selectionArgs, String sortOrder) {
        List<Album> list = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaStore.Audio.Albums.ALBUM,
                        MediaStore.Audio.Albums._ID,
                        MediaStore.Audio.Albums.ARTIST,
                        MediaStore.Audio.Albums.LAST_YEAR,
                        MediaStore.Audio.Albums.ALBUM_ART
                },
                selection, selectionArgs, sortOrder);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Album album = new Album();
                    album.setTitle(cursor.getString(0));
                    album.setId(cursor.getInt(1));
                    album.setAlbumArtist(cursor.getString(2));
                    album.setYear(cursor.getString(3));
                    album.setAlbumArtPath(cursor.getString(4));
                    list.add(album);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }

    public static List<Song> loadSongList(Context context, String selection, String[] selectionArgs, String sortOrder) {
        List<Song> list = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaStore.Audio.Media.IS_MUSIC,    /* for checking if is music*/
                        MediaStore.Audio.Media._ID,         /* */
                        MediaStore.Audio.Media.TITLE,       /* for displaying */
                        MediaStore.Audio.Media.ALBUM,       /* for displaying */
                        MediaStore.Audio.Media.ARTIST,      /* for displaying */
                        MediaStore.Audio.Media.DURATION,    /* for displaying */
                        MediaStore.Audio.Media.DATA,        /* for playing */
                        MediaStore.Audio.Media.TRACK        /* for displaying in album fragment */
                },
                selection, selectionArgs, sortOrder);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    if (cursor.getInt(0) == 1) {
                        Song song = new Song();
                        song.setId(cursor.getInt(1));
                        song.setTitle(cursor.getString(2));
                        song.setAlbum(cursor.getString(3));
                        song.setArtist(cursor.getString(4));
                        song.setDuration(cursor.getInt(5));
                        song.setPath(cursor.getString(6));
                        song.setTrack(cursor.getInt(7));
                        list.add(song);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }
}