package com.airplayer.fragment.child;

import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.airplayer.activity.AirMainActivity;
import com.airplayer.adapter.AirAdapter;
import com.airplayer.adapter.SongAdapter;
import com.airplayer.model.AirModelSingleton;
import com.airplayer.model.Song;
import com.airplayer.service.PlayMusicService;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/9.
 */
public class SongListFragment extends MyLibraryChildFragment {


    private List<Song> mList;

    private PlayMusicService.PlayerControlBinder mBinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinder = ((AirMainActivity) getParentFragment().getActivity()).getPlayerControlBinder();
        mList = AirModelSingleton.getInstance(getParentFragment().getActivity())
                .getSongArrayList();
    }

    @Override
    public void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getParentFragment().getActivity()));
        SongAdapter adapter = new SongAdapter(getParentFragment().getActivity(), mList);

        adapter.setOnItemClickListener(new AirAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                mBinder.playMusic(position - 1, mList);
            }

//            @Override
//            public void headerClicked(View view) {
//                List<Song> listOrdered = QueryUtils.loadSongList(
//                        getParentFragment().getActivity(), null, null, MediaStore.Audio.Media.TITLE
//                );
//                ArrayList<Song> listShuffled = new ArrayList<Song>();
//                do {
//                    int shuffle = (int) Math.round(Math.random() * (listOrdered.size() - 1));
//                    listShuffled.add(listOrdered.get(shuffle));
//                    listOrdered.remove(shuffle);
//                } while (listOrdered.size() > 0);
//                mBinder.playMusic(0, listShuffled);
//            }
        });
        recyclerView.setAdapter(adapter);
    }
}
