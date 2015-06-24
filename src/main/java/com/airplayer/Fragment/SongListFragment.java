package com.airplayer.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.activity.AirMainActivity;
import com.airplayer.adapter.AirAdapter;
import com.airplayer.adapter.SongAdapter;
import com.airplayer.model.Song;
import com.airplayer.service.PlayMusicService;
import com.airplayer.util.QueryUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/9.
 */
public class SongListFragment extends Fragment {

    private RecyclerView mRecyclerView;

    private List<Song> mList;

    private PlayMusicService.PlayerControlBinder mBinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinder = ((AirMainActivity) getParentFragment().getActivity()).getPlayerControlBinder();
        // get data base and load a list from it
        mList = QueryUtils.loadSongList(getParentFragment().getActivity(), null, null, MediaStore.Audio.Media.TITLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler, container, false);

        //find a recycler view and set it up
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getParentFragment().getActivity()));
        SongAdapter adapter = new SongListAdapter(getParentFragment().getActivity(), mList);

        adapter.setItemClickListener(new AirAdapter.ClickListener() {
            @Override
            public void itemClicked(View view, int position) {
                mBinder.playMusic(position - 1, mList);
            }

            @Override
            public void headerClicked(View view) {
                List<Song> listOrdered = QueryUtils.loadSongList(
                        getParentFragment().getActivity(), null, null, MediaStore.Audio.Media.TITLE
                );
                ArrayList<Song> listShuffled = new ArrayList<Song>();
                do {
                    int shuffle = (int) Math.round(Math.random() * (listOrdered.size() - 1));
                    listShuffled.add(listOrdered.get(shuffle));
                    listOrdered.remove(shuffle);
                } while (listOrdered.size() > 0);
                mBinder.playMusic(0, listShuffled);
            }
        });
        mRecyclerView.setAdapter(adapter);
        return rootView;
    }

    public class SongListAdapter extends SongAdapter {
        public SongListAdapter(Context context, List<Song> list) {
            super(context, list);
        }

        @Override
        public SongListHeadViewHolder onCreateHeadViewHolder(ViewGroup parent) {
            return new SongListHeadViewHolder(getLayoutInflater()
                    .inflate(R.layout.recycler_item_song, parent, false));
        }

        @Override
        public void setUpViewHolder(AirAdapter.AirHeadViewHolder holder) {
            SongListHeadViewHolder header = (SongListHeadViewHolder) holder;
            header.image.setImageResource(R.drawable.btn_shuffle_on);
            header.image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            header.title.setText("Shuffle All");
            header.title.setTextColor(getResources().getColor(R.color.air_accent_color));
            header.desc.setText(mList.size() + " songs");
            header.desc.setTextColor(getResources().getColor(R.color.air_accent_color));
        }

        private class SongListHeadViewHolder extends AirAdapter<Song>.AirHeadViewHolder {

            private ImageView image;
            private TextView title;
            private TextView desc;

            public SongListHeadViewHolder(View itemView) {
                super(itemView);
                image = (ImageView) itemView.findViewById(R.id.song_imageView);
                title = (TextView) itemView.findViewById(R.id.song_title);
                desc = (TextView) itemView.findViewById(R.id.song_artist_name);
            }
        }
    }
}
