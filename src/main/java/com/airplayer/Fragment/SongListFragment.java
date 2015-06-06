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
import com.airplayer.model.LibraryAdapter;
import com.airplayer.model.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/2.
 */
public class SongListFragment extends Fragment implements LibraryAdapter.LibraryAdapterCallbacks{

    private ListView mListView;
    private List<Song> songs = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_library_list, container, false);

        mListView = (ListView) rootView.findViewById(R.id.library_list);


        songs = QueryUtils.querySongs(getParentFragment().getActivity());

        LibraryAdapter<Song> adapter = new LibraryAdapter<>(getParentFragment().getActivity(),
                R.layout.library_list_item, songs, this);

        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getParentFragment().getActivity(),
                        songs.get(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });

        adapter.notifyDataSetChanged();

        return rootView;
    }

    @Override
    public String onGetTitle(int position) {
        return songs.get(position).getTitle();
    }

    @Override
    public Bitmap onGetImage(int position) {
        return ImageUtils.getAlbumArt(songs.get(position).getArtPath());
    }
}
