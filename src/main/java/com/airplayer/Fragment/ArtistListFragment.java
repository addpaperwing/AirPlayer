package com.airplayer.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.airplayer.R;
import com.airplayer.Util.QueryUtils;
import com.airplayer.activity.ArtistActivity;
import com.airplayer.model.Artist;
import com.airplayer.model.LibraryAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/2.
 */
public class ArtistListFragment extends Fragment implements LibraryAdapter.LibraryAdapterCallbacks {

    private ListView mListView;
    private List<Artist> artists = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_library_list, container, false);

        mListView = (ListView) rootView.findViewById(R.id.library_list);


        artists = QueryUtils.queryArtists(getParentFragment().getActivity());

        LibraryAdapter<Artist> adapter = new LibraryAdapter<>(getParentFragment().getActivity(),
                R.layout.library_list_item, artists, this);

        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String artistName = artists.get(position).getName();
                int artistId = artists.get(position).getId();
                Intent intent = new Intent(getParentFragment().getActivity(), ArtistActivity.class);
                intent.putExtra("artist_name", artistName);
                intent.putExtra("artist_id", artistId);
                startActivity(intent);
            }
        });


        adapter.notifyDataSetChanged();

        return rootView;
    }

    @Override
    public String onGetTitle(int position) {
        return artists.get(position).getName();
    }

    @Override
    public Bitmap onGetImage(int position) {
        return null;
    }
}
