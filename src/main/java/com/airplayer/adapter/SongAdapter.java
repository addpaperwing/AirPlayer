package com.airplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
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
 * an abstract class, which is used by recycler view that shows a song list
 */

public abstract class SongAdapter extends AirAdapter<Song> {

    private boolean showImage = true;

    public void setShowImage(boolean showImage) {
        this.showImage = showImage;
    }

    public SongAdapter(Context context, List<Song> list) {
        super(context, list);
    }

    @Override
    public AirItemViewHolder onCreateItemViewHolder(ViewGroup parent) {
        return new SongItemViewHolder(
                getLayoutInflater().inflate(R.layout.recycler_item_song, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        // when holder is a SongItemViewHolder
        if (holder instanceof SongItemViewHolder) {
            SongItemViewHolder songViewItemHolder = (SongItemViewHolder) holder;

            songViewItemHolder.titleText.setText(getList().get(position - 1).getTitle());
            songViewItemHolder.artistText.setText(getList().get(position - 1).getArtist());
            songViewItemHolder.durationText.setText(formatTime(getList().get(position - 1).getDuration()));

            if (showImage) {
                Picasso.with(getContext())
                        .load(getList().get(position - 1).getAlbumArtUri())
                        .into(songViewItemHolder.imageView);
            } else {
                int track = getList().get(position - 1).getTrack();
                songViewItemHolder.trackNum.setText(track % 1000 + "");
            }
        }

        //when holder is a SongHeadViewHolder
        if (holder instanceof SongHeadViewHolder) {
            SongHeadViewHolder songHeadViewHolder = (SongHeadViewHolder) holder;
            setUpViewHolder(songHeadViewHolder);
        }

    }

    /**
     * an abstract method to set up different header
     * @param holder use for setting up the views in header
     */
    public abstract void setUpViewHolder(SongHeadViewHolder holder);

    public class SongItemViewHolder extends AirItemViewHolder {
        ImageView imageView;
        TextView titleText;
        TextView artistText;
        TextView durationText;
        Toolbar listItem;
        TextView trackNum;

        public SongItemViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.song_imageView);
            titleText = (TextView) itemView.findViewById(R.id.song_title);
            artistText = (TextView) itemView.findViewById(R.id.song_artist_name);
            durationText = (TextView) itemView.findViewById(R.id.song_duration);
            listItem = (Toolbar) itemView.findViewById(R.id.song_item);
            trackNum = (TextView) itemView.findViewById(R.id.song_track_number);
        }
    }

    public class SongHeadViewHolder extends AirHeadViewHolder {

        public SongHeadViewHolder(View itemView) {
            super(itemView);
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

