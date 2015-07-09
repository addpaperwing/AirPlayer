package com.airplayer.fragment.singleItem;

import android.content.Context;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.activity.AirMainActivity;
import com.airplayer.adapter.AirAdapter;
import com.airplayer.adapter.SongAdapter;
import com.airplayer.model.Album;
import com.airplayer.model.Song;
import com.airplayer.service.PlayMusicService;
import com.airplayer.util.ImageUtils;
import com.airplayer.util.QueryUtils;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/14.
 */
public class AlbumFragment extends SingleItemChildFragment {

    public static final String TAG = AlbumFragment.class.getSimpleName();

    public static final String ALBUM_RECEIVED = "artist_received";

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
        mSongList = QueryUtils.loadSongList(getActivity(),
                "album = ?", new String[]{mAlbum.getTitle()}, MediaStore.Audio.Media.TRACK);

        ((AirMainActivity) getActivity()).getToolbar().setVisibility(View.INVISIBLE);
    }

    public void setUpRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        AlbumSongAdapter adapter = new AlbumSongAdapter(getActivity(), mSongList);
        adapter.showIconImage(false);
        adapter.setItemClickListener(new AirAdapter.ClickListener() {
            @Override
            public void itemClicked(View view, int position) {
                mBinder.playMusic(position - 1, mSongList);
            }

            @Override
            public void headerClicked(View view) { }

            @Override
            public void footerClicked(View view) { }
        });

        recyclerView.setAdapter(adapter);
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
            header.image.setImageBitmap(ImageUtils.getWindowWideBitmap(getActivity(), mAlbum.getAlbumArtPath()));
            header.title.setText(mAlbum.getTitle());
            header.subTitle.setText(mAlbum.getAlbumArtist());
            header.desc.setText(mAlbum.getYear() + " , " + mSongList.size() + " songs");
        }

        private class AlbumSongHeader extends AirAdapter.AirHeadViewHolder {

            private ImageView image;
            private TextView title;
            private TextView subTitle;
            private TextView desc;

            public AlbumSongHeader(View itemView) {
                super(itemView);
                image = (ImageView) itemView.findViewById(R.id.header_image);
                title = (TextView) itemView.findViewById(R.id.header_title);
                subTitle = (TextView) itemView.findViewById(R.id.header_sub_title);
                desc = (TextView) itemView.findViewById(R.id.header_desc);
            }
        }
    }
}
