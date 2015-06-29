package com.airplayer.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airplayer.R;
import com.airplayer.activity.AirMainActivity;
import com.airplayer.adapter.AirAdapter;
import com.airplayer.adapter.SongAdapter;
import com.airplayer.model.Song;
import com.airplayer.service.PlayMusicService;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/20.
 */
public class PlayListFragment extends Fragment {

    private List<Song> mPlayList;

    private RecyclerView mRecyclerView;

    private SongAdapter mAdapter;

    private PlayMusicService.PlayerControlBinder mBinder;

    private ListItemScrollReceiver receiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiver = new ListItemScrollReceiver();
        IntentFilter filter = new IntentFilter(PlayMusicService.PLAY_STATE_CHANGE);
        getActivity().registerReceiver(receiver, filter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler, container, false);

        mBinder = ((AirMainActivity) getActivity()).getPlayerControlBinder();
        mPlayList = mBinder.getPlayList();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new SongAdapter(getActivity(), mPlayList) {

            @Override
            public AirFootViewHolder onCreateFootViewHolder(ViewGroup parent) {
                return new AirAdapter.AirFootViewHolder(getLayoutInflater()
                        .inflate(R.layout.recycler_item_song, parent, false));
            }

            @Override
            public void onBindFootViewHolder(AirFootViewHolder footHolder) { }
        };
        mAdapter.setItemClickListener(new AirAdapter.ClickListener() {
            @Override
            public void itemClicked(View view, int position) {
                mBinder.playMusic(position - 1, mPlayList);
                mAdapter.notifyItemChanged(position);
            }

            @Override
            public void headerClicked(View view) { }

            @Override
            public void footerClicked(View view) { }
        });
        mAdapter.showEQAnimation(true);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scrollToPosition(mBinder.getPosition() + 1);
        return rootView;
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(receiver);
        super.onDestroy();
    }

    private class ListItemScrollReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int playState = intent.getIntExtra(PlayMusicService.PLAY_STATE_KEY, -1);
            if (playState == PlayMusicService.PLAY_STATE_PLAY) {
                mRecyclerView.scrollToPosition(mBinder.getPosition() - 1);
            }
            mAdapter.notifyDataSetChanged();
        }
    }
}
