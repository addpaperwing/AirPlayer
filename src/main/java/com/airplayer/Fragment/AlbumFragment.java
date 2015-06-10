package com.airplayer.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airplayer.R;
import com.airplayer.database.AirPlayerDB;
import com.airplayer.model.Music;
import com.airplayer.util.AirAdapter;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/9.
 */
public class AlbumFragment extends Fragment {

    private RecyclerView mRecyclerView;

    private AirPlayerDB db;
    private List<Music> mList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler, container, false);

        // get data base and load a list from it
        db = AirPlayerDB.newInstance(getParentFragment().getActivity(), 0);
        mList = db.loadList(new String[] { AirPlayerDB.ALBUM, AirPlayerDB.ALBUM_ART }, null, null, 1);

        //find a recycler view and set it up
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getParentFragment().getActivity(), 2));
        mRecyclerView.setAdapter(new AlbumAdapter(getParentFragment().getActivity(), mList));


        return rootView;
    }

    private class AlbumAdapter extends AirAdapter {

        public AlbumAdapter(Context context, List<Music> list) {
            super(context, list);
        }

        @Override
        public View getView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
            return layoutInflater.inflate(R.layout.recycler_album_item, viewGroup, false);
        }

        @Override
        public String getText(int position) {
            return mList.get(position).getAlbum();
        }

        @Override
        public String getImagePath(int position) {
            return mList.get(position).getAlbumArt();
        }

        @Override
        public int getTextViewId() {
            return R.id.album_title;
        }

        @Override
        public int getImageViewId() {
            return R.id.album_art;
        }

        @Override
        public void onItemClick(int position) {

        }
    }
}
