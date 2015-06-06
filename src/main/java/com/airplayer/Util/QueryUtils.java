package com.airplayer.Util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import com.airplayer.model.Album;
import com.airplayer.model.Artist;
import com.airplayer.model.Song;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/5.
 */
public class QueryUtils {

    public static List<Artist> queryArtists(Context context) {
        List<Artist> artists = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                new String[] {
                        MediaStore.Audio.Artists._ID,
                        MediaStore.Audio.Artists.ARTIST
                },
                null, null, MediaStore.Audio.Artists.ARTIST);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(0);
                    String name = cursor.getString(1);
                    Artist artist = new Artist();
                    artist.setId(id);
                    artist.setName(name);
                    artists.add(artist);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return artists;
    }

    public static List<Album> queryArtistAlbums(Context context, int artistId) {
        List<Album> albums = new ArrayList<>();
        boolean have = false;
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {
                        MediaStore.Audio.Media.ALBUM_ID,
                        MediaStore.Audio.Media.ALBUM,
                },
                "artist_id = ?",
                new String[]{ String.valueOf(artistId) },
                MediaStore.Audio.Media.YEAR);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int albumId = cursor.getInt(0);
                    String albumTitle = cursor.getString(1);
                    for (int i = 0; i < albums.size(); i++) {
                        if (albumId == albums.get(i).getId()) {
                            have = true;
                        }
                    }
                    if (!have) {
                        Album album = new Album();
                        album.setId(albumId);
                        album.setTitle(albumTitle);
                        album.setArtPath(getAlbumArtPath(context, albumId));
                        albums.add(album);
                    }
                    have = false;
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return albums;
    }

    public static List<Album> queryAlbums(Context context) {
        List<Album> albums = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[] {
                        MediaStore.Audio.Albums._ID,
                        MediaStore.Audio.Albums.ALBUM,
                        MediaStore.Audio.Albums.ALBUM_ART
                },
                null, null, MediaStore.Audio.Albums.ALBUM);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Album album = new Album();
                    int id = cursor.getInt(0);
                    String title = cursor.getString(1);
                    String artPath = cursor.getString(2);
                    album.setId(id);
                    album.setTitle(title);
                    album.setArtPath(artPath);
                    albums.add(album);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return albums;
    }

    public static List<Song> querySongs(Context context) {
        List<Song> songs = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {
                        MediaStore.Audio.Media.IS_MUSIC,
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ALBUM_ID,
                        MediaStore.Audio.Media.ARTIST_ID,
                },
                null, null, MediaStore.Audio.Media.TITLE);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    if (cursor.getInt(0) == 1) {
                        Song song = new Song();
                        song.setId(cursor.getInt(1));
                        song.setTitle(cursor.getString(2));
                        song.setAlbum(cursor.getString(3));
                        song.setArtist(cursor.getString(4));
                        song.setSongPath(cursor.getString(5));

                        int albumId = cursor.getInt(6);
                        song.setAlbumId(albumId);

                        song.setArtistId(cursor.getInt(7));
//                        song.setArtPath(getAlbumArtPath(context, albumId));
                        songs.add(song);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return songs;
    }

    private static String getAlbumArtPath(Context context, int albumId) {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Albums.ALBUM_ART },
                "_id = ?",
                new String[]{String.valueOf(albumId)},
                null);
        if (cursor != null) {
            cursor.moveToFirst();
            String path = cursor.getString(0);
            cursor.close();
            return path;
        } else {
            return "";
        }
    }

}