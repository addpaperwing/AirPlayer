package com.airplayer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.airplayer.R;
import com.airplayer.activity.ArtistActivity;
import com.airplayer.database.AirPlayerDB;
import com.airplayer.model.Music;
import com.airplayer.util.*;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/2.
 */
public class LibraryListFragment extends Fragment {

    public static final String CHILD_FRAGMENT_LIST_TYPE = "child_fragment_list_type";

    public static final String ARTIST_NAME = "artist_name";

    private ListView mListView;
    private List<Music> mList;

    private int listType;

    AirPlayerDB db;

    public static LibraryListFragment newInstance(int type) {
        LibraryListFragment fragment = new LibraryListFragment();
        Bundle args = new Bundle();
        args.putInt(CHILD_FRAGMENT_LIST_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_library_list, container, false);

        mListView = (ListView) rootView.findViewById(R.id.library_list);

        listType = getArguments().getInt(CHILD_FRAGMENT_LIST_TYPE);
        db = AirPlayerDB.newInstance(getParentFragment().getActivity());

        setList();

        LibraryListAdapter adapter = new LibraryListAdapter(getParentFragment().getActivity(),
                R.layout.library_list_item, mList, listType);

        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Music music = mList.get(position);
                Intent intent;
                switch (listType) {
                    case LibraryListAdapter.ARTIST_LIST:
                        intent = new Intent(getParentFragment().getActivity(), ArtistActivity.class);
                        String artist = music.getArtist();
                        intent.putExtra(ARTIST_NAME, artist);
                        startActivity(intent);
                        break;
                    case LibraryListAdapter.ALBUM_LIST:
//                        intent = new Intent(getParentFragment().getActivity(), ArtistActivity.class);
//                        startActivity(intent);
                        break;
                    case LibraryListAdapter.SONG_LIST:
                        break;
                    default:
                        break;
                }
            }
        });


        adapter.notifyDataSetChanged();

        return rootView;
    }

    private void setList() {
        String[] projection = null;
        switch (listType) {
            case 0:
                projection = new String[] { AirPlayerDB.ARTIST, AirPlayerDB.ARTIST_IMAGE };
                break;
            case 1:
                projection = new String[] { AirPlayerDB.ALBUM, AirPlayerDB.ALBUM_ART };
                break;
            case 2:
                projection = new String[] {
                        AirPlayerDB.TITLE,
                        AirPlayerDB.ALBUM,
                        AirPlayerDB.ARTIST,
                        AirPlayerDB.DURATION,
                        AirPlayerDB.PATH,
                        AirPlayerDB.ALBUM_ART
                };


        }
        mList = db.loadList(projection, null, null, listType);
    }
}
