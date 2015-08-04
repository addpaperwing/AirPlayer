package com.airplayer.model;

import android.content.Context;
import android.provider.MediaStore;

import com.airplayer.util.QueryUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by ZiyiTsang on 15/7/22.
 */
public class AirModelSingleton {

    private static ArrayList<Artist> sArtistArrayList;

    private static ArrayList<Album> sAlbumArrayList;

    private static ArrayList<Song> sSongArrayList;

    private static ArrayList<Album> sRecentAlbumArrayList;

    private static AirModelSingleton singleton;

    private Context context;

    private AirModelSingleton(Context context) {
        this.context = context;
    }

    public static AirModelSingleton getInstance(Context context) {
        if (singleton == null) {
            synchronized (AirModelSingleton.class) {
                if (singleton == null) {
                    singleton = new AirModelSingleton(context);
                }
            }
        }
        return singleton;
    }

    public ArrayList<Artist> getArtistArrayList() {
        if (sArtistArrayList == null) {
            synchronized (AirModelSingleton.class) {
                if (sArtistArrayList == null) {
                    sArtistArrayList = QueryUtils.loadArtistList(context);
                }
            }
        }
        return sArtistArrayList;
    }

    public ArrayList<Artist> getArtists() {
        if (sArtistArrayList == null) {
            if (sSongArrayList == null) {
                getSongArrayList(null, null,  MediaStore.Audio.Media.TITLE);
            }
            boolean have;
            for (int i = 0; i < sSongArrayList.size(); i++) {
                int artistId = sSongArrayList.get(i).getArtistId();
                have = false;
                for (int j = 0; j < sArtistArrayList.size(); j++) {
                    if (artistId == sArtistArrayList.get(j).getId()) {
                        have = true;
                        break;
                    }
                }
                if (!have) {
                    Artist artist = new Artist();
                    artist.setId(artistId);
                    artist.setName(sSongArrayList.get(i).getArtist());
                    sArtistArrayList.add(artist);
                }
            }
            Collections.sort(sArtistArrayList);
        }
        return sArtistArrayList;
    }

    public ArrayList<Album> getAlbumArrayList(String selection, String[] selectionArgs, String sortOrder) {
        if (sAlbumArrayList == null) {
            synchronized (AirModelSingleton.class) {
                if (sAlbumArrayList == null) {
                    sAlbumArrayList = QueryUtils.loadAlbumList(context, selection, selectionArgs, sortOrder);
                }
            }
        }
        return sAlbumArrayList;
    }

    public ArrayList<Album> getAlbums() {
        if (sAlbumArrayList == null) {
            if (sSongArrayList == null) {
                getSongArrayList(null, null,  MediaStore.Audio.Media.TITLE);
            }
            boolean have;
            for (int i = 0; i < sSongArrayList.size(); i++) {
                Song song = sSongArrayList.get(i);
                have = false;
                for (int j = 0; j < sAlbumArrayList.size(); j++) {
                    if (song.getAlbumId() == sAlbumArrayList.get(j).getId()) {
                        have = true;
                        break;
                    }
                }
                if (!have) {
                    Album album = new Album();
                    album.setId(song.getAlbumId());
                    album.setTitle(song.getAlbum());
                    album.setAlbumArtist(song.getArtist());
                    album.setYear(song.getYear());
                    album.setAlbumArtPath("");
                }
            }
            Collections.sort(sAlbumArrayList);
        }
        return sAlbumArrayList;
    }

    public ArrayList<Song> getSongArrayList(String selection, String[] selectionArgs, String sortOrder) {
        if (sSongArrayList == null) {
            synchronized (AirModelSingleton.class) {
                if (sSongArrayList == null) {
                    sSongArrayList = QueryUtils.loadSongList(context, selection, selectionArgs, sortOrder);
                }
            }
        }
        return sSongArrayList;
    }

    public ArrayList<Album> getRecentAlbumArrayList() {
        if (sRecentAlbumArrayList == null) {
            synchronized (AirModelSingleton.class) {
                if (sRecentAlbumArrayList == null) {
                    sRecentAlbumArrayList = QueryUtils.loadRecentAlbum(context);
                }
            }
        }
        return sRecentAlbumArrayList;
    }

    public ArrayList<Album> getArtistAlbum(String artistName) {
        ArrayList<Album> list = new ArrayList<>();
        if (sAlbumArrayList == null) {
            getAlbumArrayList(null, null, MediaStore.Audio.Media.ALBUM);
        }
        for (Album album : sAlbumArrayList) {
            if (album.getAlbumArtist().equals(artistName)) {
                list.add(album);
            }
        }
        Collections.sort(list);
        return list;
    }

    public ArrayList<Song> getAlbumSong(String albumTitle) {
        ArrayList<Song> list = new ArrayList<>();
        if (sSongArrayList == null) {
            getSongArrayList(null, null, MediaStore.Audio.Media.TITLE);
        }
        for (Song song : sSongArrayList) {
            if (song.getAlbum().equals(albumTitle)) {
                list.add(song);
            }
        }
        Collections.sort(list);
        return list;
    }
}
