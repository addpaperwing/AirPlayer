package com.airplayer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airplayer.R;
import com.airplayer.database.AirPlayerDB;
import com.airplayer.model.Music;
import com.airplayer.util.SongRecyclerAdapter;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/9.
 */
public class SongFragment extends Fragment {

    private RecyclerView mRecyclerView;

    private AirPlayerDB db;
    private List<Music> mList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_song_recycler, container, false);

        // get data base and load a list from it
        db = AirPlayerDB.newInstance(getParentFragment().getActivity(), 0);
        mList = db.loadList(new String[] {
                AirPlayerDB.TITLE,
                AirPlayerDB.ALBUM,
                AirPlayerDB.ARTIST,
                AirPlayerDB.DURATION,
                AirPlayerDB.PATH,
                AirPlayerDB.ALBUM_ART },
                null, null, 2);

        //find a recycler view and set it up
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.song_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getParentFragment().getActivity()));
        mRecyclerView.setAdapter(new SongRecyclerAdapter(getParentFragment().getActivity(), mList));

        return rootView;
    }
}
