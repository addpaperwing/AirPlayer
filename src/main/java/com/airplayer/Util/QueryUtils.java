package com.airplayer.util;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.provider.MediaStore;
import android.util.Log;

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
                        MediaStore.Audio.Media.TRACK,       /* for displaying in album fragment */
                        MediaStore.Audio.Media.YEAR,        /* for displaying in header of Song list fragment */
                        MediaStore.Audio.Media.ALBUM_ID     /* for getting the album art*/
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
                        song.setYear(cursor.getInt(8));
                        song.setAlbumId(cursor.getInt(9));
                        list.add(song);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }

    public static List<Album> loadRecentAlbum(Context context) {
        List<Album> list = new ArrayList<>();
        String[] recentAlbums = new String[] { "leov", "leov", "leov", "leov", "leov", "leov" };
        int p = 0;
        Cursor cursor1 = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {
                        MediaStore.Audio.Media.IS_MUSIC,
                        MediaStore.Audio.Media.ALBUM,
                },
                null, null, MediaStore.Audio.Media.DATE_ADDED + " desc");
        if (cursor1 != null) {
            if (cursor1.moveToFirst()) {
                do {
                    if (cursor1.getInt(0) == 1) {
                        String albumName = cursor1.getString(1);
                        boolean have = false;
                        for (int i = 0; i < 6; i++) {
                            if (recentAlbums[i].equals(albumName)) {
                                have = true;
                            }
                        }
                        if (!have) {
                            recentAlbums[p] = albumName;
                            p++;
                        }
                        if (p > 5) break;
                    }
                } while (cursor1.moveToNext());
            }
            cursor1.close();
            Log.d("test", recentAlbums[0] + ", " + recentAlbums[1] + ", " + recentAlbums[2] + ", "
                    + recentAlbums[3] + ", " + recentAlbums[4] + ", " + recentAlbums[5]);
        }
        for (int i = 0; i < 6; i++) {
            if (recentAlbums[i].equals("")) {

            }
        }
        for (int i = 0; i < 6; i++) {
            Cursor cursor2 = context.getContentResolver().query(
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    new String[]{
                            MediaStore.Audio.Albums._ID,
                            MediaStore.Audio.Albums.ARTIST,
                            MediaStore.Audio.Albums.LAST_YEAR,
                            MediaStore.Audio.Albums.ALBUM_ART,
                            MediaStore.Audio.Albums.NUMBER_OF_SONGS
                    },
                    "album = ?", new String[]{ recentAlbums[i] }, null
            );
            if (cursor2 != null) {
                try {
                    cursor2.moveToFirst();
                    Album album = new Album();
                    album.setTitle(recentAlbums[i]);
                    album.setId(cursor2.getInt(0));
                    album.setAlbumArtist(cursor2.getString(1));
                    album.setYear(cursor2.getString(2));
                    album.setAlbumArtPath(cursor2.getString(3));
                    album.setSongsHave(cursor2.getInt(4));
                    list.add(album);
                } catch (CursorIndexOutOfBoundsException e) {
                    Log.d("QueryUtils", "index out of bounds");
                }
                cursor2.close();
            }
        }
        return list;
    }
}