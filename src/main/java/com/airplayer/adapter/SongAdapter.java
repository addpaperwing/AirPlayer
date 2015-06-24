package com.airplayer.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.model.Song;
import com.airplayer.util.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/11.
 * an abstract class, which is used by recycler view that shows a song list
 */

public abstract class SongAdapter extends AirAdapter<Song> {

    private boolean showImage = true;
    private boolean isPlayList = false;

    public void setShowImage(boolean showImage) {
        this.showImage = showImage;
    }

    public SongAdapter(Context context, List<Song> list) {
        super(context, list);
    }

    public SongAdapter(Context context, List<Song> list, boolean isPlayList) {
        this(context, list);
        this.isPlayList = isPlayList;
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
            songViewItemHolder.durationText.setText(Utils.getFormatTime(getList().get(position - 1).getDuration()));

            if (showImage) {
                Picasso.with(getContext())
                        .load(getList().get(position - 1).getAlbumArtUri())
                        .into(songViewItemHolder.imageView);
            } else {
                int track = getList().get(position - 1).getTrack();
                if (track != 0) {
                    songViewItemHolder.trackNum.setText(track % 1000 + "");
                }
            }

            if (isPlayList) {
                AnimationDrawable animation;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    animation = (AnimationDrawable) getContext().getDrawable(R.drawable.animation_equalizer);
                } else {
                    animation = (AnimationDrawable) getContext().getResources()
                            .getDrawable(R.drawable.animation_equalizer);
                }
                songViewItemHolder.playStateImage.setImageDrawable(animation);

                if (getList().get(position - 1).isPlay() ) {
                    if (getList().get(position - 1).isPause()) {
                        songViewItemHolder.playStateImage.setVisibility(View.VISIBLE);
                        if (animation != null) animation.stop();
                    } else {
                        songViewItemHolder.playStateImage.setVisibility(View.VISIBLE);
                        if (animation != null) animation.start();
                    }
                } else {
                    songViewItemHolder.playStateImage.setVisibility(View.INVISIBLE);
                }
            }
        }

        //when holder is a SongHeadViewHolder
        if (holder instanceof AirAdapter.AirHeadViewHolder) {
            AirAdapter.AirHeadViewHolder airHeadViewHolder = (AirAdapter.AirHeadViewHolder) holder;
            setUpViewHolder(airHeadViewHolder);
        }

    }

    public class SongItemViewHolder extends AirItemViewHolder {
        ImageView imageView;
        TextView titleText;
        TextView artistText;
        TextView durationText;
        Toolbar listItem;
        TextView trackNum;
        ImageView playStateImage;

        public SongItemViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.song_imageView);
            titleText = (TextView) itemView.findViewById(R.id.song_title);
            artistText = (TextView) itemView.findViewById(R.id.song_artist_name);
            durationText = (TextView) itemView.findViewById(R.id.song_duration);
            listItem = (Toolbar) itemView.findViewById(R.id.song_item);
            trackNum = (TextView) itemView.findViewById(R.id.song_track_number);
            playStateImage = (ImageView) itemView.findViewById(R.id.playing_state);
        }
    }
}

