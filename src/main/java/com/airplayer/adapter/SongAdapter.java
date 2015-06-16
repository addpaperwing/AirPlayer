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

    public SongAdapter(Context context, List<Song> list) {
        super(context, list);
    }

    @Override
    public AirAdapter.AirViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case AirAdapter.TYPE_HEADER:
                return new SongViewTextHeaderHolder(
                        getLayoutInflater().inflate(R.layout.recycler_header_text, parent, false),
                        AirAdapter.TYPE_HEADER);
            case AirAdapter.TYPE_ITEM:
                return new SongViewItemHolder(
                        getLayoutInflater().inflate(R.layout.recycler_item_song, parent, false),
                        AirAdapter.TYPE_ITEM);
            default:
                throw new RuntimeException("no type match, make sure you use types correctly. " +
                        "unmatchable viewType : " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(AirAdapter.AirViewHolder holder, final int position) {

        // when holder is a SongViewItemHolder
        if (holder instanceof SongViewItemHolder) {
            SongViewItemHolder songViewItemHolder = (SongViewItemHolder) holder;

            songViewItemHolder.imageView.setImageResource(android.R.drawable.ic_menu_gallery);

            songViewItemHolder.titleText.setText(getList().get(position - 1).getTitle());
            songViewItemHolder.artistText.setText(getList().get(position - 1).getArtist());
            songViewItemHolder.durationText.setText(formatTime(getList().get(position - 1).getDuration()));

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

    public class SongViewItemHolder extends AirAdapter.AirViewHolder {
        ImageView imageView;
        TextView titleText;
        TextView artistText;
        TextView durationText;
        Toolbar listItem;

        public SongViewItemHolder(View itemView, int holderType) {
            super(itemView, holderType);
            imageView = (ImageView) itemView.findViewById(R.id.song_imageView);
            titleText = (TextView) itemView.findViewById(R.id.song_title);
            artistText = (TextView) itemView.findViewById(R.id.song_artist_name);
            durationText = (TextView) itemView.findViewById(R.id.song_duration);
            listItem = (Toolbar) itemView.findViewById(R.id.song_item);
        }
    }

    public class SongViewTextHeaderHolder extends AirAdapter.AirViewHolder {

        TextView headerText;

        public SongViewTextHeaderHolder(View itemView, int holderType) {
            super(itemView, holderType);
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

