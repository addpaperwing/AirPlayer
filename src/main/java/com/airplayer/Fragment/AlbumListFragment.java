package com.airplayer.Fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.airplayer.R;
import com.airplayer.Util.ImageUtils;
import com.airplayer.Util.QueryUtils;
import com.airplayer.model.Album;
import com.airplayer.model.LibraryAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/2.
 */
public class AlbumListFragment extends Fragment implements LibraryAdapter.LibraryAdapterCallbacks{

    private ListView mListView;
    private List<Album> albums = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_library_list, container, false);

        mListView = (ListView) rootView.findViewById(R.id.library_list);

        albums = QueryUtils.queryAlbums(getParentFragment().getActivity());

        LibraryAdapter<Album> adapter = new LibraryAdapter<>(getParentFragment().getActivity(),
                R.layout.library_list_item, albums, this);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getParentFragment().getActivity(),
                        albums.get(position).getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        adapter.notifyDataSetChanged();

        return rootView;
    }

    @Override
    public String onGetTitle(int position) {
        return albums.get(position).getTitle();
    }

    @Override
    public Bitmap onGetImage(int position) {
        return ImageUtils.getAlbumArt(albums.get(position).getArtPath());
    }
}
