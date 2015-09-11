package com.airplayer.activity.SingleItemActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.activity.fetchpicture.FetchArtistPictureActivity;
import com.airplayer.adapter.AirAdapter;
import com.airplayer.adapter.AlbumAdapter;
import com.airplayer.model.AirModelSingleton;
import com.airplayer.model.Album;
import com.airplayer.model.Artist;
import com.airplayer.model.PictureGettable;

import java.util.List;

/**
 * Created by Administrator on 2015/9/11 0011.
 */
public class ArtistActivity extends SingleItemActivity {

    public static final String KEY_EXTRA_ARTIST = "key_extra_artist";

    private Artist mArtist;

    private List<Album> mAlbumList;

    private FragmentManager mFragmentManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArtist = (Artist) getIntent().getSerializableExtra(KEY_EXTRA_ARTIST);
        mAlbumList = AirModelSingleton.getInstance(this).getArtistAlbum(mArtist.getName());
        mFragmentManager = getSupportFragmentManager();
    }

    public void setupRecyclerView(RecyclerView recyclerView) {
        final GridLayoutManager manager = new GridLayoutManager(this, 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? manager.getSpanCount() : 1;
            }
        });
        recyclerView.setLayoutManager(manager);
        AlbumAdapter adapter = new ArtistAlbumAdapter(this, mAlbumList);
        adapter.setOnItemClickListener(new AirAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                Intent intent = new Intent(ArtistActivity.this, AlbumActivity.class);
                intent.putExtra(AlbumActivity.KEY_EXTRA_ALBUM, mAlbumList.get(position - 1));
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void setupDraweeView() {
        mDraweeView.setOnClickListener(new SingleItemActivity.OnPictureClickListener(mArtist, FetchArtistPictureActivity.class) {
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
