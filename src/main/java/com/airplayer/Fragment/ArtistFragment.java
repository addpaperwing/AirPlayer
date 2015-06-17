package com.airplayer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.activity.AirMainActivity;
import com.airplayer.adapter.AirAdapter;
import com.airplayer.adapter.AlbumAdapter;
import com.airplayer.model.Album;
import com.airplayer.model.Artist;
import com.airplayer.util.ImageUtils;
import com.airplayer.util.QueryUtils;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/14.
 */
public class ArtistFragment extends Fragment {

    public static final String ARTIST_RECEIVED = "artist_received";

    private Artist mArtist;

    private List<Album> mAlbumList;

    private RecyclerView mRecyclerView;

    public static ArtistFragment newInstance(Artist artist) {
        ArtistFragment fragment = new ArtistFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARTIST_RECEIVED, artist);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArtist = (Artist) getArguments().get(ARTIST_RECEIVED);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler, container, false);
        rootView.setPadding(0, 0, 0, 0);
        ((AirMainActivity)getActivity()).getToolbar().setVisibility(View.INVISIBLE);

        mAlbumList = QueryUtils.loadAlbumList(getActivity(),
                "artist = ?", new String[] { mArtist.getName() }, MediaStore.Audio.Albums.FIRST_YEAR);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        final GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? manager.getSpanCount() : 1;
            }
        });
        mRecyclerView.setLayoutManager(manager);
        AlbumAdapter adapter = new ArtistAlbumAdapter(getActivity(), mAlbumList);
        adapter.setItemClickListener(new AirAdapter.ClickListener() {
            @Override
            public void itemClicked(View view, int position) {
                FragmentTransaction ft = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, AlbumFragment.newInstance(mAlbumList.get(position - 1)));
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

    private class ArtistAlbumAdapter extends AlbumAdapter {

        public ArtistAlbumAdapter(Context context, List<Album> list) {
            super(context, list);
        }

        @Override
        public void setUpViewHolder(AlbumHeaderViewHolder holder) {
            ArtistAlbumHeader header = (ArtistAlbumHeader) holder;
            header.name.setText(mArtist.getName());
            header.albumCount.setText(mAlbumList.size() + " albums");
        }

        @Override
        public AirHeadViewHolder onCreateHeadViewHolder(ViewGroup parent) {
            return new ArtistAlbumHeader(getLayoutInflater()
                    .inflate(R.layout.recycler_header_image, parent, false));
        }

        private class ArtistAlbumHeader extends AlbumAdapter.AlbumHeaderViewHolder {

            private ImageView image;
            private TextView name;
            private TextView albumCount;

            public ArtistAlbumHeader(View itemView) {
                super(itemView);
                image = (ImageView) itemView.findViewById(R.id.header_image);
                name = (TextView) itemView.findViewById(R.id.header_title);
                albumCount = (TextView) itemView.findViewById(R.id.header_sub_title);
            }
        }
    }
}
