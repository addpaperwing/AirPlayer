package com.airplayer.fragment;

import android.content.Context;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airplayer.R;
import com.airplayer.adapter.AirAdapter;
import com.airplayer.adapter.AlbumAdapter;
import com.airplayer.model.Album;
import com.airplayer.util.QueryUtils;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/9.
 */
public class AlbumGridFragment extends Fragment {

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
        final GridLayoutManager manager = new GridLayoutManager(getParentFragment().getActivity(), 2);

        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? manager.getSpanCount() : 1;
            }
        });
        mRecyclerView.setLayoutManager(manager);

        AlbumAdapter adapter = new AlbumGridAdapter(getParentFragment().getActivity(), mList);
        adapter.setItemClickListener(new AirAdapter.ClickListener() {
            @Override
            public void itemClicked(View view, int position) {
                FragmentTransaction ft = getParentFragment().getActivity()
                        .getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, AlbumFragment.newInstance(mList.get(position - 1)));
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }

            @Override
            public void headerClicked(View view) {

            }
        });
        mRecyclerView.setAdapter(adapter);

        return rootView;
    }

    private class AlbumGridAdapter extends AlbumAdapter {
        public AlbumGridAdapter(Context context, List<Album> list) {
            super(context, list);
        }

        @Override
        public void setUpViewHolder(AlbumHeaderViewHolder holder) {

        }

        @Override
        public AirHeadViewHolder onCreateHeadViewHolder(ViewGroup parent) {
            return new AlbumHeaderViewHolder(getLayoutInflater()
                    .inflate(R.layout.recycler_header_empty, parent, false));
        }
    }
}
