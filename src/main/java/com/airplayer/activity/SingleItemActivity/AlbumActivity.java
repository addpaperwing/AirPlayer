package com.airplayer.activity.SingleItemActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.activity.AirMainActivity;
import com.airplayer.activity.fetchpicture.FetchAlbumArtActivity;
import com.airplayer.adapter.AirAdapter;
import com.airplayer.adapter.SongAdapter;
import com.airplayer.fragment.child.AlbumGridFragment;
import com.airplayer.fragment.singleitem.SingleItemChildFragment;
import com.airplayer.model.AirModelSingleton;
import com.airplayer.model.Album;
import com.airplayer.model.PictureGettable;
import com.airplayer.model.Song;
import com.airplayer.service.PlayMusicService;

import java.util.List;

/**
 * Created by Administrator on 2015/9/11 0011.
 */
public class AlbumActivity extends SingleItemActivity {

    public static final String TAG = AlbumActivity.class.getSimpleName();

    public static final String KEY_EXTRA_ALBUM = "key_extra_album";

    private Album mAlbum;

    private List<Song> mSongList;

    private PlayMusicService.PlayerControlBinder mBinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAlbum = (Album) getIntent().getSerializableExtra(KEY_EXTRA_ALBUM);
//        mBinder = ((AirMainActivity) ).getPlayerControlBinder();
        mSongList = AirModelSingleton.getInstance(this).getAlbumSong(mAlbum.getTitle());
    }

    public void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AlbumSongAdapter adapter = new AlbumSongAdapter(this, mSongList);
        adapter.showIconImage(false);
        adapter.setOnItemClickListener(new AirAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                mBinder.playMusic(position - 1, mSongList);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void setupDraweeView() {
        mDraweeView.setOnClickListener(new SingleItemActivity.OnPictureClickListener(mAlbum, FetchAlbumArtActivity.class) {
            @Override
            public void onPictureDelete() {
                mAlbum.setPictureDownloaded(false);
                mDraweeView.setImageURI(mAlbum.getAlbumArtUri());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PictureGettable.REQUEST_CODE_FETCH_PICTURE) {
                mAlbum.setPictureDownloaded(true);
                mDraweeView.setImageURI(mAlbum.getAlbumArtUri());
            }
        }
    }

    public class AlbumSongAdapter extends SongAdapter {

        public AlbumSongAdapter(Context context, List<Song> list) {
            super(context, list);
        }

        @Override
        public AirHeadViewHolder onCreateHeadViewHolder(ViewGroup parent) {
            return new AlbumSongHeader(getLayoutInflater()
                    .inflate(R.layout.recycler_header_image, parent, false));
        }

        @Override
        public void onBindHeadViewHolder(AirAdapter.AirHeadViewHolder holder) {
            AlbumSongHeader header = (AlbumSongHeader) holder;

            header.title.setText(mAlbum.getTitle());
            header.subTitle.setText(mAlbum.getAlbumArtist());
            if (mAlbum.getYear() != 0) {
                header.desc.setText(mAlbum.getYear() + " , " + mSongList.size() + " songs");
            } else {
                header.desc.setText(mSongList.size() + " songs");
            }
        }

        private class AlbumSongHeader extends AirAdapter.AirHeadViewHolder {

            private TextView title;
            private TextView subTitle;
            private TextView desc;

            public AlbumSongHeader(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.header_title);
                subTitle = (TextView) itemView.findViewById(R.id.header_sub_title);
                desc = (TextView) itemView.findViewById(R.id.header_desc);
            }
        }
    }
}
