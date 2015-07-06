package com.airplayer.fragment.child;

import android.provider.MediaStore;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.airplayer.R;
import com.airplayer.adapter.AirAdapter;
import com.airplayer.adapter.AlbumAdapter;
import com.airplayer.fragment.singleItem.AlbumFragment;
import com.airplayer.model.Album;
import com.airplayer.util.QueryUtils;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/9.
 */
public class AlbumGridFragment extends MyLibraryChildFragment  {

    private List<Album> mList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mList = QueryUtils.loadAlbumList(getParentFragment().getActivity(), null, null, MediaStore.Audio.Albums.ALBUM);
    }

    @Override
    public void setUpRecyclerView(RecyclerView recyclerView) {
        final GridLayoutManager manager = new GridLayoutManager(getParentFragment().getActivity(), 2);

        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? manager.getSpanCount() : 1;
            }
        });
        recyclerView.setLayoutManager(manager);

        AlbumAdapter adapter = new AlbumAdapter(getParentFragment().getActivity(), mList);
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

            @Override
            public void footerClicked(View view) {
            }
        });
        recyclerView.setAdapter(adapter);
    }
}
