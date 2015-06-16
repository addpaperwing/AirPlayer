package com.airplayer.fragment;

import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.activity.AirMainActivity;
import com.airplayer.adapter.AirAdapter;
import com.airplayer.adapter.SongAdapter;
import com.airplayer.model.Song;
import com.airplayer.service.PlayMusicService;
import com.airplayer.util.QueryUtils;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/9.
 */
public class SongListFragment extends Fragment {

    private RecyclerView mRecyclerView;

    private List<Song> mList;

    private PlayMusicService.PlayerControlBinder mBinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinder = ((AirMainActivity) getParentFragment().getActivity()).getPlayerControlBinder();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler, container, false);

        // get data base and load a list from it
        mList = QueryUtils.loadSongList(getParentFragment().getActivity(), null, null, MediaStore.Audio.Media.TITLE);

        //find a recycler view and set it up
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getParentFragment().getActivity()));
        SongAdapter adapter = new SongAdapter(getParentFragment().getActivity(), mList);
        adapter.setItemClickListener(new AirAdapter.ClickListener() {
            @Override
            public void itemClicked(View view, int position) {
                mBinder.playMusic(position - 1, mList);
            }

            @Override
            public void headerClicked(View view) {

            }
        });
        mRecyclerView.setAdapter(adapter);

        return rootView;
    }
}
