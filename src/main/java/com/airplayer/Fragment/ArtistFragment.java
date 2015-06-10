package com.airplayer.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airplayer.R;
import com.airplayer.activity.ArtistActivity;
import com.airplayer.database.AirPlayerDB;
import com.airplayer.model.Music;
import com.airplayer.util.AirAdapter;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/9.
 */
public class ArtistFragment extends Fragment {

    private RecyclerView mRecyclerView;

    private AirPlayerDB db;
    private List<Music> mList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler, container, false);

        // get data base and load a list from it
        db = AirPlayerDB.newInstance(getParentFragment().getActivity(), 0);
        mList = db.loadList(new String[] { AirPlayerDB.ARTIST, AirPlayerDB.ARTIST_IMAGE }, null, null, 0);

        //find a recycler view and set it up
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getParentFragment().getActivity(), 2));
        mRecyclerView.setAdapter(new ArtistAdapter(getParentFragment().getActivity(), mList));


        return rootView;
    }

    private class ArtistAdapter extends AirAdapter {

        public ArtistAdapter(Context context, List<Music> list) {
            super(context, list);
        }

        @Override
        public View getView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
            return layoutInflater.inflate(R.layout.recycler_artist_item, viewGroup, false);
        }

        @Override
        public String getText(int position) {
            return mList.get(position).getArtist();
        }

        @Override
        public String getImagePath(int position) {
            return mList.get(position).getArtistImage();
        }

        @Override
        public int getTextViewId() {
            return R.id.artist_name;
        }

        @Override
        public int getImageViewId() {
            return R.id.artist_image;
        }

        @Override
        public void onItemClick(int position) {
            Intent intent = new Intent(getParentFragment().getActivity(), ArtistActivity.class);
            intent.putExtra("artist_name", mList.get(position).getArtist());
            startActivity(intent);
        }
    }
}
