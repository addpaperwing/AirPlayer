package com.airplayer.fragment;

import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.activity.AirMainActivity;
import com.airplayer.adapter.SongAdapter;
import com.airplayer.model.Album;
import com.airplayer.model.Song;
import com.airplayer.service.PlayMusicService;
import com.airplayer.util.ImageUtils;
import com.airplayer.util.QueryUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/14.
 */
public class AlbumFragment extends Fragment {

    public static final String ALBUM_RECEIVED = "artist_received";

    private Album mAlbum;

    private RecyclerView mRecyclerView;

    private PlayMusicService.PlayerControlBinder mBinder;

    public static AlbumFragment newInstance(Album album) {
        AlbumFragment fragment = new AlbumFragment();
        Bundle args = new Bundle();
        args.putSerializable(ALBUM_RECEIVED, album);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAlbum = (Album) getArguments().getSerializable(ALBUM_RECEIVED);
        mBinder = ((AirMainActivity) getActivity()).getPlayerControlBinder();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.cover_fragment, container, false);

        TextView albumTitle = (TextView) rootView.findViewById(R.id.cover_fragment_title);
        albumTitle.setText(mAlbum.getTitle());

        ImageView albumArt = (ImageView) rootView.findViewById(R.id.cover_fragment_image);
        albumArt.setImageBitmap(ImageUtils.getListItemThumbnail(mAlbum.getAlbumArt()));

        List<Song> songList = QueryUtils.loadSongList(getActivity(),
                "album = ?", new String[]{ mAlbum.getTitle() }, MediaStore.Audio.Media.TRACK);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.cover_fragment_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new SongAdapter(getActivity(), songList, mBinder));

        return rootView;
    }
}
