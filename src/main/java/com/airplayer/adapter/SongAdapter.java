package com.airplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.model.Song;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/11.
 */

public class SongAdapter extends AirAdapter<Song> {

    private HeaderSetter headerSetter;

    public SongAdapter(Context context, List<Song> list) {
        super(context, list);
    }

    @Override
    public AirHeadViewHolder onCreateHeadViewHolder(ViewGroup parent) {
        return new SongItemViewTextHeaderHolder(
                getLayoutInflater().inflate(R.layout.recycler_header_text, parent, false));
    }

    @Override
    public AirItemViewHolder onCreateItemViewHolder(ViewGroup parent) {
        return new SongItemViewItemHolder(
                getLayoutInflater().inflate(R.layout.recycler_item_song, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        // when holder is a SongItemViewItemHolder
        if (holder instanceof SongItemViewItemHolder) {
            SongItemViewItemHolder songViewItemHolder = (SongItemViewItemHolder) holder;

            songViewItemHolder.imageView.setImageResource(android.R.drawable.ic_menu_gallery);

            songViewItemHolder.titleText.setText(getList().get(position - 1).getTitle());
            songViewItemHolder.artistText.setText(getList().get(position - 1).getArtist());
            songViewItemHolder.durationText.setText(formatTime(getList().get(position - 1).getDuration()));

            Picasso.with(getContext())
                    .load(getList().get(position - 1).getAlbumArtUri())
                    .into(songViewItemHolder.imageView);
        }

        //when holder is a SongItemViewTextHeaderHolder
        if (holder instanceof SongItemViewTextHeaderHolder) {
            SongItemViewTextHeaderHolder songViewTextHeaderHolder = (SongItemViewTextHeaderHolder) holder;
            if (headerSetter != null) {
                headerSetter.setUpHeader(
                        songViewTextHeaderHolder.headerImage,
                        songViewTextHeaderHolder.mainHeaderText,
                        songViewTextHeaderHolder.secondaryHeaderText
                );
            }
        }

    }

    public void setHeadSetter(HeaderSetter headerSetter) {
        this.headerSetter = headerSetter;
    }

    public class SongItemViewItemHolder extends AirItemViewHolder {
        ImageView imageView;
        TextView titleText;
        TextView artistText;
        TextView durationText;
        Toolbar listItem;

        public SongItemViewItemHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.song_imageView);
            titleText = (TextView) itemView.findViewById(R.id.song_title);
            artistText = (TextView) itemView.findViewById(R.id.song_artist_name);
            durationText = (TextView) itemView.findViewById(R.id.song_duration);
            listItem = (Toolbar) itemView.findViewById(R.id.song_item);
        }
    }

    public class SongItemViewTextHeaderHolder extends AirHeadViewHolder {

        ImageView headerImage;
        TextView mainHeaderText;
        TextView secondaryHeaderText;

        public SongItemViewTextHeaderHolder(View itemView) {
            super(itemView);
            headerImage = (ImageView) itemView.findViewById(R.id.header_image);
            mainHeaderText = (TextView) itemView.findViewById(R.id.header_text_title);
            secondaryHeaderText = (TextView) itemView.findViewById(R.id.header_text_desc);
        }
    }

    public interface HeaderSetter {
        void setUpHeader(ImageView headImage, TextView mainHeadText, TextView secondaryHeadText);
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

