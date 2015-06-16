package com.airplayer.fragment;

import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airplayer.R;
import com.airplayer.activity.AirMainActivity;
import com.airplayer.adapter.AirAdapter;
import com.airplayer.adapter.SongAdapter;
import com.airplayer.model.Album;
import com.airplayer.model.Song;
import com.airplayer.service.PlayMusicService;
import com.airplayer.util.QueryUtils;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/14.
 */
public class AlbumFragment extends Fragment {

    public static final String ALBUM_RECEIVED = "artist_received";

    private Album mAlbum;

    private List<Song> mSongList;

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
        View rootView = inflater.inflate(R.layout.fragment_recycler, container, false);

        mSongList = QueryUtils.loadSongList(getActivity(),
                "album = ?", new String[]{ mAlbum.getTitle() }, MediaStore.Audio.Media.TRACK);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        SongAdapter adapter = new SongAdapter(getActivity(), mSongList);
        adapter.setItemClickListener(new AirAdapter.ClickListener() {
            @Override
            public void itemClicked(View view, int position) {
                mBinder.playMusic(position - 1, mSongList);
            }

            @Override
            public void headerClicked(View view) {

            }
        });
        mRecyclerView.setAdapter(adapter);

        return rootView;
    }
}
