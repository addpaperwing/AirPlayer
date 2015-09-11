package com.airplayer.fragment.singleitem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.activity.AirMainActivity;
import com.airplayer.activity.fetchpicture.FetchArtistPictureActivity;
import com.airplayer.activity.fetchpicture.FetchPictureActivity;
import com.airplayer.adapter.AirAdapter;
import com.airplayer.adapter.AlbumAdapter;
import com.airplayer.model.AirModelSingleton;
import com.airplayer.model.Album;
import com.airplayer.model.Artist;
import com.airplayer.model.PictureGettable;
import com.airplayer.util.BitmapUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/14.
 */
public class ArtistFragment extends SingleItemChildFragment {

    public static final String ARTIST_RECEIVED = "artist_received";

    private Artist mArtist;

    private List<Album> mAlbumList;

    private FragmentManager mFragmentManager;

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
        mAlbumList = AirModelSingleton.getInstance(getActivity()).getArtistAlbum(mArtist.getName());
        mFragmentManager = getActivity().getSupportFragmentManager();
    }

    public void setupRecyclerView(RecyclerView recyclerView) {
        final GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? manager.getSpanCount() : 1;
            }
        });
        recyclerView.setLayoutManager(manager);
        AlbumAdapter adapter = new ArtistAlbumAdapter(getActivity(), mAlbumList);
        adapter.setOnItemClickListener(new AirAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                ft.replace(R.id.fragment_container, AlbumFragment.newInstance(mAlbumList.get(position - 1)));
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void setupDraweeView() {
        mDraweeView.setImageURI(mArtist.getArtistPictureUri());
        mDraweeView.setOnClickListener(new OnPictureClickListener(mArtist, FetchArtistPictureActivity.class) {
            @Override
            public void onPictureDelete() {
                mArtist.setPictureDownloaded(false);
                mDraweeView.setImageURI(mArtist.getArtistPictureUri());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PictureGettable.REQUEST_CODE_FETCH_PICTURE) {
                mArtist.setPictureDownloaded(true);
                mDraweeView.setImageURI(mArtist.getArtistPictureUri());
            }
        }
    }

    private class ArtistAlbumAdapter extends AlbumAdapter {

        public ArtistAlbumAdapter(Context context, List<Album> list) {
            super(context, list);
        }

        @Override
        public AirHeadViewHolder onCreateHeadViewHolder(ViewGroup parent) {
            return new ArtistAlbumHeader(getLayoutInflater()
                    .inflate(R.layout.recycler_header_image, parent, false));
        }

        @Override
        public void onBindHeadViewHolder(AirAdapter.AirHeadViewHolder holder) {
            ArtistAlbumHeader header = (ArtistAlbumHeader) holder;

            header.name.setText(mArtist.getName());
            header.albumCount.setText(mAlbumList.size() + " albums");
        }

        private class ArtistAlbumHeader extends AirAdapter.AirHeadViewHolder {

            private TextView name;
            private TextView albumCount;

            public ArtistAlbumHeader(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.header_title);
                albumCount = (TextView) itemView.findViewById(R.id.header_sub_title);
            }
        }
    }
}
