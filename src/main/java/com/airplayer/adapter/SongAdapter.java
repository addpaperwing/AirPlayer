package com.airplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.model.Song;
import com.airplayer.service.PlayMusicService;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/11.
 */

public class SongAdapter extends AirAdapter<Song> {

    private PlayMusicService.PlayerControlBinder mBinder;

    public SongAdapter(Context context, List<Song> list) {
        super(context, list);
    }

    public SongAdapter(Context context, List<Song> list, PlayMusicService.PlayerControlBinder binder) {
        this(context, list);
        mBinder = binder;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case AirAdapter.TYPE_HEADER:
                return new SongViewTextHeaderHolder(
                        getLayoutInflater().inflate(R.layout.recycler_header_text, parent, false));
            case AirAdapter.TYPE_ITEM:
                return new SongViewItemHolder(
                        getLayoutInflater().inflate(R.layout.recycler_item_song, parent, false));
            default:
                throw new RuntimeException("no type match, make sure you use types correctly. " +
                        "unmatchable viewType : " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        // when holder is a SongViewItemHolder
        if (holder instanceof SongViewItemHolder) {
            SongViewItemHolder songViewItemHolder = (SongViewItemHolder) holder;

            songViewItemHolder.imageView.setImageResource(android.R.drawable.ic_menu_gallery);

            songViewItemHolder.titleText.setText(getList().get(position - 1).getTitle());
            songViewItemHolder.artistText.setText(getList().get(position - 1).getArtist());
            songViewItemHolder.durationText.setText(formatTime(getList().get(position - 1).getDuration()));
            songViewItemHolder.listItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBinder.playMusic(position - 1, getList());
                }
            });


            Picasso.with(getContext())
                    .load(getList().get(position - 1).getAlbumArtUri())
                    .into(songViewItemHolder.imageView);
        }

        //when holder is a SongViewTextHeaderHolder
        if (holder instanceof SongViewTextHeaderHolder) {
            SongViewTextHeaderHolder songViewTextHeaderHolder = (SongViewTextHeaderHolder) holder;
            songViewTextHeaderHolder.headerText.setText("Shuffle All");
        }

    }

    public static class SongViewItemHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleText;
        TextView artistText;
        TextView durationText;
        Toolbar listItem;

        public SongViewItemHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.song_imageView);
            titleText = (TextView) itemView.findViewById(R.id.song_title);
            artistText = (TextView) itemView.findViewById(R.id.song_artist_name);
            durationText = (TextView) itemView.findViewById(R.id.song_duration);
            listItem = (Toolbar) itemView.findViewById(R.id.song_item);
        }
    }

    public static class SongViewTextHeaderHolder extends RecyclerView.ViewHolder {

        TextView headerText;

        public SongViewTextHeaderHolder(View itemView) {
            super(itemView);
            headerText =(TextView) itemView.findViewById(R.id.header_text);
        }
    }

    private String formatTime(int duration) {
        int min = 0;
        int sec;

        sec = duration / 1000;
        if(sec > 60){
            min = sec / 60;
            sec = sec % 60;
        }
        return String.format("%02d:%02d", min, sec);
    }
}

