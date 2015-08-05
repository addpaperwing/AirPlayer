package com.airplayer.model;

import android.content.Context;
import android.provider.MediaStore;

import com.airplayer.util.QueryUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by ZiyiTsang on 15/7/22.
 */
public class AirModelSingleton {

    private static ArrayList<Artist> sArtists;

    private static ArrayList<Album> sAlbums;

    private static ArrayList<Song> sSongs;

    private static ArrayList<Album> sRecentAlbums;

    private static AirModelSingleton singleton;

    private Context context;

    private AirModelSingleton(Context context) {
        this.context = context;
    }

    public synchronized static AirModelSingleton getInstance(Context context) {
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
        if (sArtists == null) {
            synchronized (AirModelSingleton.class) {
                if (sArtists == null) {
                    sArtists = QueryUtils.loadArtistList(context);
                }
            }
        }
        return sArtists;
    }

    public ArrayList<Album> getAlbumArrayList() {
        if (sAlbums == null) {
            synchronized (AirModelSingleton.class) {
                if (sAlbums == null) {
                    sAlbums = QueryUtils.loadAlbumList(context, null, null, MediaStore.Audio.Albums.ALBUM);
                    if (sArtists == null) getArtistArrayList();
                    for (int i = 0; i < sArtists.size(); i++) {
                        Artist artist = sArtists.get(i);
                        for (int j = 0; j < sAlbums.size(); j++) {
                            Album album = sAlbums.get(j);
                            if (artist.getName().equals(album.getAlbumArtist())) {
                                sAlbums.get(j).setArtist(artist);
                            }
                        }
                    }
                }
            }
        }
        return sAlbums;
    }

    public ArrayList<Song> getSongArrayList() {
        if (sSongs == null) {
            synchronized (AirModelSingleton.class) {
                if (sSongs == null) {
                    sSongs = QueryUtils.loadSongList(context, null, null, MediaStore.Audio.Media.TITLE);
                }
                if (sAlbums == null) getAlbumArrayList();
                for (int i = 0; i < sAlbums.size(); i++) {
                    Album album = sAlbums.get(i);
                    for (int j = 0; j < sSongs.size(); j++) {
                        Song song = sSongs.get(j);
                        if (album.getId() == song.getAlbumId()) {
                            song.setAlbum(album);
                        }
                    }
                }
            }
        }
        return sSongs;
    }

    public ArrayList<Album> getRecentAlbums() {
        if (sRecentAlbums == null) {
            synchronized (AirModelSingleton.class) {
                if (sRecentAlbums == null) {
                    sRecentAlbums = QueryUtils.loadRecentAlbum(context);
                    if (sAlbums == null) {
                        getAlbumArrayList();
                    }
                    loadFavourAlbums();
                }
            }
        }
        return sRecentAlbums;
    }

    private void loadFavourAlbums() {
        ArrayList<Album> freqAlbums = new ArrayList<>();
        if (sAlbums == null) {
            getAlbumArrayList();
        }

        if (sAlbums.size() == 0) {
            return;
        }

        for (Album album : sAlbums) {
            if (album.getFreq() > 0) {
                freqAlbums.add(album);
            }
        }

        if (freqAlbums.size() == 0) {
            return;
        }

        Collections.sort(freqAlbums, new Comparator<Album>() {
            @Override
            public int compare(Album lhs, Album rhs) {
                return rhs.getFreq() - lhs.getFreq();
            }
        });

        for (int i = 0; i < 6; i++) {
            if (sRecentAlbums.size() == 12) {
                sRecentAlbums.remove(6);
            }
            sRecentAlbums.add(freqAlbums.get(i));
        }
    }

    public ArrayList<Album> getArtistAlbum(String artistName) {
        ArrayList<Album> list = new ArrayList<>();
        if (sAlbums == null) {
            getAlbumArrayList();
        }
        for (Album album : sAlbums) {
            if (album.getAlbumArtist().equals(artistName)) {
                list.add(album);
            }
        }
        Collections.sort(list);
        return list;
    }

    public ArrayList<Song> getAlbumSong(String albumTitle) {
        ArrayList<Song> list = new ArrayList<>();
        if (sSongs == null) {
            getSongArrayList();
        }
        for (Song song : sSongs) {
            if (song.getAlbum().equals(albumTitle)) {
                list.add(song);
            }
        }
        Collections.sort(list);
        return list;
    }
}
