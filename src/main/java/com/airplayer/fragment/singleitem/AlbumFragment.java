package com.airplayer.fragment.singleitem;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.activity.AirMainActivity;
import com.airplayer.activity.fetchpicture.FetchAlbumArtActivity;
import com.airplayer.activity.fetchpicture.FetchPictureActivity;
import com.airplayer.adapter.AirAdapter;
import com.airplayer.adapter.SongAdapter;
import com.airplayer.model.AirModelSingleton;
import com.airplayer.model.Album;
import com.airplayer.model.PictureGettable;
import com.airplayer.model.Song;
import com.airplayer.service.PlayMusicService;
import com.airplayer.util.BitmapUtils;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/14.
 */
public class AlbumFragment extends SingleItemChildFragment {

    public static final String TAG = AlbumFragment.class.getSimpleName();

    public static final String ALBUM_RECEIVED = "album_received";

    private ImageView mImageView;

    private Album mAlbum;

    private List<Song> mSongList;

    private PlayMusicService.PlayerControlBinder mBinder;

    public static AlbumFragment newInstance(Album album) {
        AlbumFragment fragment = new AlbumFragment();
        Bundle args = new Bundle();
        args.putSerializable(ALBUM_RECEIVED, album);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAlbum = (Album) getArguments().getSerializable(ALBUM_RECEIVED);
        mBinder = ((AirMainActivity) getActivity()).getPlayerControlBinder();
        mSongList = AirModelSingleton.getInstance(getActivity()).getAlbumSong(mAlbum.getTitle());
        ((AirMainActivity) getActivity()).getToolbar().setVisibility(View.INVISIBLE);

    }

    public void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        AlbumSongAdapter adapter = new AlbumSongAdapter(getActivity(), mSongList);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PictureGettable.REQUEST_CODE_FETCH_PICTURE) {
                mAlbum.setPictureDownloaded(true);
                mImageView.setImageBitmap(BitmapUtils.getWindowWideBitmap(getActivity(),
                        mAlbum.getAlbumArtPath(), true));
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
                    .inflate(R.layout.recycler_header_image_fab, parent, false));
        }

        @Override
        public void onBindHeadViewHolder(AirAdapter.AirHeadViewHolder holder) {
            final AlbumSongHeader header = (AlbumSongHeader) holder;
            header.image.setImageBitmap(BitmapUtils.getWindowWideBitmap(getActivity(), mAlbum.getAlbumArtPath(), true));
            header.image.setOnClickListener(new OnPictureClickListener(mAlbum, FetchAlbumArtActivity.class) {

                @Override
                public void onPictureDelete() {
                    mAlbum.setPictureDownloaded(false);
                    header.image.setImageBitmap(BitmapUtils
                            .getWindowWideBitmap(getActivity(), mAlbum.getAlbumArtPath(), true));
                }
            });
            mImageView = header.image;

            header.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBinder.playMusic(0, mSongList);
                }
            });
            header.title.setText(mAlbum.getTitle());
            header.subTitle.setText(mAlbum.getAlbumArtist());
            if (mAlbum.getYear() != 0) {
                header.desc.setText(mAlbum.getYear() + " , " + mSongList.size() + " songs");
            } else {
                header.desc.setText(mSongList.size() + " songs");
            }
        }

        private class AlbumSongHeader extends AirAdapter.AirHeadViewHolder {

            private ImageView image;
            private FloatingActionButton fab;
            private TextView title;
            private TextView subTitle;
            private TextView desc;

            public AlbumSongHeader(View itemView) {
                super(itemView);
                image = (ImageView) itemView.findViewById(R.id.header_image);
                fab = (FloatingActionButton) itemView.findViewById(R.id.header_fab);
                title = (TextView) itemView.findViewById(R.id.header_title);
                subTitle = (TextView) itemView.findViewById(R.id.header_sub_title);
                desc = (TextView) itemView.findViewById(R.id.header_desc);
            }
        }
    }
}
