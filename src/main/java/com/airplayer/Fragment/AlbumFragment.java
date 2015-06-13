package com.airplayer.fragment;

import android.content.Context;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.adapter.AirAdapter;
import com.airplayer.adapter.AlbumAdapter;
import com.airplayer.model.Album;
import com.airplayer.util.ImageUtils;
import com.airplayer.util.QueryUtils;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/9.
 */
public class AlbumFragment extends Fragment {

    private RecyclerView mRecyclerView;

    private List<Album> mList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler, container, false);

        // get data base and load a list from it
        mList = QueryUtils.loadAlbumList(getParentFragment().getActivity(), null, null, MediaStore.Audio.Albums.ALBUM);

        //find a recycler view and set it up
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getParentFragment().getActivity(), 2));
        mRecyclerView.setAdapter(new AlbumAdapter(getParentFragment().getActivity(), mList));

        return rootView;
    }
}
